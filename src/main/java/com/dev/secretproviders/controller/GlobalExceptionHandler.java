package com.dev.secretproviders.controller;

import com.dev.secretproviders.service.SecretNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SecretNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleSecretNotFound() {
        return "secret-not-found";
    }

}
