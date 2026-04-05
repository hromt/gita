package com.gita.app.data.local.model;

import com.gita.app.data.local.entity.Trade;

/**
 * UI model for trade lists: core trade row plus resolved party name and item id lists.
 */
public class TradeSummary {

    public final Trade trade;
    public final String otherPartyUsername;
    public final long[] offeredItemIds;
    public final long[] requestedItemIds;

    public TradeSummary(Trade trade, String otherPartyUsername, long[] offeredItemIds, long[] requestedItemIds) {
        this.trade = trade;
        this.otherPartyUsername = otherPartyUsername;
        this.offeredItemIds = offeredItemIds;
        this.requestedItemIds = requestedItemIds;
    }
}
