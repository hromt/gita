package com.gita.app.ui.trades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gita.app.R;
import com.gita.app.data.local.model.TradeStatus;
import com.gita.app.data.local.model.TradeSummary;

import java.util.ArrayList;
import java.util.List;
/**
 * Binds {@link TradeSummary} rows; incoming pending trades expose accept/reject.
 */
public class TradeAdapter extends RecyclerView.Adapter<TradeAdapter.VH> {

    public interface Listener {
        void onAccept(long tradeId);

        void onReject(long tradeId);
    }

    private final List<TradeSummary> items = new ArrayList<>();
    private final boolean incoming;
    private final Listener listener;

    public TradeAdapter(boolean incoming, Listener listener) {
        this.incoming = incoming;
        this.listener = listener;
    }

    public void submit(List<TradeSummary> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_trade, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TradeSummary s = items.get(position);
        android.content.Context c = holder.itemView.getContext();
        String party = incoming
                ? c.getString(R.string.trade_from, s.otherPartyUsername)
                : c.getString(R.string.trade_to, s.otherPartyUsername);
        holder.party.setText(party);
        holder.status.setText(statusLabel(c, s.trade.status));
        int offer = s.offeredItemIds.length;
        int req = s.requestedItemIds.length;
        String line;
        if (incoming) {
            line = c.getString(R.string.trade_line_incoming, offer, req);
        } else {
            line = c.getString(R.string.trade_line_outgoing, offer, req);
        }
        holder.items.setText(line);

        boolean pending = TradeStatus.PENDING.equals(s.trade.status);
        boolean show = incoming && pending;
        holder.actions.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            holder.accept.setOnClickListener(v -> listener.onAccept(s.trade.id));
            holder.reject.setOnClickListener(v -> listener.onReject(s.trade.id));
        }
    }

    private static String statusLabel(android.content.Context c, String status) {
        if (TradeStatus.ACCEPTED.equals(status)) {
            return c.getString(R.string.trade_status_accepted);
        }
        if (TradeStatus.REJECTED.equals(status)) {
            return c.getString(R.string.trade_status_rejected);
        }
        return c.getString(R.string.trade_status_pending);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static final class VH extends RecyclerView.ViewHolder {
        final TextView party;
        final TextView status;
        final TextView items;
        final LinearLayout actions;
        final View accept;
        final View reject;

        VH(@NonNull View itemView) {
            super(itemView);
            party = itemView.findViewById(R.id.textParty);
            status = itemView.findViewById(R.id.textStatus);
            items = itemView.findViewById(R.id.textItems);
            actions = itemView.findViewById(R.id.layoutActions);
            accept = itemView.findViewById(R.id.buttonAccept);
            reject = itemView.findViewById(R.id.buttonReject);
        }
    }
}
