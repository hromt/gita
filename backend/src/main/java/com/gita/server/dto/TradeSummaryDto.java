package com.gita.server.dto;

import java.util.List;

public record TradeSummaryDto(
        long id,
        long senderId,
        long receiverId,
        String status,
        String otherUsername,
        boolean incoming,
        List<Long> offeredItemIds,
        List<Long> requestedItemIds
) {
}
