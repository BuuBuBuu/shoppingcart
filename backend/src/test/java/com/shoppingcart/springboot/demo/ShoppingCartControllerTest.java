package com.shoppingcart.springboot.demo;

import com.shoppingcart.springboot.controller.ShoppingCartController;
import com.shoppingcart.springboot.model.*;
import com.shoppingcart.springboot.service.ShoppingCartService;
import com.shoppingcart.springboot.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShoppingCartController.class)
public class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingCartService shoppingCartService;

    @MockBean
    private UserService userService;

    // 测试查看购物车接口
    @Test
    public void testViewShoppingCart() throws Exception {
        User mockUser = new User();
        Customer mockCustomer = new Customer();
        mockCustomer.setCustomerId(1L);
        mockUser.setCustomer(mockCustomer);
        ShoppingCart mockCart = new ShoppingCart();

        // Mock 行为
        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(shoppingCartService.getShoppingCartByCustomer(any(Customer.class))).thenReturn(mockCart);

        // 发起 GET 请求
        mockMvc.perform(get("/shoppingCart/view-cart?userId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // 测试添加商品到购物车接口
    @Test
    public void testAddProductToCart() throws Exception {
        User mockUser = new User();
        Customer mockCustomer = new Customer();
        mockCustomer.setCustomerId(1L);
        mockUser.setCustomer(mockCustomer);
        Product mockProduct = new Product();
        mockProduct.setProductId(2L);

        // Mock 行为
        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(shoppingCartService.addProductToCart(any(Customer.class), any(Product.class), eq(5)))
                .thenReturn(new ShoppingCart());

        // 发起 POST 请求
        mockMvc.perform(post("/shoppingCart/add-product?userId=1&productId=2&quantity=5"))
                .andExpect(status().isOk());
    }

    // 测试创建订单接口
    @Test
    public void testCreateOrder() throws Exception {
        User mockUser = new User();
        Customer mockCustomer = new Customer();
        mockCustomer.setCustomerId(1L);
        mockUser.setCustomer(mockCustomer);
        Order mockOrder = new Order();
        mockOrder.setOrderId(1L);

        // Mock 行为
        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(shoppingCartService.createOrderFromCart(any(Customer.class))).thenReturn(mockOrder);
        Mockito.when(shoppingCartService.applyVoucherToOrder(any(Order.class), any(String.class))).thenReturn(100.0);

        // 发起 POST 请求
        mockMvc.perform(post("/shoppingCart/create-order")
                        .param("userId", "1")
                        .param("productIds", "1,2")
                        .param("quantities", "3,2")
                        .param("voucherCode", "DISCOUNT2024"))
                .andExpect(status().isOk())
                .andExpect(content().string("100.0"));
    }

    // 测试删除订单接口
    @Test
    public void testDeleteOrder() throws Exception {
        Order mockOrder = new Order();
        mockOrder.setOrderId(1L);
        mockOrder.setStatus("UNPAID");

        // Mock 行为
        Mockito.when(shoppingCartService.findOrderById(1L)).thenReturn(Optional.of(mockOrder));

        // 发起 POST 请求
        mockMvc.perform(post("/shoppingCart/delete-order?orderId=1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order deleted successfully."));
    }

    // 测试完成订单支付接口
    @Test
    public void testCompletePayment() throws Exception {
        Order mockOrder = new Order();
        mockOrder.setOrderId(1L);
        mockOrder.setStatus("UNPAID");

        // Mock 行为
        Mockito.when(shoppingCartService.findOrderById(1L)).thenReturn(Optional.of(mockOrder));

        // 发起 POST 请求
        mockMvc.perform(post("/shoppingCart/complete-payment?orderId=1&voucherCode=DISCOUNT2024"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order payment completed successfully."));
    }
}

