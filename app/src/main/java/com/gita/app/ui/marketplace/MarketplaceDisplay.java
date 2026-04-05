package com.gita.app.ui.marketplace;

import com.gita.app.data.local.model.ItemWithOwner;

import java.util.ArrayList;
import java.util.List;

/**
 * Split view for the marketplace: wishlist/value-based recommendations vs the full filtered list.
 */
public class MarketplaceDisplay {

    public final List<ItemWithOwner> recommended;
    public final List<ItemWithOwner> rest;

    public MarketplaceDisplay(List<ItemWithOwner> recommended, List<ItemWithOwner> rest) {
        this.recommended = recommended;
        this.rest = rest;
    }

    public static MarketplaceDisplay empty() {
        return new MarketplaceDisplay(new ArrayList<>(), new ArrayList<>());
    }
}
