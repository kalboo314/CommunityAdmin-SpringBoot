package com.example.communityadmin.service;

import com.example.communityadmin.entity.DocumentRequest;
import java.util.List;

public interface DocumentRequestService {
    List<DocumentRequest> findAll();
    List<DocumentRequest> findByResidentId(int residentId);
    List<DocumentRequest> findByStatus(String status);
    DocumentRequest findById(int id);
    void submit(DocumentRequest request);
    void process(int id, int staffId);
    void approve(int id, int adminId);
    void reject(int id, int adminId, String reason);
    void markReady(int id);
    int countAll();
    int countByStatus(String status);
}