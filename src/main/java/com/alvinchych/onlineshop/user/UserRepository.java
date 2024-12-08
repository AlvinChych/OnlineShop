package com.alvinchych.onlineshop.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alvinchych.onlineshop.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
