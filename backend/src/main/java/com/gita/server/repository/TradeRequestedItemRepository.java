package com.gita.server.repository;

import com.gita.server.entity.TradeRequestedItem;
import com.gita.server.entity.TradeRequestedItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRequestedItemRepository extends JpaRepository<TradeRequestedItem, TradeRequestedItemId> {

    List<TradeRequestedItem> findByTradeId(Long tradeId);
}
