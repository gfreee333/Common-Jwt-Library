package ru.bank.jwt;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class JwtKeyLoader {

    private JwtKeyLoader(){}


    private static PublicKey parsePublicKey(String keyContent) throws Exception{
        String cleaned = keyContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(cleaned);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    public static PublicKey loadFromClasspath(String path){
        try(InputStream is = JwtKeyLoader.class.getClassLoader().getResourceAsStream(path)) {
            if(is == null){
                throw new IllegalArgumentException("Публичный ключ не найден в ClassPath:" + path);
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return parsePublicKey(content);
        } catch (Exception ex){
            throw new RuntimeException("Не удалось загрузить публичный ключ: " + path, ex);
        }
    }

    public static PublicKey loadFromPem(String pem){
        try {
            return parsePublicKey(pem);
        } catch (Exception ex){
            throw new RuntimeException("Не удалось распарсить публичный ключ", ex);
        }
    }




}
