package com.gita.app.ui.inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gita.app.R;
import com.gita.app.data.local.entity.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory list with quick edit/delete actions.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.VH> {

    public interface Listener {
        void onEdit(Item item);

        void onDelete(Item item);
    }

    private final List<Item> items = new ArrayList<>();
    private final Listener listener;

    public InventoryAdapter(Listener listener) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_inventory_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Item it = items.get(position);
        holder.title.setText(it.name);
        holder.subtitle.setText(it.category + " · " + it.subcategory + " · est. " + Math.round(it.estimatedValue));
        holder.edit.setOnClickListener(v -> listener.onEdit(it));
        holder.delete.setOnClickListener(v -> listener.onDelete(it));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static final class VH extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;
        final ImageButton edit;
        final ImageButton delete;

        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            subtitle = itemView.findViewById(R.id.textSubtitle);
            edit = itemView.findViewById(R.id.buttonEdit);
            delete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
