package com.gita.app.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * Junction: items the sender offers in a trade.
 */
@Entity(
        tableName = "trade_offered_items",
        primaryKeys = {"tradeId", "itemId"},
        foreignKeys = {
                @ForeignKey(entity = Trade.class, parentColumns = "id", childColumns = "tradeId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Item.class, parentColumns = "id", childColumns = "itemId", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("tradeId"), @Index("itemId")}
)
public class TradeOfferedItem {

    public long tradeId;

    public long itemId;
}
