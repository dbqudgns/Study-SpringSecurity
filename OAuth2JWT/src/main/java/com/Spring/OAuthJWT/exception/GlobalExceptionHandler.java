package com.Spring.OAuthJWT.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResult> noData(IllegalArgumentException ex) {

        ErrorResult errorResult = ErrorResult.builder()
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

}
