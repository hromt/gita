package com.gita.app.ui.trade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gita.app.R;
import com.gita.app.data.local.entity.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Checkbox list for choosing which {@link Item}s participate in a trade proposal.
 */
public class SelectableItemAdapter extends RecyclerView.Adapter<SelectableItemAdapter.VH> {

    public interface Listener {
        void onToggle(long itemId, boolean checked);
    }

    private final List<Item> items = new ArrayList<>();
    private final Set<Long> selected;
    private final Listener listener;

    public SelectableItemAdapter(Set<Long> selected, Listener listener) {
        this.selected = selected;
        this.listener = listener;
    }

    public void submit(List<Item> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_selectable_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Item it = items.get(position);
        holder.label.setText(it.name + " · " + it.category);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selected.contains(it.id));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onToggle(it.id, isChecked));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static final class VH extends RecyclerView.ViewHolder {
        final CheckBox checkBox;
        final TextView label;

        VH(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            label = itemView.findViewById(R.id.textLabel);
        }
    }
}
