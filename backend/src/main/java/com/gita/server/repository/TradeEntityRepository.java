package com.gita.server.repository;

import com.gita.server.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeEntityRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByReceiverIdOrderByIdDesc(Long receiverId);

    List<Trade> findBySenderIdOrderByIdDesc(Long senderId);
}
