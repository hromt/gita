package com.gita.app.ui.inventory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gita.app.data.local.entity.Item;
import com.gita.app.data.repository.ItemRepository;

import java.util.List;

/**
 * CRUD-backed inventory list for the signed-in collector.
 */
public class InventoryViewModel extends AndroidViewModel {

    private final ItemRepository itemRepository;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        itemRepository = new ItemRepository(application);
    }

    public LiveData<List<Item>> getInventory() {
        return itemRepository.observeMyInventory();
    }

    public void delete(Item item, Runnable onDone) {
        itemRepository.delete(item, onDone);
    }
}
