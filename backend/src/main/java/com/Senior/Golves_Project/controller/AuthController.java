package com.Senior.Golves_Project.controller;

import com.Senior.Golves_Project.dto.LoginRequest;
import com.Senior.Golves_Project.dto.UpdateProfileRequest;
import com.Senior.Golves_Project.dto.UserRequest;
import com.Senior.Golves_Project.dto.UserResponse;
import com.Senior.Golves_Project.entity.User;
import com.Senior.Golves_Project.repository.UserRepository;
import com.Senior.Golves_Project.service.UserService;
import com.Senior.Golves_Project.util.PasswordUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest request) {
        try {
            UserResponse response = userService.register(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage() != null ? e.getMessage() : "Register failed");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {

            User user = userRepository.findByEmail(request.getEmail())
                    .orElse(null);

            if (user == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Email not found");
                return ResponseEntity.badRequest().body(error);
            }

            String savedPassword = user.getPassword();
            String enteredPassword = request.getPassword();

            boolean correctPassword = false;

            if (savedPassword != null &&
                    savedPassword.equals(PasswordUtil.hashPassword(enteredPassword))) {

                correctPassword = true;

            } else if (savedPassword != null &&
                    savedPassword.equals(enteredPassword)) {

                correctPassword = true;

                user.setPassword(
                        PasswordUtil.hashPassword(enteredPassword)
                );

                userRepository.save(user);
            }

            if (!correctPassword) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Wrong password");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("userId", user.getId());
            response.put("fullName",
                    user.getFullName() != null ? user.getFullName() : "");
            response.put("email",
                    user.getEmail() != null ? user.getEmail() : "");
            response.put("phone",
                    user.getPhone() != null ? user.getPhone() : "");

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Map<String, Object> error = new HashMap<>();
            error.put("message",
                    e.getMessage() != null ? e.getMessage() : "Login failed");

            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody UpdateProfileRequest request) {

        try {

            UserResponse response =
                    userService.updateProfile(request);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Map<String, Object> error = new HashMap<>();
            error.put("message",
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Profile update failed");

            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long userId) {

        try {

            userService.deleteUser(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "User deleted successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Map<String, Object> error = new HashMap<>();
            error.put("message",
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Delete failed");

            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}