package com.gita.app.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.gita.app.data.local.dao.ItemDao;
import com.gita.app.data.local.dao.TradeDao;
import com.gita.app.data.local.dao.UserDao;
import com.gita.app.data.local.entity.Item;
import com.gita.app.data.local.entity.Trade;
import com.gita.app.data.local.entity.TradeOfferedItem;
import com.gita.app.data.local.entity.TradeRequestedItem;
import com.gita.app.data.local.entity.User;

/**
 * Single Room database for offline-first storage. Azure App Service can later sync
 * these tables via REST; see {@link com.gita.app.data.remote.AzureSyncStub}.
 */
@Database(
        entities = {User.class, Item.class, Trade.class, TradeOfferedItem.class, TradeRequestedItem.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract ItemDao itemDao();

    public abstract TradeDao tradeDao();
}
