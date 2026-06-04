package com.example.communityadmin.dao;

import com.example.communityadmin.entity.Resident;
import java.util.List;

public interface ResidentDao {
    List<Resident> findAll();
    List<Resident> searchByKeyword(String keyword);
    Resident findById(int id);
    Resident findByUserId(int userId);
    Resident findByNik(String nik);
    void save(Resident resident);
    void update(Resident resident);
    void delete(int id);
    boolean existsByNik(String nik);
    int countAll();
}