package com.gita.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemDto(
        Long id,
        Long ownerId,
        String name,
        String description,
        String category,
        String subcategory,
        double estimatedValue,
        @JsonProperty("isLookingToTrade") boolean lookingToTrade,
        String imageUri
) {
}
