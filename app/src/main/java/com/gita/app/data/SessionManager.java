package com.gita.app.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Persists the signed-in user id for the session (local auth only).
 */
public final class SessionManager {

    private static final String PREFS = "gita_session";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void setCurrentUserId(long userId) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply();
    }

    public long getCurrentUserId() {
        return prefs.getLong(KEY_USER_ID, -1L);
    }

    public void clearSession() {
        prefs.edit().remove(KEY_USER_ID).apply();
    }

    public boolean isLoggedIn() {
        return getCurrentUserId() >= 0;
    }
}
