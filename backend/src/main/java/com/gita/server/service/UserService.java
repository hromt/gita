package com.gita.server.service;

import com.gita.server.dto.UpdateProfileRequest;
import com.gita.server.dto.UserDto;
import com.gita.server.entity.User;
import com.gita.server.repository.UserEntityRepository;
import com.gita.server.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserEntityRepository users;

    public UserService(UserEntityRepository users) {
        this.users = users;
    }

    public UserDto getMe() {
        long id = CurrentUser.id();
        User u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toDto(u);
    }

    @Transactional
    public UserDto updateProfile(UpdateProfileRequest req) {
        long id = CurrentUser.id();
        User u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (req.collectionInterest() != null) {
            u.setCollectionInterest(req.collectionInterest());
        }
        if (req.wishlist() != null) {
            u.setWishlist(req.wishlist());
        }
        if (req.contactInfo() != null) {
            u.setContactInfo(req.contactInfo());
        }
        users.save(u);
        return toDto(u);
    }

    public User requireUser(long id) {
        return users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private static UserDto toDto(User u) {
        return new UserDto(
                u.getId(),
                u.getUsername(),
                u.getCollectionInterest(),
                u.getWishlist(),
                u.getContactInfo()
        );
    }
}
