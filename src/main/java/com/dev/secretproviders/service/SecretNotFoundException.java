package com.dev.secretproviders.service;

public class SecretNotFoundException extends RuntimeException {

    public SecretNotFoundException() {
        super("Secret not found or already viewed");
    }

}
