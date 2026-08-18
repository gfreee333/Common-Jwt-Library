package ru.bank.exception;

public class TokenNotFoundException extends JwtValidationException{
    public TokenNotFoundException(String message) {
        super(message);
    }
}
