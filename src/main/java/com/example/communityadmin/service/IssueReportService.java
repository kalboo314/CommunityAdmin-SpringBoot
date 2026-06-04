package com.example.communityadmin.service;

import com.example.communityadmin.entity.IssueReport;
import java.util.List;

public interface IssueReportService {
    List<IssueReport> findAll();
    List<IssueReport> findByResidentId(int residentId);
    List<IssueReport> findByStatus(String status);
    List<IssueReport> findByAssignedStaffId(int staffId);
    IssueReport findById(int id);
    void submit(IssueReport report);
    void assign(int id, int staffId);
    void updateStatus(int id, String status, String resolutionNotes);
    int countAll();
    int countByStatus(String status);
}