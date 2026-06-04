package com.example.communityadmin.dao;

import com.example.communityadmin.entity.IssueReport;
import java.util.List;

public interface IssueReportDao {
    List<IssueReport> findAll();
    List<IssueReport> findByResidentId(int residentId);
    List<IssueReport> findByStatus(String status);
    List<IssueReport> findByAssignedStaffId(int staffId);
    IssueReport findById(int id);
    void save(IssueReport report);
    void updateStatus(int id, String status, Integer assignedStaffId, String resolutionNotes, String resolutionPhotoPath);
    int countAll();
    int countByStatus(String status);
}