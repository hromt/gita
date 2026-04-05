package com.gita.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.gita.app.GitaApplication;
import com.gita.app.data.SessionManager;
import com.gita.app.data.local.AppDatabase;
import com.gita.app.data.local.entity.User;

import java.util.concurrent.Executor;

/**
 * Repository for user registration, login, and profile (Room + session).
 */
public class UserRepository {

    private final AppDatabase db;
    private final Executor io;
    private final SessionManager session;

    public UserRepository(Application app) {
        GitaApplication gita = (GitaApplication) app;
        this.db = gita.getDatabase();
        this.io = gita.getIoExecutor();
        this.session = new SessionManager(app);
    }

    public SessionManager getSession() {
        return session;
    }

    public LiveData<User> observeCurrentUser() {
        long id = session.getCurrentUserId();
        if (id < 0) {
            return new androidx.lifecycle.MutableLiveData<>(null);
        }
        return db.userDao().observeUser(id);
    }

    /**
     * Registers a new user if username is free; returns null on success or an error message.
     */
    public void register(User user, ResultCallback<String> callback) {
        io.execute(() -> {
            if (db.userDao().countByUsername(user.username.trim()) > 0) {
                callback.onResult("Username already taken");
                return;
            }
            user.username = user.username.trim();
            user.collectionInterest = user.collectionInterest != null ? user.collectionInterest : "";
            user.wishlist = user.wishlist != null ? user.wishlist : "";
            user.contactInfo = user.contactInfo != null ? user.contactInfo : "";
            long id = db.userDao().insert(user);
            session.setCurrentUserId(id);
            callback.onResult(null);
        });
    }

    /**
     * Validates credentials and opens a session.
     */
    public void login(String username, String password, ResultCallback<String> callback) {
        io.execute(() -> {
            User u = db.userDao().getByUsername(username.trim());
            if (u == null || !u.password.equals(password)) {
                callback.onResult("Invalid username or password");
                return;
            }
            session.setCurrentUserId(u.id);
            callback.onResult(null);
        });
    }

    public void logout() {
        session.clearSession();
    }

    public void updateProfile(String interest, String wishlist, String contact, Runnable onDone) {
        long id = session.getCurrentUserId();
        if (id < 0) {
            return;
        }
        io.execute(() -> {
            db.userDao().updateProfile(id, interest != null ? interest : "", wishlist != null ? wishlist : "",
                    contact != null ? contact : "");
            if (onDone != null) {
                onDone.run();
            }
        });
    }

    public User getUserByIdSync(long id) {
        return db.userDao().getById(id);
    }

    public interface ResultCallback<T> {
        void onResult(T value);
    }
}
