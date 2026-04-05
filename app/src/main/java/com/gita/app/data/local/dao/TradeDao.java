package com.gita.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.gita.app.data.local.entity.Trade;
import com.gita.app.data.local.entity.TradeOfferedItem;
import com.gita.app.data.local.entity.TradeRequestedItem;

import java.util.List;

/**
 * Data access for {@link com.gita.app.data.local.entity.Trade} and junction rows.
 */
@Dao
public interface TradeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTrade(Trade trade);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOfferedItems(List<TradeOfferedItem> rows);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRequestedItems(List<TradeRequestedItem> rows);

    @Query("SELECT * FROM trades WHERE id = :id LIMIT 1")
    Trade getById(long id);

    @Query("SELECT * FROM trades WHERE receiverId = :userId ORDER BY id DESC")
    LiveData<List<Trade>> observeIncoming(long userId);

    @Query("SELECT * FROM trades WHERE senderId = :userId ORDER BY id DESC")
    LiveData<List<Trade>> observeOutgoing(long userId);

    @Query("SELECT itemId FROM trade_offered_items WHERE tradeId = :tradeId")
    List<Long> getOfferedItemIds(long tradeId);

    @Query("SELECT itemId FROM trade_requested_items WHERE tradeId = :tradeId")
    List<Long> getRequestedItemIds(long tradeId);

    @Query("UPDATE trades SET status = :status WHERE id = :tradeId")
    void updateStatus(long tradeId, String status);
}
