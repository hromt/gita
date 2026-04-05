package com.gita.server.web;

import com.gita.server.dto.UpdateProfileRequest;
import com.gita.server.dto.UserDto;
import com.gita.server.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDto me() {
        return userService.getMe();
    }

    @PatchMapping("/me")
    public UserDto updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(request);
    }
}
