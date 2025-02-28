package com.Spring.OAuthJWT.exception;

import lombok.Builder;

@Builder
public record ErrorResult(
        String message
) {
}
