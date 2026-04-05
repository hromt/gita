package com.gita.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gita.app.data.local.entity.Item;
import com.gita.app.data.local.model.ItemWithOwner;

import java.util.List;

/**
 * Data access for {@link com.gita.app.data.local.entity.Item}.
 */
@Dao
public interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Item item);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);

    @Query("SELECT * FROM items WHERE ownerId = :ownerId ORDER BY name COLLATE NOCASE")
    LiveData<List<Item>> observeByOwner(long ownerId);

    @Query("SELECT * FROM items WHERE ownerId = :ownerId ORDER BY name COLLATE NOCASE")
    List<Item> getByOwner(long ownerId);

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    Item getById(long id);

    @Query("SELECT COUNT(*) FROM items WHERE ownerId = :ownerId")
    int countByOwner(long ownerId);

    @Query("SELECT AVG(estimatedValue) FROM items WHERE ownerId = :ownerId")
    Double averageValueForOwner(long ownerId);

    /**
     * Marketplace: other users' items, optionally restricted to those open to trade.
     */
    @Query("SELECT items.*, users.username AS ownerUsername FROM items "
            + "INNER JOIN users ON items.ownerId = users.id "
            + "WHERE items.ownerId != :excludeUserId "
            + "AND (:includeNonTrade = 1 OR items.isLookingToTrade = 1) "
            + "AND (LENGTH(TRIM(:query)) = 0 OR items.name LIKE '%' || :query || '%' "
            + "OR items.category LIKE '%' || :query || '%') "
            + "ORDER BY items.name COLLATE NOCASE")
    LiveData<List<ItemWithOwner>> observeMarketplace(long excludeUserId, boolean includeNonTrade, String query);

    @Query("UPDATE items SET ownerId = :newOwnerId WHERE id IN (:itemIds)")
    void updateOwnerForItems(List<Long> itemIds, long newOwnerId);
}
