package com.gita.app.ui.marketplace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gita.app.R;
import com.gita.app.data.repository.ItemRepository;
import com.gita.app.ui.main.MainActivity;
import com.gita.app.ui.trade.CreateTradeActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Filterable marketplace with recommendation sections; gated until the user lists inventory.
 */
public class MarketplaceFragment extends Fragment {

    private MarketplaceViewModel viewModel;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private MarketplaceFeedAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_marketplace, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MarketplaceViewModel.class);

        SwipeRefreshLayout swipe = view.findViewById(R.id.swipeRefresh);
        RecyclerView recycler = view.findViewById(R.id.recyclerMarketplace);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MarketplaceFeedAdapter(requireContext(), row -> {
            Intent i = CreateTradeActivity.newIntent(requireContext(), row.item.ownerId, row.item.id);
            startActivity(i);
        });
        recycler.setAdapter(adapter);

        com.google.android.material.textfield.TextInputEditText search = view.findViewById(R.id.inputSearch);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> viewModel.setSearchQuery(s != null ? s.toString() : "");
                handler.postDelayed(searchRunnable, 300);
            }
        });

        SwitchMaterial sw = view.findViewById(R.id.switchNonTrade);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.setIncludeNonTrade(isChecked));

        viewModel.getDisplay().observe(getViewLifecycleOwner(), display -> {
            adapter.submit(display);
            swipe.setRefreshing(false);
        });

        swipe.setOnRefreshListener(() -> viewModel.setSearchQuery(
                search.getText() != null ? search.getText().toString() : ""));

        View gate = view.findViewById(R.id.gateInventory);
        view.findViewById(R.id.buttonGoInventory).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectTab(R.id.nav_inventory);
            }
        });

        ItemRepository repo = new ItemRepository(requireActivity().getApplication());
        repo.countMyItems(count -> requireActivity().runOnUiThread(() ->
                gate.setVisibility(count != null && count > 0 ? View.GONE : View.VISIBLE)));
    }

    @Override
    public void onResume() {
        super.onResume();
        ItemRepository repo = new ItemRepository(requireActivity().getApplication());
        View gate = getView() != null ? getView().findViewById(R.id.gateInventory) : null;
        repo.countMyItems(count -> requireActivity().runOnUiThread(() -> {
            if (gate != null) {
                gate.setVisibility(count != null && count > 0 ? View.GONE : View.VISIBLE);
            }
        }));
    }
}
