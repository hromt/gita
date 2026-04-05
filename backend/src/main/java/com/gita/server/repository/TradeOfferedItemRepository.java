package com.gita.server.repository;

import com.gita.server.entity.TradeOfferedItem;
import com.gita.server.entity.TradeOfferedItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeOfferedItemRepository extends JpaRepository<TradeOfferedItem, TradeOfferedItemId> {

    List<TradeOfferedItem> findByTradeId(Long tradeId);
}
