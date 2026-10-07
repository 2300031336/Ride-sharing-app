package com.ridesharing.backend.controller;

import com.ridesharing.backend.model.User;
import com.ridesharing.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.List;

// Simple DTO to avoid leaking password hashes
record UserDto(Long user_id, String username, String email, java.time.LocalDateTime created_at, java.time.LocalDateTime last_login) {
    static UserDto from(User u){
        return new UserDto(u.getUser_id(), u.getUsername(), u.getEmail(), u.getCreated_at(), u.getLast_login());
    }
}

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");

        if (username == null || email == null || password == null) {
            return Map.of("error", "Missing required fields");
        }

        if (userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent()) {
            return Map.of("error", "Username or email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword_hash(passwordEncoder.encode(password));
        userRepository.save(user);

        return Map.of("user_id", user.getUser_id());
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Map.of("error", "Invalid username or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword_hash())) {
            return Map.of("error", "Invalid username or password");
        }

        user.setLast_login(java.time.LocalDateTime.now());
        userRepository.save(user);

        return Map.of("message", "Login successful", "user_id", user.getUser_id(), "username", user.getUsername(), "email", user.getEmail());
    }

    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String newPassword = body.get("newPassword");

        if (username == null || username.isBlank() || newPassword == null || newPassword.isBlank()) {
            return Map.of("error", "Missing required fields: username, newPassword");
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Map.of("error", "User not found");
        }

        if (newPassword.length() < 8) {
            return Map.of("error", "Password must be at least 8 characters");
        }

        User user = userOpt.get();
        user.setPassword_hash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return Map.of("message", "Password updated");
    }

    // List all users (sanitized) - for development/debug only
    @GetMapping("/users")
    public List<UserDto> allUsers(){
        return userRepository.findAll().stream().map(UserDto::from).toList();
    }

    // Get a single user by id
    @GetMapping("/users/{id}")
    public Object getUser(@PathVariable Long id){
        return userRepository.findById(id)
            .<Object>map(UserDto::from)
            .orElseGet(() -> Map.of("error", "User not found"));
    }
}