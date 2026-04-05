package com.gita.app.ui.inventory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gita.app.R;
import com.gita.app.data.local.entity.Item;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Form for adding or editing an {@link Item}; optional gallery URI for display elsewhere.
 */
public class AddEditItemActivity extends AppCompatActivity {

    private static final String EXTRA_ITEM_ID = "item_id";

    private AddEditItemViewModel viewModel;
    private boolean editMode;
    private long itemId;
    private String pendingImageUri;
    /** Retained on edit so ownerId is preserved when saving. */
    private Item loaded;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    pendingImageUri = uri.toString();
                    Toast.makeText(this, R.string.pick_image, Toast.LENGTH_SHORT).show();
                }
            });

    public static Intent createIntent(Context context) {
        return new Intent(context, AddEditItemActivity.class);
    }

    public static Intent editIntent(Context context, long itemId) {
        Intent i = new Intent(context, AddEditItemActivity.class);
        i.putExtra(EXTRA_ITEM_ID, itemId);
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);
        viewModel = new ViewModelProvider(this).get(AddEditItemViewModel.class);
        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        editMode = itemId > 0;

        TextInputEditText name = findViewById(R.id.inputName);
        TextInputEditText desc = findViewById(R.id.inputDescription);
        TextInputEditText cat = findViewById(R.id.inputCategory);
        TextInputEditText sub = findViewById(R.id.inputSubcategory);
        TextInputEditText val = findViewById(R.id.inputValue);
        SwitchMaterial sw = findViewById(R.id.switchTrade);

        if (editMode) {
            viewModel.load(itemId, item -> runOnUiThread(() -> {
                if (item == null) {
                    finish();
                    return;
                }
                loaded = item;
                name.setText(item.name);
                desc.setText(item.description);
                cat.setText(item.category);
                sub.setText(item.subcategory);
                val.setText(String.valueOf(item.estimatedValue));
                sw.setChecked(item.isLookingToTrade);
                pendingImageUri = item.imageUri;
            }));
        }

        findViewById(R.id.buttonPickImage).setOnClickListener(v -> pickImage.launch("image/*"));

        findViewById(R.id.buttonSave).setOnClickListener(v -> {
            Item item = new Item();
            if (editMode && loaded != null) {
                item.id = loaded.id;
                item.ownerId = loaded.ownerId;
            }
            item.name = text(name);
            item.description = text(desc);
            item.category = text(cat);
            item.subcategory = text(sub);
            try {
                item.estimatedValue = Double.parseDouble(text(val));
            } catch (NumberFormatException e) {
                item.estimatedValue = 0;
            }
            item.isLookingToTrade = sw.isChecked();
            item.imageUri = pendingImageUri;
            viewModel.save(item, editMode, this::finish);
        });
    }

    private static String text(TextInputEditText e) {
        return e.getText() != null ? e.getText().toString().trim() : "";
    }
}
