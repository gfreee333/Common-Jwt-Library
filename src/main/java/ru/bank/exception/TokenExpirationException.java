package ru.bank.exception;

public class TokenExpirationException extends JwtValidationException{
    public TokenExpirationException(String message, Throwable cause) {
        super(message, cause);
    }
}
