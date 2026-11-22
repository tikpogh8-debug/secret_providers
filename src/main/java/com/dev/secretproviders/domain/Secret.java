package com.dev.secretproviders.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A secret that can be read exactly once. Only the hash of the access token
 * is stored, so a database leak does not reveal working links.
 */
@Entity
@Table(name = "secrets")
public class Secret {

    public static final int MAX_CONTENT_LENGTH = 10_000;

    @Id
    @Column(name = "token_hash", length = 64, nullable = false, updatable = false)
    private String tokenHash;

    @Column(length = MAX_CONTENT_LENGTH, nullable = false, updatable = false)
    private String content;

    @Column(name = "sender_email", nullable = false, updatable = false)
    private String senderEmail;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Secret() {
    }

    public Secret(String tokenHash, String content, String senderEmail, Instant createdAt) {
        this.tokenHash = tokenHash;
        this.content = content;
        this.senderEmail = senderEmail;
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

}
