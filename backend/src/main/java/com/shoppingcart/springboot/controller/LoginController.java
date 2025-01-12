/**
 * @author MAMINGYANG
 * Generated by script
 */

package com.shoppingcart.springboot.controller;

import com.shoppingcart.springboot.model.User;
import com.shoppingcart.springboot.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {

        try {
            userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + ex.getMessage() + "\"}");
        }
    }

    // Authenticate a user (Login)
    @PostMapping("/")
    public ResponseEntity<?> authenticateUser(@RequestParam String email, @RequestParam String password,
                                              HttpSession session) {
        try {
            User user = userService.authenticate(email, password);
            session.setAttribute("user_id", user.getUserId());
            return ResponseEntity.ok(user);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


    @GetMapping("/guest")
    public ResponseEntity<?> guestLogin(HttpSession session) {
        try {
            session.setAttribute("user_id", 2);
            System.out.println(session.getAttribute("user_id"));
            return ResponseEntity.ok("Guest logged in successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }




    // Request password reset by email
//    @PostMapping("/reset-password")
//    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
//        try {
//            System.out.println(email);
//            userService.requestPasswordReset(email);
//            return ResponseEntity.ok("Password reset link has been sent to your email");
//        } catch (RuntimeException ex) {
//            return ResponseEntity.badRequest().body(ex.getMessage());
//        }
//    }



}
