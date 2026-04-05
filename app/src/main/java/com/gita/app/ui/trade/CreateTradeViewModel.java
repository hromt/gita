package com.gita.app.ui.trade;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.gita.app.GitaApplication;
import com.gita.app.data.local.AppDatabase;
import com.gita.app.data.local.entity.Item;
import com.gita.app.data.repository.TradeRepository;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Loads both sides of a trade (your inventory vs the counterparty's) and submits a proposal.
 */
public class CreateTradeViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final Executor io;
    private final TradeRepository tradeRepository;

    public CreateTradeViewModel(@NonNull Application application) {
        super(application);
        GitaApplication app = (GitaApplication) application;
        db = app.getDatabase();
        io = app.getIoExecutor();
        tradeRepository = new TradeRepository(application);
    }

    public void loadSides(long currentUserId, long receiverUserId, ResultCallback<SidePayload> cb) {
        io.execute(() -> {
            List<Item> mine = db.itemDao().getByOwner(currentUserId);
            List<Item> theirs = db.itemDao().getByOwner(receiverUserId);
            cb.onResult(new SidePayload(mine, theirs));
        });
    }

    public void submit(long receiverId, List<Long> offeredIds, List<Long> requestedIds, TradeRepository.ResultCallback<String> cb) {
        tradeRepository.createTrade(receiverId, offeredIds, requestedIds, cb);
    }

    public static final class SidePayload {
        public final List<Item> myItems;
        public final List<Item> theirItems;

        public SidePayload(List<Item> myItems, List<Item> theirItems) {
            this.myItems = myItems;
            this.theirItems = theirItems;
        }
    }

    public interface ResultCallback<T> {
        void onResult(T value);
    }
}
