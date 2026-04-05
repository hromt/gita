package com.gita.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Trade proposal between two users. Item membership is stored in junction tables
 * {@link TradeOfferedItem} and {@link TradeRequestedItem}.
 */
@Entity(
        tableName = "trades",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "senderId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "receiverId", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("senderId"), @Index("receiverId")}
)
public class Trade {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long senderId;

    public long receiverId;

    /** One of {@link com.gita.app.data.local.model.TradeStatus} constants. */
    @NonNull
    public String status = com.gita.app.data.local.model.TradeStatus.PENDING;
}
