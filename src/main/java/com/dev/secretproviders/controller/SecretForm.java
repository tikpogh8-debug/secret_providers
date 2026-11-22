package com.dev.secretproviders.controller;

import com.dev.secretproviders.domain.Secret;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SecretForm {

    @NotBlank(message = "Secret must not be empty")
    @Size(max = Secret.MAX_CONTENT_LENGTH, message = "Secret is too long")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
