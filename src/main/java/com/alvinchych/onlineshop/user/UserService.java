package com.alvinchych.onlineshop.user;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.alvinchych.onlineshop.entity.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public User registerUser(User user) {
        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Encrypt password
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return saveUser(user);
    }

    public Optional<User> authenticatUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && bCryptPasswordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Cache the result of finding a user by ID
    @Cacheable(value = "users", key = "#id")
    public Optional<User> getUserById(Long id) {
        System.out.println("Fetching user from the database...");
        return userRepository.findById(id);
    }

    // Update the cache when a user is saved
    @CachePut(value = "users", key = "#user.id")
    private User saveUser(User user) {
        return userRepository.save(user);
    }

    // Remove a user from the cache when deleted
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Clear all users from the cache
    @CacheEvict(value = "users", allEntries = true)
    public void clearCache() {
        System.out.println("Clearing all user cache...");
    }
}