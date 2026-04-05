package com.gita.server.web;

import com.gita.server.dto.InventoryStatsDto;
import com.gita.server.dto.ItemDto;
import com.gita.server.dto.ItemUpsertRequest;
import com.gita.server.dto.ItemWithOwnerDto;
import com.gita.server.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/mine")
    public List<ItemDto> mine() {
        return itemService.listMine();
    }

    @GetMapping("/marketplace")
    public List<ItemWithOwnerDto> marketplace(
            @RequestParam(defaultValue = "false") boolean includeNonTrade,
            @RequestParam(required = false) String query
    ) {
        return itemService.marketplace(includeNonTrade, query);
    }

    @GetMapping("/inventory/stats")
    public InventoryStatsDto inventoryStats() {
        return itemService.inventoryStats();
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable long id) {
        return itemService.getById(id);
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemUpsertRequest request) {
        return itemService.create(request);
    }

    @PutMapping("/{id}")
    public ItemDto update(@PathVariable long id, @Valid @RequestBody ItemUpsertRequest request) {
        return itemService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        itemService.delete(id);
    }
}
