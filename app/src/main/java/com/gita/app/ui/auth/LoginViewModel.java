package com.gita.app.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gita.app.data.repository.UserRepository;

/**
 * Validates credentials against Room and opens a local session.
 */
public class LoginViewModel extends AndroidViewModel {

    private final UserRepository userRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void login(String username, String password, UserRepository.ResultCallback<String> callback) {
        userRepository.login(username, password, callback);
    }
}
