package com.gita.app.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * Junction: items the sender requests from the receiver.
 */
@Entity(
        tableName = "trade_requested_items",
        primaryKeys = {"tradeId", "itemId"},
        foreignKeys = {
                @ForeignKey(entity = Trade.class, parentColumns = "id", childColumns = "tradeId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Item.class, parentColumns = "id", childColumns = "itemId", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("tradeId"), @Index("itemId")}
)
public class TradeRequestedItem {

    public long tradeId;

    public long itemId;
}
