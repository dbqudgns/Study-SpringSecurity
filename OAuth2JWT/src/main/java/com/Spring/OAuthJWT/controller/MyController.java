package com.Spring.OAuthJWT.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MyController {

    @GetMapping("/my")
    public ResponseEntity<?> myAPI() {
        // JSON 형태로 {"message": "Hello World"} 를 반환합니다.
        return ResponseEntity.ok(Map.of("message", "Hello World"));
    }
}
