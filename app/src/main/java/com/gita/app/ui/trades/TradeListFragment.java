package com.gita.app.ui.trades;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gita.app.R;

/**
 * One page of the trades tab: either incoming or outgoing {@link com.gita.app.data.local.model.TradeSummary} list.
 */
public class TradeListFragment extends Fragment {

    private static final String ARG_INCOMING = "incoming";

    public static TradeListFragment newInstance(boolean incoming) {
        TradeListFragment f = new TradeListFragment();
        Bundle b = new Bundle();
        b.putBoolean(ARG_INCOMING, incoming);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trade_list_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean incoming = requireArguments().getBoolean(ARG_INCOMING);
        TradesViewModel vm = new ViewModelProvider(requireActivity()).get(TradesViewModel.class);
        RecyclerView rv = view.findViewById(R.id.recyclerTrades);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        TradeAdapter adapter = new TradeAdapter(incoming, new TradeAdapter.Listener() {
            @Override
            public void onAccept(long tradeId) {
                vm.accept(tradeId, err -> requireActivity().runOnUiThread(() -> {
                    if (err != null) {
                        Toast.makeText(requireContext(), err, Toast.LENGTH_LONG).show();
                    }
                }));
            }

            @Override
            public void onReject(long tradeId) {
                vm.reject(tradeId, err -> requireActivity().runOnUiThread(() -> {
                    if (err != null) {
                        Toast.makeText(requireContext(), err, Toast.LENGTH_LONG).show();
                    }
                }));
            }
        });
        rv.setAdapter(adapter);
        if (incoming) {
            vm.getIncoming().observe(getViewLifecycleOwner(), adapter::submit);
        } else {
            vm.getOutgoing().observe(getViewLifecycleOwner(), adapter::submit);
        }
    }
}
