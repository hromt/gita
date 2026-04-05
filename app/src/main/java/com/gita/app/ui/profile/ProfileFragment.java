package com.gita.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gita.app.R;
import com.gita.app.ui.auth.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Edits wishlist/interests (drives recommendations) and ends the local session.
 */
public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        TextInputEditText collection = view.findViewById(R.id.inputCollection);
        TextInputEditText wishlist = view.findViewById(R.id.inputWishlist);
        TextInputEditText contact = view.findViewById(R.id.inputContact);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                return;
            }
            collection.setText(user.collectionInterest);
            wishlist.setText(user.wishlist);
            contact.setText(user.contactInfo);
        });

        view.findViewById(R.id.buttonSave).setOnClickListener(v ->
                viewModel.saveProfile(
                        text(collection),
                        text(wishlist),
                        text(contact),
                        () -> requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), R.string.save, Toast.LENGTH_SHORT).show())));

        view.findViewById(R.id.buttonLogout).setOnClickListener(v -> {
            viewModel.logout();
            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            requireActivity().finish();
        });
    }

    private static String text(TextInputEditText e) {
        return e.getText() != null ? e.getText().toString().trim() : "";
    }
}
