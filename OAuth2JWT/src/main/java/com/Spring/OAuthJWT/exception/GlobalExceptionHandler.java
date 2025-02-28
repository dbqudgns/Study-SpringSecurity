package com.Spring.OAuthJWT.exception;

import com.Spring.OAuthJWT.controller.OAuth2Controller;
import com.Spring.OAuthJWT.controller.ReissueController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {OAuth2Controller.class, ReissueController.class})
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResult> noData(IllegalArgumentException ex) {

        ErrorResult errorResult = ErrorResult.builder()
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

}
