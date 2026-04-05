package com.gita.app.ui.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gita.app.R;
import com.gita.app.data.local.entity.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * RecyclerView-backed inventory; opens {@link AddEditItemActivity} for CRUD.
 */
public class InventoryFragment extends Fragment {

    private InventoryViewModel viewModel;
    private InventoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);
        RecyclerView rv = view.findViewById(R.id.recyclerInventory);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new InventoryAdapter(new InventoryAdapter.Listener() {
            @Override
            public void onEdit(Item item) {
                startActivity(AddEditItemActivity.editIntent(requireContext(), item.id));
            }

            @Override
            public void onDelete(Item item) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.delete)
                        .setMessage(item.name)
                        .setPositiveButton(R.string.delete, (d, w) ->
                                viewModel.delete(item, null))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
        rv.setAdapter(adapter);
        viewModel.getInventory().observe(getViewLifecycleOwner(), adapter::submit);

        FloatingActionButton fab = view.findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(AddEditItemActivity.createIntent(requireContext())));
    }
}
