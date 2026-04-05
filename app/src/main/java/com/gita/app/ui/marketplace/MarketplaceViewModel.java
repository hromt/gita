package com.gita.app.ui.marketplace;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.gita.app.data.local.entity.User;
import com.gita.app.data.local.model.ItemWithOwner;
import com.gita.app.data.repository.ItemRepository;
import com.gita.app.data.repository.UserRepository;

import java.util.List;

/**
 * Combines marketplace query results with profile + average value to produce recommendation buckets.
 */
public class MarketplaceViewModel extends AndroidViewModel {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final MutableLiveData<Filter> filter = new MutableLiveData<>(new Filter(false, ""));

    private final LiveData<List<ItemWithOwner>> rawMarketplace;
    private final LiveData<User> currentUser;

    private List<ItemWithOwner> lastItems;
    private User lastUser;

    private final MediatorLiveData<MarketplaceDisplay> display = new MediatorLiveData<>();

    public MarketplaceViewModel(@NonNull Application application) {
        super(application);
        itemRepository = new ItemRepository(application);
        userRepository = new UserRepository(application);

        rawMarketplace = Transformations.switchMap(filter, f ->
                itemRepository.observeMarketplace(f.includeNonTrade, f.query));
        currentUser = userRepository.observeCurrentUser();

        display.addSource(rawMarketplace, items -> {
            lastItems = items;
            push();
        });
        display.addSource(currentUser, user -> {
            lastUser = user;
            push();
        });
    }

    private void push() {
        if (lastItems == null) {
            display.setValue(MarketplaceDisplay.empty());
            return;
        }
        itemRepository.averageMyInventoryValue(avg ->
                display.postValue(RecommendationEngine.partition(lastItems, lastUser, avg)));
    }

    public LiveData<MarketplaceDisplay> getDisplay() {
        return display;
    }

    public void setIncludeNonTrade(boolean include) {
        Filter f = filter.getValue();
        filter.setValue(new Filter(include, f != null ? f.query : ""));
    }

    public void setSearchQuery(String q) {
        Filter f = filter.getValue();
        filter.setValue(new Filter(f != null && f.includeNonTrade, q != null ? q : ""));
    }

    private static final class Filter {
        final boolean includeNonTrade;
        final String query;

        Filter(boolean includeNonTrade, String query) {
            this.includeNonTrade = includeNonTrade;
            this.query = query;
        }
    }
}
