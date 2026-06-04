package com.example.communityadmin.service.impl;

import com.example.communityadmin.dao.DocumentRequestDao;
import com.example.communityadmin.entity.DocumentRequest;
import com.example.communityadmin.service.DocumentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentRequestServiceImpl implements DocumentRequestService {

    @Autowired
    private DocumentRequestDao documentRequestDao;

    @Override
    public List<DocumentRequest> findAll() {
        return documentRequestDao.findAll();
    }

    @Override
    public List<DocumentRequest> findByResidentId(int residentId) {
        return documentRequestDao.findByResidentId(residentId);
    }

    @Override
    public List<DocumentRequest> findByStatus(String status) {
        return documentRequestDao.findByStatus(status);
    }

    @Override
    public DocumentRequest findById(int id) {
        return documentRequestDao.findById(id);
    }

    @Override
    public void submit(DocumentRequest request) {
        documentRequestDao.save(request);
    }

    @Override
    public void process(int id, int staffId) {
        DocumentRequest request = documentRequestDao.findById(id);
        if (request == null) {
            throw new RuntimeException("Request not found");
        }
        if (!request.getStatus().equals("Submitted")) {
            throw new RuntimeException("Request cannot be processed at this stage");
        }
        documentRequestDao.updateStatus(id, "In Progress", staffId, null, null);
    }

    @Override
    public void approve(int id, int adminId) {
        DocumentRequest request = documentRequestDao.findById(id);
        if (request == null) {
            throw new RuntimeException("Request not found");
        }
        if (!request.getStatus().equals("In Progress")) {
            throw new RuntimeException("Request must be In Progress before approval");
        }
        documentRequestDao.updateStatus(id, "Approved", null, adminId, null);
    }

    @Override
    public void reject(int id, int adminId, String reason) {
        DocumentRequest request = documentRequestDao.findById(id);
        if (request == null) {
            throw new RuntimeException("Request not found");
        }
        documentRequestDao.updateStatus(id, "Rejected", null, adminId, reason);
    }

    @Override
    public void markReady(int id) {
        documentRequestDao.updateStatus(id, "Ready", null, null, null);
    }

    @Override
    public int countAll() {
        return documentRequestDao.countAll();
    }

    @Override
    public int countByStatus(String status) {
        return documentRequestDao.countByStatus(status);
    }
}