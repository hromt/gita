package com.gita.server.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Public endpoints so you can verify the server is running without a JWT.
 * Other {@code /api/**} routes (except {@code /api/auth/**}) require a Bearer token.
 */
@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of(
                "service", "gita-server",
                "status", "up",
                "hint", "POST /api/auth/register or /api/auth/login; then header Authorization: Bearer YOUR_TOKEN for protected routes"
        );
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "up");
    }
}
