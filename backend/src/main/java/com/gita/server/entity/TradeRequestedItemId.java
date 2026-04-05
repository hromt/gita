package com.gita.server.entity;

import java.io.Serializable;
import java.util.Objects;

public class TradeRequestedItemId implements Serializable {

    private Long tradeId;
    private Long itemId;

    public TradeRequestedItemId() {
    }

    public TradeRequestedItemId(Long tradeId, Long itemId) {
        this.tradeId = tradeId;
        this.itemId = itemId;
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TradeRequestedItemId that = (TradeRequestedItemId) o;
        return Objects.equals(tradeId, that.tradeId) && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeId, itemId);
    }
}
