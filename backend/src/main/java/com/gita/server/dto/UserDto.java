package com.gita.server.dto;

public record UserDto(
        long id,
        String username,
        String collectionInterest,
        String wishlist,
        String contactInfo
) {
}
