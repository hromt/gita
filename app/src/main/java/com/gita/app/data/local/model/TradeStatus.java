package com.gita.app.data.local.model;

/**
 * Persisted as plain strings in {@link com.gita.app.data.local.entity.Trade#status}.
 */
public final class TradeStatus {
    public static final String PENDING = "PENDING";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REJECTED = "REJECTED";

    private TradeStatus() {
    }
}
