package com.example.communityadmin.service.impl;

import com.example.communityadmin.dao.IssueReportDao;
import com.example.communityadmin.entity.IssueReport;
import com.example.communityadmin.service.IssueReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueReportServiceImpl implements IssueReportService {

    @Autowired
    private IssueReportDao issueReportDao;

    @Override
    public List<IssueReport> findAll() {
        return issueReportDao.findAll();
    }

    @Override
    public List<IssueReport> findByResidentId(int residentId) {
        return issueReportDao.findByResidentId(residentId);
    }

    @Override
    public List<IssueReport> findByStatus(String status) {
        return issueReportDao.findByStatus(status);
    }

    @Override
    public List<IssueReport> findByAssignedStaffId(int staffId) {
        return issueReportDao.findByAssignedStaffId(staffId);
    }

    @Override
    public IssueReport findById(int id) {
        return issueReportDao.findById(id);
    }

    @Override
    public void submit(IssueReport report) {
        issueReportDao.save(report);
    }

    @Override
    public void assign(int id, int staffId) {
        IssueReport report = issueReportDao.findById(id);
        if (report == null) {
            throw new RuntimeException("Report not found");
        }
        issueReportDao.updateStatus(id, "Under Review", staffId, null, null);
    }

    @Override
    public void updateStatus(int id, String status, String resolutionNotes, String resolutionPhotoPath, Integer staffId) {
        IssueReport report = issueReportDao.findById(id);
        if (report == null) {
            throw new RuntimeException("Report not found");
        }
        issueReportDao.updateStatus(id, status, staffId, resolutionNotes, resolutionPhotoPath);
    }

    @Override
    public int countAll() {
        return issueReportDao.countAll();
    }

    @Override
    public int countByStatus(String status) {
        return issueReportDao.countByStatus(status);
    }
}