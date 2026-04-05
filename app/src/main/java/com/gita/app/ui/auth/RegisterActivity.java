package com.gita.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gita.app.R;
import com.gita.app.data.local.entity.User;
import com.gita.app.ui.main.MainActivity;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Creates a {@link User} row and signs the user in on success.
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        RegisterViewModel vm = new ViewModelProvider(this).get(RegisterViewModel.class);

        TextInputEditText u = findViewById(R.id.inputUsername);
        TextInputEditText p = findViewById(R.id.inputPassword);
        TextInputEditText c = findViewById(R.id.inputCollection);
        TextInputEditText w = findViewById(R.id.inputWishlist);
        TextInputEditText contact = findViewById(R.id.inputContact);

        findViewById(R.id.buttonRegister).setOnClickListener(v -> {
            User user = new User();
            user.username = text(u);
            user.password = text(p);
            user.collectionInterest = text(c);
            user.wishlist = text(w);
            user.contactInfo = text(contact);
            if (user.username.isEmpty() || user.password.isEmpty()) {
                Toast.makeText(this, R.string.username, Toast.LENGTH_SHORT).show();
                return;
            }
            vm.register(user, err -> runOnUiThread(() -> {
                if (err != null) {
                    Toast.makeText(RegisterActivity.this, err, Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            }));
        });
    }

    private static String text(TextInputEditText e) {
        return e.getText() != null ? e.getText().toString().trim() : "";
    }
}
