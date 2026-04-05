package com.gita.app.ui.trade;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gita.app.R;
import com.gita.app.data.SessionManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Multi-select inventory on both sides, then persists a {@link com.gita.app.data.local.entity.Trade}.
 */
public class CreateTradeActivity extends AppCompatActivity {

    public static final String EXTRA_RECEIVER_ID = "receiver_id";
    public static final String EXTRA_THEIR_ITEM_ID = "their_item_id";

    private final Set<Long> mineSelected = new HashSet<>();
    private final Set<Long> theirsSelected = new HashSet<>();
    private SelectableItemAdapter mineAdapter;
    private SelectableItemAdapter theirsAdapter;
    private long receiverId;

    public static Intent newIntent(Context context, long receiverUserId, long preselectTheirItemId) {
        Intent i = new Intent(context, CreateTradeActivity.class);
        i.putExtra(EXTRA_RECEIVER_ID, receiverUserId);
        i.putExtra(EXTRA_THEIR_ITEM_ID, preselectTheirItemId);
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trade);
        receiverId = getIntent().getLongExtra(EXTRA_RECEIVER_ID, -1);
        long pre = getIntent().getLongExtra(EXTRA_THEIR_ITEM_ID, -1);
        if (receiverId < 0) {
            finish();
            return;
        }

        SessionManager session = new SessionManager(this);
        long me = session.getCurrentUserId();
        if (me < 0) {
            finish();
            return;
        }
        CreateTradeViewModel vm = new ViewModelProvider(this).get(CreateTradeViewModel.class);

        RecyclerView rvMine = findViewById(R.id.recyclerMine);
        RecyclerView rvTheirs = findViewById(R.id.recyclerTheirs);
        rvMine.setLayoutManager(new LinearLayoutManager(this));
        rvTheirs.setLayoutManager(new LinearLayoutManager(this));

        mineAdapter = new SelectableItemAdapter(mineSelected, (itemId, checked) -> {
            if (checked) {
                mineSelected.add(itemId);
            } else {
                mineSelected.remove(itemId);
            }
        });
        theirsAdapter = new SelectableItemAdapter(theirsSelected, (itemId, checked) -> {
            if (checked) {
                theirsSelected.add(itemId);
            } else {
                theirsSelected.remove(itemId);
            }
        });
        rvMine.setAdapter(mineAdapter);
        rvTheirs.setAdapter(theirsAdapter);

        vm.loadSides(me, receiverId, payload -> runOnUiThread(() -> {
            mineAdapter.submit(payload.myItems);
            theirsAdapter.submit(payload.theirItems);
            if (pre > 0) {
                theirsSelected.add(pre);
                theirsAdapter.notifyDataSetChanged();
            }
        }));

        findViewById(R.id.buttonSubmit).setOnClickListener(v -> {
            List<Long> offered = new ArrayList<>(mineSelected);
            List<Long> requested = new ArrayList<>(theirsSelected);
            vm.submit(receiverId, offered, requested, err -> runOnUiThread(() -> {
                if (err != null) {
                    Toast.makeText(CreateTradeActivity.this, err, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CreateTradeActivity.this, R.string.trade_saved, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }));
        });
    }
}
