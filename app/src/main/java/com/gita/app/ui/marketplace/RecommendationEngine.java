package com.gita.app.ui.marketplace;

import com.gita.app.data.local.entity.Item;
import com.gita.app.data.local.entity.User;
import com.gita.app.data.local.model.ItemWithOwner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Simple string-matching and value-band heuristics for "recommended" rows (local-only demo).
 */
public final class RecommendationEngine {

    private RecommendationEngine() {
    }

    public static MarketplaceDisplay partition(List<ItemWithOwner> all, User user, double avgInventoryValue) {
        List<ItemWithOwner> rec = new ArrayList<>();
        List<ItemWithOwner> rest = new ArrayList<>();
        if (all == null) {
            return new MarketplaceDisplay(rec, rest);
        }
        List<String> keywords = parseWishlist(user != null ? user.wishlist : "");
        for (ItemWithOwner row : all) {
            if (isRecommended(row.item, keywords, avgInventoryValue)) {
                rec.add(row);
            } else {
                rest.add(row);
            }
        }
        return new MarketplaceDisplay(rec, rest);
    }

    private static List<String> parseWishlist(String wishlist) {
        List<String> out = new ArrayList<>();
        if (wishlist == null || wishlist.trim().isEmpty()) {
            return out;
        }
        for (String part : wishlist.split(",")) {
            String k = part.trim().toLowerCase(Locale.US);
            if (k.length() > 0) {
                out.add(k);
            }
        }
        return out;
    }

    private static boolean isRecommended(Item item, List<String> keywords, double avg) {
        String name = item.name != null ? item.name.toLowerCase(Locale.US) : "";
        String cat = item.category != null ? item.category.toLowerCase(Locale.US) : "";
        String sub = item.subcategory != null ? item.subcategory.toLowerCase(Locale.US) : "";
        for (String k : keywords) {
            if (name.contains(k) || cat.contains(k) || sub.contains(k)) {
                return true;
            }
        }
        double baseline = avg > 0 ? avg : 1d;
        double ratio = Math.abs(item.estimatedValue - avg) / baseline;
        return ratio <= 0.25;
    }
}
