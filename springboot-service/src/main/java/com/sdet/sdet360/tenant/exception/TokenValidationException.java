package com.sdet.sdet360.tenant.exception;

public class TokenValidationException extends Exception {

    public TokenValidationException(String message) {
        super(message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
