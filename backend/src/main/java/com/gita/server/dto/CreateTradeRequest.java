package com.gita.server.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateTradeRequest(
        @NotNull Long receiverId,
        @NotEmpty List<Long> offeredItemIds,
        @NotEmpty List<Long> requestedItemIds
) {
}
