package org.example.repository;

import org.example.db.DatabaseManager;
import org.example.model.NotificationSettings;

import java.sql.*;

public class NotificationSettingsRepository {

    private final DatabaseManager databaseManager;

    public NotificationSettingsRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public NotificationSettings load() {

        String sql = "SELECT * FROM notification_settings WHERE id = 1";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {

                NotificationSettings s = new NotificationSettings();

                s.setKeyword(rs.getString("keyword"));

                int minSalary = rs.getInt("min_salary");
                if (!rs.wasNull()) {
                    s.setMinSalary(minSalary);
                }

                s.setCity(rs.getString("city"));

                return s;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new NotificationSettings();
    }

    public void save(NotificationSettings s) {

        String sql = """
            INSERT INTO notification_settings (id, keyword, min_salary, city)
            VALUES (1, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                keyword = excluded.keyword,
                min_salary = excluded.min_salary,
                city = excluded.city
        """;

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getKeyword());

            if (s.getMinSalary() == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, s.getMinSalary());
            }

            stmt.setString(3, s.getCity());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}