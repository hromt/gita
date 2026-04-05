package com.gita.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ItemUpsertRequest(
        @NotBlank @Size(max = 512) String name,
        @Size(max = 4096) String description,
        @Size(max = 256) String category,
        @Size(max = 256) String subcategory,
        double estimatedValue,
        @JsonProperty("isLookingToTrade") Boolean lookingToTrade,
        @Size(max = 2048) String imageUri
) {
}
