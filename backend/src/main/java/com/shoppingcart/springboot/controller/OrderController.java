/**
 * @author DUGUANYU
 * Generated by script
 */

package com.shoppingcart.springboot.controller;

import com.shoppingcart.springboot.model.Order;
import com.shoppingcart.springboot.model.ShoppingCart;
import com.shoppingcart.springboot.model.User;
import com.shoppingcart.springboot.service.OrderService;
import com.shoppingcart.springboot.service.ShoppingCartService;
import com.shoppingcart.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OrderController {


    @Autowired
    private OrderService orderService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    // Place an order (Checkout)
    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@RequestParam Long customerId) {
        // Get shopping cart by customer ID
        ShoppingCart cart = shoppingCartService.getCartByCustomerId(customerId);
        // Convert cart to order
        Order order = orderService.createOrderFromCart(cart);
        // Clear the cart
        shoppingCartService.clearCart(customerId);
        return ResponseEntity.ok(order);
    }

    // View purchase history
    @GetMapping("/purchase-history")
    public ResponseEntity<List<Order>> browsePurchaseHistory(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        Long customerId = user.getCustomer().getCustomerId();
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    // Get order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }


//>>>>>>> 2cc64b3a417ace2fc10dac07b6de34bac44ee772
}