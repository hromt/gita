package com.gita.app.data.local.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.gita.app.data.local.entity.Item;

/**
 * Marketplace row: item plus seller username from a JOIN query.
 */
public class ItemWithOwner {

    @Embedded
    public Item item;

    @ColumnInfo(name = "ownerUsername")
    public String ownerUsername;
}
