package com.gita.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.gita.app.GitaApplication;
import com.gita.app.data.SessionManager;
import com.gita.app.data.local.AppDatabase;
import com.gita.app.data.local.entity.Item;
import com.gita.app.data.local.entity.Trade;
import com.gita.app.data.local.entity.TradeOfferedItem;
import com.gita.app.data.local.entity.TradeRequestedItem;
import com.gita.app.data.local.entity.User;
import com.gita.app.data.local.model.TradeStatus;
import com.gita.app.data.local.model.TradeSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Trade lifecycle: create proposals, list incoming/outgoing, accept/reject with ownership transfer.
 */
public class TradeRepository {

    private final AppDatabase db;
    private final Executor io;
    private final SessionManager session;

    public TradeRepository(Application app) {
        GitaApplication gita = (GitaApplication) app;
        this.db = gita.getDatabase();
        this.io = gita.getIoExecutor();
        this.session = new SessionManager(app);
    }

    /**
     * Creates a trade: sender offers their items and requests items from the receiver.
     */
    public void createTrade(long receiverId, List<Long> offeredItemIds, List<Long> requestedItemIds, ResultCallback<String> cb) {
        io.execute(() -> {
            long senderId = session.getCurrentUserId();
            if (senderId < 0) {
                cb.onResult("Not signed in");
                return;
            }
            if (receiverId == senderId) {
                cb.onResult("Cannot trade with yourself");
                return;
            }
            if (offeredItemIds == null || offeredItemIds.isEmpty() || requestedItemIds == null || requestedItemIds.isEmpty()) {
                cb.onResult("Select at least one item on each side");
                return;
            }
            for (long iid : offeredItemIds) {
                Item it = db.itemDao().getById(iid);
                if (it == null || it.ownerId != senderId) {
                    cb.onResult("Invalid offered item");
                    return;
                }
            }
            for (long iid : requestedItemIds) {
                Item it = db.itemDao().getById(iid);
                if (it == null || it.ownerId != receiverId) {
                    cb.onResult("Invalid requested item");
                    return;
                }
            }

            Trade t = new Trade();
            t.senderId = senderId;
            t.receiverId = receiverId;
            t.status = TradeStatus.PENDING;

            List<TradeOfferedItem> offered = new ArrayList<>();
            for (long id : offeredItemIds) {
                TradeOfferedItem row = new TradeOfferedItem();
                row.itemId = id;
                offered.add(row);
            }
            List<TradeRequestedItem> requested = new ArrayList<>();
            for (long id : requestedItemIds) {
                TradeRequestedItem row = new TradeRequestedItem();
                row.itemId = id;
                requested.add(row);
            }

            db.runInTransaction(() -> {
                long tradeRowId = db.tradeDao().insertTrade(t);
                for (TradeOfferedItem o : offered) {
                    o.tradeId = tradeRowId;
                }
                for (TradeRequestedItem r : requested) {
                    r.tradeId = tradeRowId;
                }
                if (!offered.isEmpty()) {
                    db.tradeDao().insertOfferedItems(offered);
                }
                if (!requested.isEmpty()) {
                    db.tradeDao().insertRequestedItems(requested);
                }
            });
            cb.onResult(null);
        });
    }

    public LiveData<List<TradeSummary>> observeIncomingSummaries() {
        long uid = session.getCurrentUserId();
        if (uid < 0) {
            return new MutableLiveData<>(null);
        }
        MediatorLiveData<List<TradeSummary>> out = new MediatorLiveData<>();
        out.addSource(db.tradeDao().observeIncoming(uid), trades -> io.execute(() -> {
            List<TradeSummary> list = mapTrades(trades, true);
            out.postValue(list);
        }));
        return out;
    }

    public LiveData<List<TradeSummary>> observeOutgoingSummaries() {
        long uid = session.getCurrentUserId();
        if (uid < 0) {
            return new MutableLiveData<>(null);
        }
        MediatorLiveData<List<TradeSummary>> out = new MediatorLiveData<>();
        out.addSource(db.tradeDao().observeOutgoing(uid), trades -> io.execute(() -> {
            List<TradeSummary> list = mapTrades(trades, false);
            out.postValue(list);
        }));
        return out;
    }

    private List<TradeSummary> mapTrades(List<Trade> trades, boolean incoming) {
        List<TradeSummary> list = new ArrayList<>();
        if (trades == null) {
            return list;
        }
        for (Trade t : trades) {
            long otherId = incoming ? t.senderId : t.receiverId;
            User u = db.userDao().getById(otherId);
            String name = u != null ? u.username : "?";
            List<Long> o = db.tradeDao().getOfferedItemIds(t.id);
            List<Long> r = db.tradeDao().getRequestedItemIds(t.id);
            list.add(new TradeSummary(t, name, toPrim(o), toPrim(r)));
        }
        return list;
    }

    private static long[] toPrim(List<Long> ids) {
        long[] a = new long[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            a[i] = ids.get(i);
        }
        return a;
    }

    /**
     * Receiver accepts: offered items go to receiver; requested items go to sender.
     */
    public void acceptTrade(long tradeId, ResultCallback<String> cb) {
        io.execute(() -> {
            long me = session.getCurrentUserId();
            Trade t = db.tradeDao().getById(tradeId);
            if (t == null || !TradeStatus.PENDING.equals(t.status)) {
                cb.onResult("Trade not available");
                return;
            }
            if (t.receiverId != me) {
                cb.onResult("Only the receiver can accept");
                return;
            }
            List<Long> offered = db.tradeDao().getOfferedItemIds(tradeId);
            List<Long> requested = db.tradeDao().getRequestedItemIds(tradeId);
            if (!offered.isEmpty()) {
                db.itemDao().updateOwnerForItems(offered, t.receiverId);
            }
            if (!requested.isEmpty()) {
                db.itemDao().updateOwnerForItems(requested, t.senderId);
            }
            db.tradeDao().updateStatus(tradeId, TradeStatus.ACCEPTED);
            cb.onResult(null);
        });
    }

    public void rejectTrade(long tradeId, ResultCallback<String> cb) {
        io.execute(() -> {
            long me = session.getCurrentUserId();
            Trade t = db.tradeDao().getById(tradeId);
            if (t == null || !TradeStatus.PENDING.equals(t.status)) {
                cb.onResult("Trade not available");
                return;
            }
            if (t.receiverId != me) {
                cb.onResult("Only the receiver can reject");
                return;
            }
            db.tradeDao().updateStatus(tradeId, TradeStatus.REJECTED);
            cb.onResult(null);
        });
    }

    public interface ResultCallback<T> {
        void onResult(T value);
    }
}
