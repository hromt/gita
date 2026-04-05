package com.gita.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemWithOwnerDto(
        Long id,
        Long ownerId,
        String ownerUsername,
        String name,
        String description,
        String category,
        String subcategory,
        double estimatedValue,
        @JsonProperty("isLookingToTrade") boolean lookingToTrade,
        String imageUri
) {
}
