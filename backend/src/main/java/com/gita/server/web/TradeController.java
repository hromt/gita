package com.gita.server.web;

import com.gita.server.dto.CreateTradeRequest;
import com.gita.server.dto.TradeSummaryDto;
import com.gita.server.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping
    public void create(@Valid @RequestBody CreateTradeRequest request) {
        tradeService.create(request);
    }

    @GetMapping("/incoming")
    public List<TradeSummaryDto> incoming() {
        return tradeService.incoming();
    }

    @GetMapping("/outgoing")
    public List<TradeSummaryDto> outgoing() {
        return tradeService.outgoing();
    }

    @PostMapping("/{id}/accept")
    public void accept(@PathVariable long id) {
        tradeService.accept(id);
    }

    @PostMapping("/{id}/reject")
    public void reject(@PathVariable long id) {
        tradeService.reject(id);
    }
}
