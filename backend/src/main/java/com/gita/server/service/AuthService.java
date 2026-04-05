package com.gita.server.service;

import com.gita.server.config.JwtService;
import com.gita.server.dto.AuthResponse;
import com.gita.server.dto.LoginRequest;
import com.gita.server.dto.RegisterRequest;
import com.gita.server.entity.User;
import com.gita.server.repository.UserEntityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserEntityRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserEntityRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String username = req.username().trim();
        if (users.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setCollectionInterest(trimOrEmpty(req.collectionInterest()));
        u.setWishlist(trimOrEmpty(req.wishlist()));
        u.setContactInfo(trimOrEmpty(req.contactInfo()));
        u = users.save(u);
        String token = jwtService.createToken(u.getId());
        return new AuthResponse(token, u.getId(), u.getUsername());
    }

    public AuthResponse login(LoginRequest req) {
        User u = users.findByUsernameIgnoreCase(req.username().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        String token = jwtService.createToken(u.getId());
        return new AuthResponse(token, u.getId(), u.getUsername());
    }

    private static String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }
}
