package com.example.communityadmin.dao.impl;

import com.example.communityadmin.dao.ResidentDao;
import com.example.communityadmin.entity.Resident;
import com.example.communityadmin.util.DBUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ResidentDaoImpl implements ResidentDao {

    private Resident mapRow(ResultSet rs) throws SQLException {
        Resident r = new Resident();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setNik(rs.getString("nik"));
        r.setFullName(rs.getString("full_name"));
        r.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        r.setGender(rs.getString("gender"));
        r.setAddress(rs.getString("address"));
        r.setReligion(rs.getString("religion"));
        r.setOccupation(rs.getString("occupation"));
        r.setMaritalStatus(rs.getString("marital_status"));
        r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return r;
    }

    @Override
    public List<Resident> findAll() {
        String sql = "SELECT * FROM residents ORDER BY full_name";
        List<Resident> list = new ArrayList<>();
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
    public List<Resident> searchByKeyword(String keyword) {
        String sql = "SELECT * FROM residents WHERE full_name LIKE ? OR nik LIKE ?";
        List<Resident> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Resident findById(int id) {
        String sql = "SELECT * FROM residents WHERE id = ?";
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
    public Resident findByUserId(int userId) {
        String sql = "SELECT * FROM residents WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Resident findByNik(String nik) {
        String sql = "SELECT * FROM residents WHERE nik = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nik);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(Resident resident) {
        String sql = "INSERT INTO residents (user_id, nik, full_name, date_of_birth, gender, address, religion, occupation, marital_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, resident.getUserId() == 0 ? null : resident.getUserId());
            ps.setString(2, resident.getNik());
            ps.setString(3, resident.getFullName());
            ps.setDate(4, Date.valueOf(resident.getDateOfBirth()));
            ps.setString(5, resident.getGender());
            ps.setString(6, resident.getAddress());
            ps.setString(7, resident.getReligion());
            ps.setString(8, resident.getOccupation());
            ps.setString(9, resident.getMaritalStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Resident resident) {
        String sql = "UPDATE residents SET nik = ?, full_name = ?, date_of_birth = ?, gender = ?, address = ?, religion = ?, occupation = ?, marital_status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resident.getNik());
            ps.setString(2, resident.getFullName());
            ps.setDate(3, Date.valueOf(resident.getDateOfBirth()));
            ps.setString(4, resident.getGender());
            ps.setString(5, resident.getAddress());
            ps.setString(6, resident.getReligion());
            ps.setString(7, resident.getOccupation());
            ps.setString(8, resident.getMaritalStatus());
            ps.setInt(9, resident.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM residents WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsByNik(String nik) {
        String sql = "SELECT COUNT(*) FROM residents WHERE nik = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nik);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM residents";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}