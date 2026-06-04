package com.example.communityadmin.dao;

import com.example.communityadmin.entity.User;
import java.util.List;

public interface UserDao {
    User findByUsername(String username);
    User findById(int id);
    List<User> findAll();
    List<User> findByRole(String role);
    void save(User user);
    void update(User user);
    void delete(int id);
    boolean existsByUsername(String username);
    void updateActiveStatus(int id, boolean isActive);
}