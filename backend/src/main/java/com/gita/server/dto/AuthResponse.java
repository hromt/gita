package com.gita.server.dto;

public record AuthResponse(String token, long userId, String username) {
}
