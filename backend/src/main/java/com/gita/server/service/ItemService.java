package com.gita.server.service;

import com.gita.server.dto.InventoryStatsDto;
import com.gita.server.dto.ItemDto;
import com.gita.server.dto.ItemUpsertRequest;
import com.gita.server.dto.ItemWithOwnerDto;
import com.gita.server.entity.Item;
import com.gita.server.repository.ItemEntityRepository;
import com.gita.server.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ItemService {

    private final ItemEntityRepository items;

    public ItemService(ItemEntityRepository items) {
        this.items = items;
    }

    public List<ItemDto> listMine() {
        long uid = CurrentUser.id();
        return items.findByOwnerIdOrderByNameAsc(uid).stream()
                .map(ItemService::toDto)
                .toList();
    }

    public List<ItemWithOwnerDto> marketplace(boolean includeNonTrade, String query) {
        long uid = CurrentUser.id();
        String q = query == null ? "" : query.trim();
        return items.searchMarketplace(uid, includeNonTrade, q);
    }

    public ItemDto getById(long itemId) {
        Item it = items.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        return toDto(it);
    }

    @Transactional
    public ItemDto create(ItemUpsertRequest req) {
        long uid = CurrentUser.id();
        Item it = new Item();
        it.setOwnerId(uid);
        it.setName(req.name().trim());
        it.setDescription(req.description() != null ? req.description() : "");
        it.setCategory(req.category() != null ? req.category() : "");
        it.setSubcategory(req.subcategory() != null ? req.subcategory() : "");
        it.setEstimatedValue(req.estimatedValue());
        it.setLookingToTrade(req.lookingToTrade() != null ? req.lookingToTrade() : false);
        it.setImageUri(req.imageUri());
        it = items.save(it);
        return toDto(it);
    }

    @Transactional
    public ItemDto update(long itemId, ItemUpsertRequest req) {
        long uid = CurrentUser.id();
        Item it = items.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        if (!it.getOwnerId().equals(uid)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your item");
        }
        applyUpsert(it, req);
        it = items.save(it);
        return toDto(it);
    }

    @Transactional
    public void delete(long itemId) {
        long uid = CurrentUser.id();
        Item it = items.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        if (!it.getOwnerId().equals(uid)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your item");
        }
        items.delete(it);
    }

    public InventoryStatsDto inventoryStats() {
        long uid = CurrentUser.id();
        int count = items.countByOwnerId(uid);
        double avg = items.averageEstimatedValueForOwner(uid);
        return new InventoryStatsDto(count, avg);
    }

    private static void applyUpsert(Item it, ItemUpsertRequest req) {
        if (req.name() != null) {
            it.setName(req.name());
        }
        if (req.description() != null) {
            it.setDescription(req.description());
        }
        if (req.category() != null) {
            it.setCategory(req.category());
        }
        if (req.subcategory() != null) {
            it.setSubcategory(req.subcategory());
        }
        it.setEstimatedValue(req.estimatedValue());
        if (req.lookingToTrade() != null) {
            it.setLookingToTrade(req.lookingToTrade());
        }
        if (req.imageUri() != null) {
            it.setImageUri(req.imageUri());
        }
    }

    private static ItemDto toDto(Item it) {
        return new ItemDto(
                it.getId(),
                it.getOwnerId(),
                it.getName(),
                it.getDescription(),
                it.getCategory(),
                it.getSubcategory(),
                it.getEstimatedValue(),
                it.isLookingToTrade(),
                it.getImageUri()
        );
    }
}
