package com.gita.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Local user account. Passwords are stored in plain text for this demo only;
 * production apps should hash credentials and sync via a secure backend (e.g. Azure App Service).
 */
@Entity(tableName = "users", indices = {@Index(value = "username", unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String username = "";

    @NonNull
    public String password = "";

    /** Free-text description of what the user collects (e.g. "vintage comics, vinyl"). */
    @NonNull
    public String collectionInterest = "";

    /** Comma-separated wishlist keywords used for marketplace recommendations. */
    @NonNull
    public String wishlist = "";

    @NonNull
    public String contactInfo = "";
}
