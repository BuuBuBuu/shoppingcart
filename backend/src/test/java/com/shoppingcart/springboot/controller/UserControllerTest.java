//package com.shoppingcart.springboot.controller;
//
//import com.shoppingcart.springboot.model.User;
//import com.shoppingcart.springboot.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Date;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class UserControllerTest {
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private UserController userController;
//
//    private User testUser;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // 初始化测试用户
//        testUser = new User();
//        testUser.setUserId(1L);
//        testUser.setEmail("test@example.com");
//        testUser.setPassword("test1234");
//        testUser.setUserName("testUser");
//        testUser.setFirstName("Test");
//        testUser.setLastName("User");
//        testUser.setLastLogin(new Date());
//        testUser.setActive(true);
//        testUser.setStaff(false);
//    }
//
////    @Test
////    void testRegisterUser() {
////        // 模拟 userService 行为
////        when(userService.registerUser(testUser)).thenReturn(true);
////
////        // 执行 registerUser 方法
//////        ResponseEntity<String> response = userController.registerUser(testUser);
////
////        // 验证返回结果
////        assertEquals("User registered successfully", response.getBody());
////        assertEquals(200, response.getStatusCodeValue());
////
////        System.out.println("registerUser 测试成功");
////    }
//
//    @Test
//    void testAuthenticateUser() {
//        // 模拟 userService 行为
//        when(userService.authenticate("test@example.com", "test1234")).thenReturn(testUser);
//
//        // 执行 authenticateUser 方法
//        ResponseEntity<?> response = userController.authenticateUser("test@example.com", "test1234");
//
//        // 验证返回结果
//        assertEquals(testUser, response.getBody());
//        assertEquals(200, response.getStatusCodeValue());
//
//        System.out.println("authenticateUser 测试成功");
//    }
//
//    @Test
//    void testChangePassword() {
//        // 模拟 userService 行为
//        when(userService.changePassword("test@example.com", "oldPassword", "newPassword")).thenReturn(true);
//
//        // 执行 changePassword 方法
//        ResponseEntity<String> response = userController.changePassword("test@example.com", "oldPassword", "newPassword");
//
//        // 验证返回结果
//        assertEquals("Password changed successfully", response.getBody());
//        assertEquals(200, response.getStatusCodeValue());
//
//        System.out.println("changePassword 测试成功");
//    }
//
//    @Test
//    void testRequestPasswordReset() {
//        // 模拟 userService 行为
//        doNothing().when(userService).requestPasswordReset("test@example.com");
//
//        // 执行 requestPasswordReset 方法
//        ResponseEntity<String> response = userController.requestPasswordReset("test@example.com");
//
//        // 验证返回结果
//        assertEquals("Password reset link has been sent to your email", response.getBody());
//        assertEquals(200, response.getStatusCodeValue());
//
//        System.out.println("requestPasswordReset 测试成功");
//    }
//
//    @Test
//    void testLogoutUser() {
//        // 模拟 userService 行为
//        doNothing().when(userService).logout(1L);
//
//        // 执行 logoutUser 方法
//        ResponseEntity<String> response = userController.logoutUser(1L);
//
//        // 验证返回结果
//        assertEquals("User logged out successfully", response.getBody());
//        assertEquals(200, response.getStatusCodeValue());
//
//        System.out.println("logoutUser 测试成功");
//    }
//
//    @Test
//    void testGetUserById() {
//        // 模拟 userService 行为
//        when(userService.getUserById(1L)).thenReturn(testUser);
//
//        // 执行 getUserById 方法
//        ResponseEntity<?> response = userController.getUserById(1L);
//
//        // 验证返回结果
//        assertEquals(testUser, response.getBody());
//        assertEquals(200, response.getStatusCodeValue());
//
//        System.out.println("getUserById 测试成功");
//    }
//}
