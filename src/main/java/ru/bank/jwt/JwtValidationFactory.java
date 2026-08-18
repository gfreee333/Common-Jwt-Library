package ru.bank.jwt;

import java.security.PublicKey;

public class JwtValidationFactory {

    private JwtValidationFactory() {}

    public static JwtValidator fromClasspath(String path){
        PublicKey publicKey = JwtKeyLoader.loadFromClasspath(path);
        return new JwtValidator(publicKey);
    }

    public static JwtValidator fromPublicKey(PublicKey publicKey){
        return new JwtValidator(publicKey);
    }

}
