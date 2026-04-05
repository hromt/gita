package com.gita.app.ui.trades;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gita.app.data.local.model.TradeSummary;
import com.gita.app.data.repository.TradeRepository;

import java.util.List;

/**
 * Incoming and outgoing trade lists share one ViewModel scoped to the hosting activity.
 */
public class TradesViewModel extends AndroidViewModel {

    private final TradeRepository tradeRepository;

    public TradesViewModel(@NonNull Application application) {
        super(application);
        tradeRepository = new TradeRepository(application);
    }

    public LiveData<List<TradeSummary>> getIncoming() {
        return tradeRepository.observeIncomingSummaries();
    }

    public LiveData<List<TradeSummary>> getOutgoing() {
        return tradeRepository.observeOutgoingSummaries();
    }

    public void accept(long tradeId, TradeRepository.ResultCallback<String> cb) {
        tradeRepository.acceptTrade(tradeId, cb);
    }

    public void reject(long tradeId, TradeRepository.ResultCallback<String> cb) {
        tradeRepository.rejectTrade(tradeId, cb);
    }
}
