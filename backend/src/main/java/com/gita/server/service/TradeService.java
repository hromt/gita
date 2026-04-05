package com.gita.server.service;

import com.gita.server.dto.CreateTradeRequest;
import com.gita.server.dto.TradeSummaryDto;
import com.gita.server.entity.Item;
import com.gita.server.entity.Trade;
import com.gita.server.entity.TradeOfferedItem;
import com.gita.server.entity.TradeRequestedItem;
import com.gita.server.model.TradeStatuses;
import com.gita.server.repository.ItemEntityRepository;
import com.gita.server.repository.TradeEntityRepository;
import com.gita.server.repository.TradeOfferedItemRepository;
import com.gita.server.repository.TradeRequestedItemRepository;
import com.gita.server.repository.UserEntityRepository;
import com.gita.server.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class TradeService {

    private final TradeEntityRepository trades;
    private final TradeOfferedItemRepository offeredRows;
    private final TradeRequestedItemRepository requestedRows;
    private final ItemEntityRepository items;
    private final UserEntityRepository users;

    public TradeService(
            TradeEntityRepository trades,
            TradeOfferedItemRepository offeredRows,
            TradeRequestedItemRepository requestedRows,
            ItemEntityRepository items,
            UserEntityRepository users
    ) {
        this.trades = trades;
        this.offeredRows = offeredRows;
        this.requestedRows = requestedRows;
        this.items = items;
        this.users = users;
    }

    @Transactional
    public void create(CreateTradeRequest req) {
        long senderId = CurrentUser.id();
        Long receiverId = req.receiverId();
        if (receiverId.equals(senderId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot trade with yourself");
        }
        if (!users.existsById(receiverId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown receiver");
        }
        List<Long> offered = req.offeredItemIds();
        List<Long> requested = req.requestedItemIds();
        if (offered == null || offered.isEmpty() || requested == null || requested.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Select at least one item on each side");
        }
        for (Long iid : offered) {
            Item it = items.findById(iid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid offered item"));
            if (!it.getOwnerId().equals(senderId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid offered item");
            }
        }
        for (Long iid : requested) {
            Item it = items.findById(iid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid requested item"));
            if (!it.getOwnerId().equals(receiverId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid requested item");
            }
        }

        Trade t = new Trade();
        t.setSenderId(senderId);
        t.setReceiverId(receiverId);
        t.setStatus(TradeStatuses.PENDING);
        t = trades.save(t);

        long tradeId = t.getId();
        for (Long id : offered) {
            TradeOfferedItem row = new TradeOfferedItem();
            row.setTradeId(tradeId);
            row.setItemId(id);
            offeredRows.save(row);
        }
        for (Long id : requested) {
            TradeRequestedItem row = new TradeRequestedItem();
            row.setTradeId(tradeId);
            row.setItemId(id);
            requestedRows.save(row);
        }
    }

    public List<TradeSummaryDto> incoming() {
        long uid = CurrentUser.id();
        return mapSummaries(trades.findByReceiverIdOrderByIdDesc(uid), true);
    }

    public List<TradeSummaryDto> outgoing() {
        long uid = CurrentUser.id();
        return mapSummaries(trades.findBySenderIdOrderByIdDesc(uid), false);
    }

    private List<TradeSummaryDto> mapSummaries(List<Trade> list, boolean incoming) {
        List<TradeSummaryDto> out = new ArrayList<>();
        for (Trade t : list) {
            long otherId = incoming ? t.getSenderId() : t.getReceiverId();
            String otherName = users.findById(otherId).map(u -> u.getUsername()).orElse("?");
            List<Long> o = offeredRows.findByTradeId(t.getId()).stream().map(TradeOfferedItem::getItemId).toList();
            List<Long> r = requestedRows.findByTradeId(t.getId()).stream().map(TradeRequestedItem::getItemId).toList();
            out.add(new TradeSummaryDto(
                    t.getId(),
                    t.getSenderId(),
                    t.getReceiverId(),
                    t.getStatus(),
                    otherName,
                    incoming,
                    o,
                    r
            ));
        }
        return out;
    }

    @Transactional
    public void accept(long tradeId) {
        long me = CurrentUser.id();
        Trade t = trades.findById(tradeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trade not available"));
        if (!TradeStatuses.PENDING.equals(t.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trade not available");
        }
        if (!t.getReceiverId().equals(me)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the receiver can accept");
        }
        List<Long> offered = offeredRows.findByTradeId(tradeId).stream().map(TradeOfferedItem::getItemId).toList();
        List<Long> requested = requestedRows.findByTradeId(tradeId).stream().map(TradeRequestedItem::getItemId).toList();
        if (!offered.isEmpty()) {
            items.updateOwnerForItems(offered, t.getReceiverId());
        }
        if (!requested.isEmpty()) {
            items.updateOwnerForItems(requested, t.getSenderId());
        }
        t.setStatus(TradeStatuses.ACCEPTED);
        trades.save(t);
    }

    @Transactional
    public void reject(long tradeId) {
        long me = CurrentUser.id();
        Trade t = trades.findById(tradeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trade not available"));
        if (!TradeStatuses.PENDING.equals(t.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trade not available");
        }
        if (!t.getReceiverId().equals(me)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the receiver can reject");
        }
        t.setStatus(TradeStatuses.REJECTED);
        trades.save(t);
    }
}
