package com.example.communityadmin.dao;

import com.example.communityadmin.entity.DocumentRequest;
import java.util.List;

public interface DocumentRequestDao {
    List<DocumentRequest> findAll();
    List<DocumentRequest> findByResidentId(int residentId);
    List<DocumentRequest> findByStatus(String status);
    DocumentRequest findById(int id);
    void save(DocumentRequest request);
    void updateStatus(int id, String status, Integer staffId, Integer adminId, String rejectionReason);
    int countAll();
    int countByStatus(String status);
}