package com.gita.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.gita.app.data.local.entity.User;

/**
 * Data access for {@link com.gita.app.data.local.entity.User}.
 */
@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(User user);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getByUsername(String username);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User getById(long id);

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int countByUsername(String username);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    LiveData<User> observeUser(long id);

    @Query("UPDATE users SET collectionInterest = :interest, wishlist = :wishlist, contactInfo = :contact WHERE id = :userId")
    void updateProfile(long userId, String interest, String wishlist, String contact);
}
