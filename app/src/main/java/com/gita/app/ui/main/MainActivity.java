package com.gita.app.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gita.app.R;
import com.gita.app.data.SessionManager;
import com.gita.app.ui.auth.LoginActivity;
import com.gita.app.ui.inventory.InventoryFragment;
import com.gita.app.ui.marketplace.MarketplaceFragment;
import com.gita.app.ui.profile.ProfileFragment;
import com.gita.app.ui.trades.TradesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Hosts the four main {@link Fragment}s behind bottom navigation (Material shell).
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_MARKET = "marketplace";
    private static final String TAG_INV = "inventory";
    private static final String TAG_TRADES = "trades";
    private static final String TAG_PROFILE = "profile";

    private MarketplaceFragment marketplaceFragment;
    private InventoryFragment inventoryFragment;
    private TradesFragment tradesFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            marketplaceFragment = new MarketplaceFragment();
            inventoryFragment = new InventoryFragment();
            tradesFragment = new TradesFragment();
            profileFragment = new ProfileFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, marketplaceFragment, TAG_MARKET)
                    .add(R.id.fragmentContainer, inventoryFragment, TAG_INV).hide(inventoryFragment)
                    .add(R.id.fragmentContainer, tradesFragment, TAG_TRADES).hide(tradesFragment)
                    .add(R.id.fragmentContainer, profileFragment, TAG_PROFILE).hide(profileFragment)
                    .commit();
        } else {
            marketplaceFragment = (MarketplaceFragment) fm.findFragmentByTag(TAG_MARKET);
            inventoryFragment = (InventoryFragment) fm.findFragmentByTag(TAG_INV);
            tradesFragment = (TradesFragment) fm.findFragmentByTag(TAG_TRADES);
            profileFragment = (ProfileFragment) fm.findFragmentByTag(TAG_PROFILE);
        }

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_marketplace) {
                showFragment(marketplaceFragment);
                return true;
            }
            if (id == R.id.nav_inventory) {
                showFragment(inventoryFragment);
                return true;
            }
            if (id == R.id.nav_trades) {
                showFragment(tradesFragment);
                return true;
            }
            if (id == R.id.nav_profile) {
                showFragment(profileFragment);
                return true;
            }
            return false;
        });
    }

    private void showFragment(Fragment target) {
        if (target == null) {
            return;
        }
        getSupportFragmentManager().beginTransaction()
                .hide(marketplaceFragment)
                .hide(inventoryFragment)
                .hide(tradesFragment)
                .hide(profileFragment)
                .show(target)
                .commit();
    }

    /** Lets child fragments jump to Inventory (e.g. marketplace gate). */
    public void selectTab(@IdRes int menuItemId) {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(menuItemId);
    }
}
