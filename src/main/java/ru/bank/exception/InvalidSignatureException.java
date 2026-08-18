package ru.bank.exception;

public class InvalidSignatureException extends JwtValidationException{
    public InvalidSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
