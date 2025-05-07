package com.sdet.sdet360.tenant.exception;


/**
 * Exception thrown when token generation fails
 */
public class TokenGenerationException extends Exception {

    public TokenGenerationException(String message) {
        super(message);
    }

    public TokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
