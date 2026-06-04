package com.example.communityadmin.service.impl;

import com.example.communityadmin.dao.UserDao;
import com.example.communityadmin.entity.User;
import com.example.communityadmin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && user.getPassword().equals(password) && user.isActive()) {
            return user;
        }
        return null;
    }

    @Override
    public void register(User user) {
        if (userDao.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        userDao.save(user);
    }

    @Override
    public User findById(int id) {
        return userDao.findById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public List<User> findByRole(String role) {
        return userDao.findByRole(role);
    }

    @Override
    public List<User> findPending() {
        return userDao.findByActiveStatus(false);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Override
    public void delete(int id) {
        userDao.delete(id);
    }

    @Override
    public void updateActiveStatus(int id, boolean isActive) {
        userDao.updateActiveStatus(id, isActive);
    }
}