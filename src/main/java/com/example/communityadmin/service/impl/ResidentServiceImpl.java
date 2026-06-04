package com.example.communityadmin.service.impl;

import com.example.communityadmin.dao.ResidentDao;
import com.example.communityadmin.entity.Resident;
import com.example.communityadmin.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResidentServiceImpl implements ResidentService {

    @Autowired
    private ResidentDao residentDao;

    @Override
    public List<Resident> findAll() {
        return residentDao.findAll();
    }

    @Override
    public List<Resident> searchByKeyword(String keyword) {
        return residentDao.searchByKeyword(keyword);
    }

    @Override
    public Resident findById(int id) {
        return residentDao.findById(id);
    }

    @Override
    public Resident findByUserId(int userId) {
        return residentDao.findByUserId(userId);
    }

    @Override
    public void save(Resident resident) {
        if (residentDao.existsByNik(resident.getNik())) {
            throw new RuntimeException("NIK already registered");
        }
        residentDao.save(resident);
    }

    @Override
    public void update(Resident resident) {
        residentDao.update(resident);
    }

    @Override
    public void delete(int id) {
        residentDao.delete(id);
    }

    @Override
    public int countAll() {
        return residentDao.countAll();
    }
}