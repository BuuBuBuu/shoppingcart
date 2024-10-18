/**
 * @author HUANGZEYUAN
 * Generated by script
 */

package com.shoppingcart.springboot.controller;

import com.shoppingcart.springboot.model.*;
import com.shoppingcart.springboot.repository.CustomerRepository;
import com.shoppingcart.springboot.repository.ProductRepository;
import com.shoppingcart.springboot.service.ShoppingCartService;
import com.shoppingcart.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ShoppingCartController {

    private final ShoppingCartService shoppingService;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final UserService userService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService,
                                  ProductRepository productRepository,
                                  CustomerRepository customerRepository,
                                  UserService userService) {
        this.shoppingService = shoppingCartService;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.userService = userService;
    }

    // ========================= 购物车相关接口 =========================

    /**
     * 查看购物车接口
     * 完整路径：GET http://localhost:8080/shoppingCart/view-cart?userId=1
     * 根据用户ID获取该用户的购物车内容
     *
     * @param userId 用户ID
     * @return 购物车详情
     */
    @GetMapping("/view-cart")
    public ResponseEntity<ShoppingCart> viewShoppingCart(@RequestParam Long userId) {
        try {
            User user = userService.getUserById(userId);
            Long customerId = user.getCustomer().getCustomerId();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            ShoppingCart shoppingCart = shoppingService.getShoppingCartByCustomer(customer);
            return ResponseEntity.ok(shoppingCart);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * 添加商品到购物车接口
     * 完整路径：POST http://localhost:8080/shoppingCart/add-product
     * 根据用户ID和产品ID将商品添加到购物车
     *
     * @param userId    用户ID
     * @param productId 产品ID
     * @param quantity  添加的数量
     * @return 购物车更新状态
     */
    @RequestMapping(value = "/add-product", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> addProductToCart(@RequestParam Long userId,
                                              @RequestParam Long productId,
                                              @RequestParam int quantity) {
        try {
            User user = userService.getUserById(userId);
            Long customerId = user.getCustomer().getCustomerId();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStoreQuantity() < quantity) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("库存不足");
            }

            shoppingService.addProductToCart(customer, product, quantity);
            return ResponseEntity.ok("商品已添加到购物车");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("添加商品失败");
        }
    }

    /**
     * 从购物车中移除指定商品接口
     * 完整路径：POST http://localhost:8080/shoppingCart/remove-product
     * 根据用户ID和产品ID移除购物车中的商品
     *
     * @param userId    用户ID
     * @param productId 产品ID
     * @param quantity  移除的数量
     * @return 购物车更新状态
     */
    @RequestMapping(value = "/remove-product", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<ShoppingCart> removeProductFromCart(@RequestParam Long userId,
                                                              @RequestParam Long productId,
                                                              @RequestParam int quantity) {
        try {
            User user = userService.getUserById(userId);
            Long customerId = user.getCustomer().getCustomerId();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            ShoppingCart shoppingCart = shoppingService.removeProductFromCart(customer, product, quantity);
            return ResponseEntity.ok(shoppingCart);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * 清空购物车接口
     * 完整路径：POST http://localhost:8080/shoppingCart/clear-cart
     * 根据用户ID清空其购物车
     *
     * @param userId 用户ID
     * @return 清空购物车确认信息
     */
    @RequestMapping(value = "/clear-cart", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> clearCart(@RequestParam Long userId) {
        try {
            User user = userService.getUserById(userId);
            Long customerId = user.getCustomer().getCustomerId();
            shoppingService.clearCart(customerId);
            return ResponseEntity.ok("Shopping cart has been emptied.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to empty the shopping cart.");
        }
    }

    // ========================= 订单相关接口 =========================

    /**
     * 创建订单接口
     * 完整路径：POST http://localhost:8080/shoppingCart/create-order
     * 输入用户ID、产品ID列表和数量，创建订单后从购物车删除相应的产品，并返回最终价格
     *
     * @param userId      用户ID
     * @param productIds  产品ID列表
     * @param quantities  对应产品的数量列表
     * @param voucherCode 可选的代金券代码
     * @return 订单的最终价格
     */
    @RequestMapping(value = "/create-order", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<Double> createOrder(@RequestParam Long userId,
                                              @RequestParam List<Long> productIds,
                                              @RequestParam List<Integer> quantities,
                                              @RequestParam(required = false) String voucherCode) {
        try {
            // 使用 userId 获取 User 和 Customer
            User user = userService.getUserById(userId);
            Long customerId = user.getCustomer().getCustomerId();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            // 创建订单，并计算订单总价
            Order order = shoppingService.createOrderFromCart(customer);

            double totalPrice = 0.0;  // 声明总价变量

            // 从购物车中移除指定产品，同时计算总价
            for (int i = 0; i < productIds.size(); i++) {
                Long productId = productIds.get(i);
                int quantity = quantities.get(i);
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                // 累加每个产品的总价
                totalPrice += product.getPrice() * quantity;

                // 从购物车中移除商品
                shoppingService.removeProductFromCart(customer, product, quantity);
            }

            // 将总价设置到订单中
            order.setFinalPrice(totalPrice); // [修改部分] 先计算购物车的总价，然后再保存到订单中

            // 计算最终价格，应用代金券（如有）
            double finalPrice = shoppingService.applyVoucherToOrder(order, voucherCode);

            return ResponseEntity.ok(finalPrice);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 完成订单支付接口
     * 完整路径：POST http://localhost:8080/shoppingCart/complete-payment
     * 支付订单，支持使用代金券
     *
     * @param orderId     订单ID
     * @param voucherCode 可选的代金券代码
     * @return 支付完成状态
     */
    @RequestMapping(value = "/complete-payment", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> completePayment(@RequestParam Long orderId,
                                                  @RequestParam(required = false) String voucherCode) {
        try {
            Order order = shoppingService.findOrderById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if ("PAID".equalsIgnoreCase(order.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The order has been paid.");
            }

            // 应用代金券（如果有）
            if (voucherCode != null && !voucherCode.isEmpty()) {
                shoppingService.applyVoucherToOrder(order, voucherCode);
            }

            shoppingService.completePayment(order, voucherCode);
            return ResponseEntity.ok("Order payment successful.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during payment processing.");
        }
    }

    /**
     * 删除订单接口
     * 完整路径：POST http://localhost:8080/shoppingCart/delete-order
     * 删除未支付的订单，将订单状态设置为 DELETED
     *
     * @param orderId 订单ID
     * @return 删除订单确认信息
     */
    @RequestMapping(value = "/delete-order", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> deleteOrder(@RequestParam Long orderId) {
        try {
            Order order = shoppingService.findOrderById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if ("UNPAID".equalsIgnoreCase(order.getStatus())) {
                // 将订单状态改为 DELETED
                shoppingService.deleteOrder(order);
                return ResponseEntity.ok("Order has been deleted.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only unpaid orders can be deleted.");
            }
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        }
    }
}
