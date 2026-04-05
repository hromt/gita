package com.gita.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gita.jwt")
public class JwtProperties {

    /**
     * HMAC secret; use env GITA_JWT_SECRET in production (32+ bytes recommended).
     */
    private String secret = "ChangeThisToALongRandomSecretKeyForProductionUseAtLeast256Bits";

    private long expirationMs = 604800000L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }
}
