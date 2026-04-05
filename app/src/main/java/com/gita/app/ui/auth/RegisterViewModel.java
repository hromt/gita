package com.gita.app.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gita.app.data.local.entity.User;
import com.gita.app.data.repository.UserRepository;

/**
 * Persists a new {@link User} row if the username is unique.
 */
public class RegisterViewModel extends AndroidViewModel {

    private final UserRepository userRepository;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void register(User user, UserRepository.ResultCallback<String> callback) {
        userRepository.register(user, callback);
    }
}
