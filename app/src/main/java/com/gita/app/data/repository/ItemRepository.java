package com.gita.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gita.app.GitaApplication;
import com.gita.app.data.SessionManager;
import com.gita.app.data.local.AppDatabase;
import com.gita.app.data.local.entity.Item;
import com.gita.app.data.local.model.ItemWithOwner;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Inventory and marketplace queries backed by Room.
 */
public class ItemRepository {

    private final AppDatabase db;
    private final Executor io;
    private final SessionManager session;

    public ItemRepository(Application app) {
        GitaApplication gita = (GitaApplication) app;
        this.db = gita.getDatabase();
        this.io = gita.getIoExecutor();
        this.session = new SessionManager(app);
    }

    public LiveData<List<Item>> observeMyInventory() {
        long uid = session.getCurrentUserId();
        if (uid < 0) {
            return new MutableLiveData<>(null);
        }
        return db.itemDao().observeByOwner(uid);
    }

    public void insert(Item item, Runnable onDone) {
        io.execute(() -> {
            long uid = session.getCurrentUserId();
            item.ownerId = uid;
            db.itemDao().insert(item);
            if (onDone != null) {
                onDone.run();
            }
        });
    }

    public void update(Item item, Runnable onDone) {
        io.execute(() -> {
            db.itemDao().update(item);
            if (onDone != null) {
                onDone.run();
            }
        });
    }

    public void delete(Item item, Runnable onDone) {
        io.execute(() -> {
            db.itemDao().delete(item);
            if (onDone != null) {
                onDone.run();
            }
        });
    }

    public void getById(long itemId, ResultCallback<Item> cb) {
        io.execute(() -> cb.onResult(db.itemDao().getById(itemId)));
    }

    public LiveData<List<ItemWithOwner>> observeMarketplace(boolean includeNonTrade, String searchQuery) {
        long uid = session.getCurrentUserId();
        if (uid < 0) {
            return new MutableLiveData<>(null);
        }
        String q = searchQuery == null ? "" : searchQuery.trim();
        return db.itemDao().observeMarketplace(uid, includeNonTrade, q);
    }

    public void countMyItems(ResultCallback<Integer> cb) {
        io.execute(() -> {
            long uid = session.getCurrentUserId();
            if (uid < 0) {
                cb.onResult(0);
                return;
            }
            cb.onResult(db.itemDao().countByOwner(uid));
        });
    }

    public void averageMyInventoryValue(ResultCallback<Double> cb) {
        io.execute(() -> {
            long uid = session.getCurrentUserId();
            if (uid < 0) {
                cb.onResult(0d);
                return;
            }
            Double d = db.itemDao().averageValueForOwner(uid);
            cb.onResult(d != null ? d : 0d);
        });
    }

    public interface ResultCallback<T> {
        void onResult(T value);
    }
}
