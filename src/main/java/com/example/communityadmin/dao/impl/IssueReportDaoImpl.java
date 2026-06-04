package com.example.communityadmin.dao.impl;

import com.example.communityadmin.dao.IssueReportDao;
import com.example.communityadmin.entity.IssueReport;
import com.example.communityadmin.util.DBUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IssueReportDaoImpl implements IssueReportDao {

    private IssueReport mapRow(ResultSet rs) throws SQLException {
        IssueReport ir = new IssueReport();
        ir.setId(rs.getInt("id"));
        ir.setResidentId(rs.getInt("resident_id"));
        ir.setCategory(rs.getString("category"));
        ir.setTitle(rs.getString("title"));
        ir.setDescription(rs.getString("description"));
        ir.setLocation(rs.getString("location"));
        ir.setStatus(rs.getString("status"));
        ir.setAssignedStaffId(rs.getObject("assigned_staff_id") != null ? rs.getInt("assigned_staff_id") : null);
        ir.setResolutionNotes(rs.getString("resolution_notes"));
        ir.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        ir.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        try { ir.setPhotoPath(rs.getString("photo_path")); } catch (SQLException ignored) {}
        try { ir.setResolutionPhotoPath(rs.getString("resolution_photo_path")); } catch (SQLException ignored) {}
        try { ir.setResidentName(rs.getString("resident_name")); } catch (SQLException ignored) {}
        try { ir.setAssignedStaffName(rs.getString("staff_name")); } catch (SQLException ignored) {}
        return ir;
    }

    @Override
    public List<IssueReport> findAll() {
        String sql = "SELECT ir.*, r.full_name AS resident_name, " +
                "u.full_name AS staff_name FROM issue_reports ir " +
                "JOIN residents r ON ir.resident_id = r.id " +
                "LEFT JOIN users u ON ir.assigned_staff_id = u.id " +
                "ORDER BY ir.created_at DESC";
        List<IssueReport> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<IssueReport> findByResidentId(int residentId) {
        String sql = "SELECT ir.*, r.full_name AS resident_name, " +
                "u.full_name AS staff_name FROM issue_reports ir " +
                "JOIN residents r ON ir.resident_id = r.id " +
                "LEFT JOIN users u ON ir.assigned_staff_id = u.id " +
                "WHERE ir.resident_id = ? ORDER BY ir.created_at DESC";
        List<IssueReport> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, residentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<IssueReport> findByStatus(String status) {
        String sql = "SELECT ir.*, r.full_name AS resident_name, " +
                "u.full_name AS staff_name FROM issue_reports ir " +
                "JOIN residents r ON ir.resident_id = r.id " +
                "LEFT JOIN users u ON ir.assigned_staff_id = u.id " +
                "WHERE ir.status = ? ORDER BY ir.created_at DESC";
        List<IssueReport> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<IssueReport> findByAssignedStaffId(int staffId) {
        String sql = "SELECT ir.*, r.full_name AS resident_name, " +
                "u.full_name AS staff_name FROM issue_reports ir " +
                "JOIN residents r ON ir.resident_id = r.id " +
                "LEFT JOIN users u ON ir.assigned_staff_id = u.id " +
                "WHERE ir.assigned_staff_id = ? ORDER BY ir.created_at DESC";
        List<IssueReport> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public IssueReport findById(int id) {
        String sql = "SELECT ir.*, r.full_name AS resident_name, " +
                "u.full_name AS staff_name FROM issue_reports ir " +
                "JOIN residents r ON ir.resident_id = r.id " +
                "LEFT JOIN users u ON ir.assigned_staff_id = u.id " +
                "WHERE ir.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(IssueReport report) {
        String sql = "INSERT INTO issue_reports (resident_id, category, title, description, location, photo_path) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, report.getResidentId());
            ps.setString(2, report.getCategory());
            ps.setString(3, report.getTitle());
            ps.setString(4, report.getDescription());
            ps.setString(5, report.getLocation());
            ps.setString(6, report.getPhotoPath());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateStatus(int id, String status, Integer assignedStaffId,
                             String resolutionNotes, String resolutionPhotoPath) {
        String sql = "UPDATE issue_reports SET status = ?, " +
                "assigned_staff_id = COALESCE(?, assigned_staff_id), " +
                "resolution_notes = COALESCE(?, resolution_notes), " +
                "resolution_photo_path = COALESCE(?, resolution_photo_path) WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setObject(2, assignedStaffId);
            ps.setString(3, resolutionNotes);
            ps.setString(4, resolutionPhotoPath);
            ps.setInt(5, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM issue_reports";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM issue_reports WHERE status = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}