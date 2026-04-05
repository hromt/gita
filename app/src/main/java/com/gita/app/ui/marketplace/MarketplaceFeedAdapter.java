package com.gita.app.ui.marketplace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gita.app.R;
import com.gita.app.data.local.model.ItemWithOwner;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Renders section headers plus marketplace cards (Material list).
 */
public class MarketplaceFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public interface Listener {
        void onProposeTrade(ItemWithOwner row);
    }

    private final List<Object> rows = new ArrayList<>();
    private final Listener listener;
    private final Context appContext;

    public MarketplaceFeedAdapter(Context context, Listener listener) {
        this.appContext = context.getApplicationContext();
        this.listener = listener;
    }

    public void submit(MarketplaceDisplay display) {
        rows.clear();
        if (display.recommended != null && !display.recommended.isEmpty()) {
            rows.add(appContext.getString(R.string.recommendations));
            rows.addAll(display.recommended);
        }
        if (display.rest != null && !display.rest.isEmpty()) {
            rows.add(appContext.getString(R.string.all_listings));
            rows.addAll(display.rest);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object o = rows.get(position);
        return o instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View v = inf.inflate(R.layout.row_section_header, parent, false);
            return new HeaderVH(v);
        }
        View v = inf.inflate(R.layout.row_marketplace_item, parent, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderVH) {
            ((HeaderVH) holder).bind((String) rows.get(position));
        } else {
            ItemVH h = (ItemVH) holder;
            ItemWithOwner row = (ItemWithOwner) rows.get(position);
            h.bind(row, listener);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static void appendStatusLine(StringBuilder sb, boolean looking) {
        sb.append(looking ? "Open to trade" : "Not actively trading");
    }

    static final class HeaderVH extends RecyclerView.ViewHolder {
        private final TextView text;

        HeaderVH(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textHeader);
        }

        void bind(String title) {
            text.setText(title);
        }
    }

    static final class ItemVH extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView subtitle;
        private final TextView meta;
        private final View tradeBtn;

        ItemVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            subtitle = itemView.findViewById(R.id.textSubtitle);
            meta = itemView.findViewById(R.id.textMeta);
            tradeBtn = itemView.findViewById(R.id.buttonTrade);
        }

        void bind(ItemWithOwner row, Listener listener) {
            title.setText(row.item.name);
            subtitle.setText(String.format(Locale.US, "%s / %s · Owner: %s",
                    row.item.category, row.item.subcategory, row.ownerUsername));
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(Locale.US, "Est. value: %.0f · ", row.item.estimatedValue));
            appendStatusLine(sb, row.item.isLookingToTrade);
            meta.setText(sb.toString());
            tradeBtn.setOnClickListener(v -> listener.onProposeTrade(row));
        }
    }
}
