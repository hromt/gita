package com.gita.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gita.app.R;
import com.gita.app.data.SessionManager;
import com.gita.app.ui.main.MainActivity;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Local credential check against Room; successful login stores {@link SessionManager} user id.
 */
public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            goMain();
            return;
        }
        setContentView(R.layout.activity_login);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        TextInputEditText user = findViewById(R.id.inputUsername);
        TextInputEditText pass = findViewById(R.id.inputPassword);
        findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            String u = user.getText() != null ? user.getText().toString().trim() : "";
            String p = pass.getText() != null ? pass.getText().toString() : "";
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, R.string.username, Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.login(u, p, err -> runOnUiThread(() -> {
                if (err != null) {
                    Toast.makeText(LoginActivity.this, err, Toast.LENGTH_LONG).show();
                } else {
                    goMain();
                }
            }));
        });
        findViewById(R.id.buttonGoRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void goMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
