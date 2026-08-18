package ru.bank.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import ru.bank.exception.InvalidSignatureException;
import ru.bank.exception.JwtValidationException;
import ru.bank.exception.TokenExpirationException;
import ru.bank.exception.TokenNotFoundException;

import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;

public class JwtValidator {

    private final PublicKey publicKey;

    public JwtValidator(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            throw new TokenExpirationException("Токен истек: " + ex.getClaims().getExpiration(), ex);
        } catch (SignatureException ex){
            throw new InvalidSignatureException("Токен содержит ошибку в сигнатуре", ex);
        } catch (Exception ex){
            throw new JwtValidationException("Токен невалидный:" + ex.getMessage(), ex);
        }
    }


    /** Извлечение userId из токена
     * */
    public UUID getUserId(String token){
        String userIdStr = validateToken(token).get("userId", String.class);
        if(userIdStr == null){
            throw new TokenNotFoundException("userId отсутствует в токене");
        }
        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException ex){
            throw new JwtValidationException("Кривой формат userId: " + userIdStr, ex);
        }
    }

    /** Извлечение userStatus из токена
     * */
    public String getUserStatusFromToken(String token){
        String statusStr = validateToken(token).get("status", String.class);
        if(statusStr == null){
            throw new JwtValidationException("Невалидный JWT, проблема с извлечением status");
        }
        return statusStr;
    }

    /** Извлечение Role из токена
     * */
    public String getRoleFromToken(String token){
        String roleStr = validateToken(token).get("role", String.class);
        if(roleStr == null){
            throw new JwtValidationException("Невалидный JWT, проблема с извлечением role");
        }
        return roleStr;
    }

    /** Извлечение email из токена
     * */
    public String getEmailFromToken(String token){
        String email = validateToken(token).getSubject();
        if(email == null){
            throw new JwtValidationException("Невалидный JWT, проблема с извлечением Email");
        }
        return email;
    }


    /** Извлечение остатка времени из токена
     * */
    public Long getExpirationFromToken(String token){
        Date expiration = validateToken(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    /** Извлечение sessionId из токена
     * */
    public String getSessionIdFromToken(String token){
        String sessionId = validateToken(token).get("sessionId", String.class);
        if(sessionId == null){
            throw new JwtValidationException("Невалидный JWT, проблема с извлечением sessionId");
        }
        return sessionId;
    }

    /** Boolean Метод для проверки валидности токена
     * */
    public boolean isValidToken(String token){
        try {
            validateToken(token);
            return true;
        } catch (Exception ex){
            return false;
        }
    }

    /** Обратный метод, для выявления невалидного токена
     * */
    public boolean isInvalidToken(String token){
        return !isValidToken(token);
    }




}
