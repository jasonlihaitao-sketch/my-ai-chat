package com.example.service;

import com.example.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User register(User user);
    User findByUsername(String username);
    User findByUserId(String userId);
    User updateUser(User user);
    void deleteUser(Long id);
}