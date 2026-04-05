package com.gita.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "trade_requested_items", indexes = {
        @Index(columnList = "tradeId"),
        @Index(columnList = "itemId")
})
@IdClass(TradeRequestedItemId.class)
public class TradeRequestedItem {

    @Id
    private Long tradeId;

    @Id
    private Long itemId;

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
}
