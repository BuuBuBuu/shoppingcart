/**
 * @author ZENGXING
 * Generated by script
 */

package com.shoppingcart.springboot.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // 允许带凭证的请求
@RestController
@RequestMapping("/session")
public class SessionController {

    @GetMapping("/getSessionData")
    public ResponseEntity<?> getSessionData(HttpSession session) {
        // 获取会话中的属性
        Object someData = session.getAttribute("user_id");

        // 构建返回数据
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("user_id", someData);

        return ResponseEntity.ok(sessionData);
    }
}
