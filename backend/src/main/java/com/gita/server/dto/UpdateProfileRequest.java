package com.gita.server.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 2048) String collectionInterest,
        @Size(max = 2048) String wishlist,
        @Size(max = 512) String contactInfo
) {
}
