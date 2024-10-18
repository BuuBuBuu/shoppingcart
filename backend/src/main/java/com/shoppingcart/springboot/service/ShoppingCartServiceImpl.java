/**
 * @author HUANGZEYUAN
 * Generated by script
 */

package com.shoppingcart.springboot.service;

import com.shoppingcart.springboot.model.*;
import com.shoppingcart.springboot.repository.*;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {


    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartServiceImpl.class);

    // 仓库依赖
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartProductRepository shoppingCartProductRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final VoucherRepository voucherRepository;

    @Autowired
    public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepository,
                                   ShoppingCartProductRepository shoppingCartProductRepository,
                                   OrderRepository orderRepository,
                                   OrderDetailRepository orderDetailRepository,
                                   VoucherRepository voucherRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartProductRepository = shoppingCartProductRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.voucherRepository = voucherRepository;
    }

    // ========================= 购物车操作相关方法 =========================

    @Override
    public ShoppingCart getShoppingCartByCustomer(Customer customer) {
        return shoppingCartRepository.findByCustomer(customer)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found for customer ID: " + customer.getCustomerId()));
    }

    @Override
    public ShoppingCart addProductToCart(Customer customer, Product product, int quantity) {
        ShoppingCart shoppingCart = getShoppingCartByCustomer(customer);
        Optional<ShoppingCartProduct> existingProduct = shoppingCart.getShoppingCartProducts().stream()
                .filter(cartProduct -> cartProduct.getProduct().equals(product))
                .findFirst();

        if (existingProduct.isPresent()) {
            ShoppingCartProduct shoppingCartProduct = existingProduct.get();
            shoppingCartProduct.setQuantity(shoppingCartProduct.getQuantity() + quantity);
        } else {
            ShoppingCartProduct shoppingCartProduct = new ShoppingCartProduct();
            shoppingCartProduct.setProduct(product);
            shoppingCartProduct.setQuantity(quantity);
            shoppingCartProduct.setShoppingCart(shoppingCart);
            shoppingCartProductRepository.save(shoppingCartProduct);
            shoppingCart.getShoppingCartProducts().add(shoppingCartProduct);
        }

        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCart removeProductFromCart(Customer customer, Product product, int quantity) {
        ShoppingCart shoppingCart = getShoppingCartByCustomer(customer);

        // 使用Iterator来避免ConcurrentModificationException
        Iterator<ShoppingCartProduct> iterator = shoppingCart.getShoppingCartProducts().iterator();

        while (iterator.hasNext()) {
            ShoppingCartProduct shoppingCartProduct = iterator.next();
            if (shoppingCartProduct.getProduct().equals(product)) {
                int updatedQuantity = shoppingCartProduct.getQuantity() - quantity;
                if (updatedQuantity > 0) {
                    shoppingCartProduct.setQuantity(updatedQuantity);
                } else {
                    // 使用Iterator的remove方法移除元素，确保安全
                    iterator.remove();
                }
            }
        }

        return shoppingCartRepository.save(shoppingCart);
    }


    // ========================= 订单操作相关方法 =========================

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public Order createOrderFromCart(Customer customer) {
        ShoppingCart shoppingCart = getShoppingCartByCustomer(customer);
        Order newOrder = new Order();
        newOrder.setCustomer(customer);
        newOrder.setOrderDate(new Date());
        newOrder.setStatus("UNPAID");

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCartProduct cartProduct : shoppingCart.getShoppingCartProducts()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(cartProduct.getProduct());
            orderDetail.setQuantity(cartProduct.getQuantity());
            orderDetail.setOrder(newOrder);
            orderDetails.add(orderDetail);
        }

        newOrder.setOrderDetails(orderDetails);
        orderRepository.save(newOrder);
        orderDetailRepository.saveAll(orderDetails);


        logger.info("Order created successfully for customer ID: {}", customer.getCustomerId());
        return newOrder;
    }


    @Override
    public void completePayment(Order order, String voucherCode) {
        if (order.getStatus().equalsIgnoreCase("PAID")) {
            throw new IllegalStateException("Order is already paid.");
        }
        applyVoucherIfAvailable(order, voucherCode);
        order.setStatus("PAID");
        orderRepository.save(order);
        logger.info("Order payment completed successfully for order ID: {}", order.getOrderId());
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public double applyVoucherToOrder(Order order, String voucherCode) {
        // 如果代金券代码为空，则直接返回当前订单的最终价格
        if (voucherCode == null || voucherCode.isEmpty()) {
            System.out.println("No voucher applied, returning original price: " + order.getFinalPrice());
            return order.getFinalPrice();
        }

        // 查找并应用代金券
        applyVoucherIfAvailable(order, voucherCode);

        // 返回更新后的订单最终价格
        System.out.println("Final price after applying voucher: " + order.getFinalPrice());
        return order.getFinalPrice();
    }

    private void applyVoucherIfAvailable(Order order, String voucherCode) {
        // 查找代金券
        Optional<Voucher> voucherOpt = voucherRepository.findByVoucherCode(voucherCode);

        voucherOpt.ifPresentOrElse(voucher -> {
            // 检查代金券是否有剩余数量
            if (voucher.getRemainingQuantity() > 0) {
                double discount = voucher.getVoucherDiscount();
                System.out.println("Applying voucher. Discount: " + discount);

                // 计算折扣后的价格，确保价格不低于 0
                double discountedPrice = order.getFinalPrice() - order.getFinalPrice()*discount;
                order.setFinalPrice(Math.max(discountedPrice, 0));

                // 更新代金券的剩余数量
                voucher.setRemainingQuantity(voucher.getRemainingQuantity() - 1);
                voucherRepository.save(voucher);
                orderRepository.save(order);
            } else {
                System.out.println("Voucher has no remaining quantity.");
            }
        }, () -> {
            System.out.println("Voucher code not found.");
 });
}


  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Override
  public void addProductToCart(Long customerId, Long productId, int quantity) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new RuntimeException("Customer not found"));
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Product not found"));

    ShoppingCart cart = customer.getShoppingCart();
    if (cart == null) {
      cart = new ShoppingCart();
      cart.setCustomer(customer);
      customer.setShoppingCart(cart);
      shoppingCartRepository.save(cart);
    }

    Optional<ShoppingCartProduct> cartProductOptional = shoppingCartProductRepository
        .findByShoppingCartShoppingCartIdAndProductProductId(cart.getShoppingCartId(), productId);

    ShoppingCartProduct cartProduct;
    if (cartProductOptional.isPresent()) {
      cartProduct = cartProductOptional.get();
      cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
    } else {
      cartProduct = new ShoppingCartProduct();
      cartProduct.setShoppingCart(cart);
      cartProduct.setProduct(product);
      cartProduct.setQuantity(quantity);
    }
    shoppingCartProductRepository.save(cartProduct);
  }

  @Override
  public void removeProductFromCart(Long customerId, Long productId) {
    ShoppingCart cart = getCartByCustomerId(customerId);
    shoppingCartProductRepository.deleteByShoppingCartShoppingCartIdAndProductProductId(
        cart.getShoppingCartId(), productId);
  }

  @Override
  public ShoppingCart getCartByCustomerId(Long customerId) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new RuntimeException("Customer not found"));
    ShoppingCart cart = customer.getShoppingCart();
    if (cart == null) {
      cart = new ShoppingCart();
      cart.setCustomer(customer);
      customer.setShoppingCart(cart);
      shoppingCartRepository.save(cart);
    }
    return cart;
  }

  @Override
  public void clearCart(Long customerId) {
    ShoppingCart cart = getCartByCustomerId(customerId);
    shoppingCartProductRepository.deleteByShoppingCartShoppingCartId(cart.getShoppingCartId());
  }

  @Override
  public List<ShoppingCartProduct> getCartProducts(Long customerId) {
    ShoppingCart cart = getCartByCustomerId(customerId);
    return cart.getShoppingCartProducts();
  }

    @Override
    public void deleteOrder(Order order) {
        order.setStatus("DELETED");
        orderRepository.save(order);  // 保存订单状态更改
        logger.info("Order ID: {} has been marked as DELETED.", order.getOrderId());
    }

}
