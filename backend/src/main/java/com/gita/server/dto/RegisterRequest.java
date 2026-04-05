package com.gita.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 128) String username,
        @NotBlank @Size(max = 256) String password,
        @Size(max = 2048) String collectionInterest,
        @Size(max = 2048) String wishlist,
        @Size(max = 512) String contactInfo
) {
}
