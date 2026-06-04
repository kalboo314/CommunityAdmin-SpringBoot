package com.example.communityadmin.service;

import com.example.communityadmin.entity.User;
import java.util.List;

public interface UserService {
    User login(String username, String password);
    void register(User user);
    User findById(int id);
    List<User> findAll();
    List<User> findByRole(String role);
    void update(User user);
    void delete(int id);
    void updateActiveStatus(int id, boolean isActive);
}