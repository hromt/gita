package com.gita.app.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gita.app.data.local.entity.User;
import com.gita.app.data.repository.UserRepository;

/**
 * Profile fields and logout for the current session user.
 */
public class ProfileViewModel extends AndroidViewModel {

    private final UserRepository userRepository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<User> getUser() {
        return userRepository.observeCurrentUser();
    }

    public void saveProfile(String interest, String wishlist, String contact, Runnable onDone) {
        userRepository.updateProfile(interest, wishlist, contact, onDone);
    }

    public void logout() {
        userRepository.logout();
    }
}
