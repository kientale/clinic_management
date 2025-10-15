package com.kien.project.clinicmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.kien.project.clinicmanagement.model.SystemLog;
import com.kien.project.clinicmanagement.utils.ConnectionDatabase;


public class SystemLogDAO {

    public void logAction(String userCode, String action) {
        String sql = "INSERT INTO system_log (user_code, action) VALUES (?, ?)";

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userCode);
            stmt.setString(2, action);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<SystemLog> getLogsByUserCode(String userCode) {
        List<SystemLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM system_log WHERE user_code = ? ORDER BY timestamp DESC";

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userCode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                SystemLog log = new SystemLog();
                log.setId(rs.getLong("id"));
                log.setUserCode(rs.getString("user_code"));
                log.setAction(rs.getString("action"));
                log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                logs.add(log);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return logs;
    }
}