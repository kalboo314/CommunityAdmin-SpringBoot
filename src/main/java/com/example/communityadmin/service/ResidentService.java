package com.example.communityadmin.service;

import com.example.communityadmin.entity.Resident;
import java.util.List;

public interface ResidentService {
    List<Resident> findAll();
    List<Resident> searchByKeyword(String keyword);
    Resident findById(int id);
    Resident findByUserId(int userId);
    void save(Resident resident);
    void update(Resident resident);
    void delete(int id);
    int countAll();
}