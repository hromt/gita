package com.gita.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * A collectible listed by a user. {@link #isLookingToTrade} gates default marketplace visibility.
 */
@Entity(
        tableName = "items",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "ownerId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("ownerId"), @Index("category")}
)
public class Item {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long ownerId;

    @NonNull
    public String name = "";

    @NonNull
    public String description = "";

    @NonNull
    public String category = "";

    @NonNull
    public String subcategory = "";

    /** Rough value for matching and recommendations (not a price — trades are barter). */
    public double estimatedValue;

    public boolean isLookingToTrade;

    /** Optional content URI string for a photo chosen from the gallery. */
    public String imageUri;
}
