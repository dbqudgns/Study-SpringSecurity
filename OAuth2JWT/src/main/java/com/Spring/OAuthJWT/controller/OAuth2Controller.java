package com.Spring.OAuthJWT.controller;

import com.Spring.OAuthJWT.service.OAuth2AccessHeaderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2AccessHeaderService oAuth2AccessHeaderService;

    @PostMapping("/change-to-header")
    public ResponseEntity<?> AccessToHeader(HttpServletRequest request, HttpServletResponse response) {
        return oAuth2AccessHeaderService.oauth2AccessHeader(request, response);
    }

}
