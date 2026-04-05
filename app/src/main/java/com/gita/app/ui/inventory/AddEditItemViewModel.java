package com.gita.app.ui.inventory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gita.app.data.local.entity.Item;
import com.gita.app.data.repository.ItemRepository;

/**
 * Persists new inventory rows or updates existing ones (including optional image URI).
 */
public class AddEditItemViewModel extends AndroidViewModel {

    private final ItemRepository itemRepository;

    public AddEditItemViewModel(@NonNull Application application) {
        super(application);
        itemRepository = new ItemRepository(application);
    }

    public void load(long itemId, ItemRepository.ResultCallback<Item> cb) {
        itemRepository.getById(itemId, cb);
    }

    public void save(Item item, boolean isEdit, Runnable onDone) {
        if (isEdit) {
            itemRepository.update(item, onDone);
        } else {
            itemRepository.insert(item, onDone);
        }
    }
}
