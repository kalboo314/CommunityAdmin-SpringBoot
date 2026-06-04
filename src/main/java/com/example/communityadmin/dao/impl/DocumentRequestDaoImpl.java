package com.example.communityadmin.dao.impl;

import com.example.communityadmin.dao.DocumentRequestDao;
import com.example.communityadmin.entity.DocumentRequest;
import com.example.communityadmin.util.DBUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DocumentRequestDaoImpl implements DocumentRequestDao {

    private DocumentRequest mapRow(ResultSet rs) throws SQLException {
        DocumentRequest dr = new DocumentRequest();
        dr.setId(rs.getInt("id"));
        dr.setResidentId(rs.getInt("resident_id"));
        dr.setDocumentType(rs.getString("document_type"));
        dr.setPurpose(rs.getString("purpose"));
        dr.setStatus(rs.getString("status"));
        dr.setStaffId(rs.getObject("staff_id") != null ? rs.getInt("staff_id") : null);
        dr.setAdminId(rs.getObject("admin_id") != null ? rs.getInt("admin_id") : null);
        dr.setRejectionReason(rs.getString("rejection_reason"));
        dr.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        dr.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        try { dr.setResidentName(rs.getString("resident_name")); } catch (SQLException ignored) {}
        try { dr.setResidentNik(rs.getString("nik")); } catch (SQLException ignored) {}
        try { dr.setStaffName(rs.getString("staff_name")); } catch (SQLException ignored) {}
        return dr;
    }

    @Override
    public List<DocumentRequest> findAll() {
        String sql = "SELECT dr.*, r.full_name AS resident_name, r.nik, " +
                "u.full_name AS staff_name FROM document_requests dr " +
                "JOIN residents r ON dr.resident_id = r.id " +
                "LEFT JOIN users u ON dr.staff_id = u.id " +
                "ORDER BY dr.created_at DESC";
        List<DocumentRequest> list = new ArrayList<>();
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
    public List<DocumentRequest> findByResidentId(int residentId) {
        String sql = "SELECT dr.*, r.full_name AS resident_name, r.nik, " +
                "u.full_name AS staff_name FROM document_requests dr " +
                "JOIN residents r ON dr.resident_id = r.id " +
                "LEFT JOIN users u ON dr.staff_id = u.id " +
                "WHERE dr.resident_id = ? ORDER BY dr.created_at DESC";
        List<DocumentRequest> list = new ArrayList<>();
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
    public List<DocumentRequest> findByStatus(String status) {
        String sql = "SELECT dr.*, r.full_name AS resident_name, r.nik, " +
                "u.full_name AS staff_name FROM document_requests dr " +
                "JOIN residents r ON dr.resident_id = r.id " +
                "LEFT JOIN users u ON dr.staff_id = u.id " +
                "WHERE dr.status = ? ORDER BY dr.created_at DESC";
        List<DocumentRequest> list = new ArrayList<>();
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
    public DocumentRequest findById(int id) {
        String sql = "SELECT dr.*, r.full_name AS resident_name, r.nik, " +
                "u.full_name AS staff_name FROM document_requests dr " +
                "JOIN residents r ON dr.resident_id = r.id " +
                "LEFT JOIN users u ON dr.staff_id = u.id " +
                "WHERE dr.id = ?";
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
    public void save(DocumentRequest request) {
        String sql = "INSERT INTO document_requests (resident_id, document_type, purpose) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, request.getResidentId());
            ps.setString(2, request.getDocumentType());
            ps.setString(3, request.getPurpose());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateStatus(int id, String status, Integer staffId,
                             Integer adminId, String rejectionReason) {
        String sql = "UPDATE document_requests SET status = ?, staff_id = COALESCE(?, staff_id), " +
                "admin_id = COALESCE(?, admin_id), rejection_reason = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setObject(2, staffId);
            ps.setObject(3, adminId);
            ps.setString(4, rejectionReason);
            ps.setInt(5, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM document_requests";
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
        String sql = "SELECT COUNT(*) FROM document_requests WHERE status = ?";
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