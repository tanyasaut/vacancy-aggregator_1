package org.example.repository;

import org.example.db.DatabaseManager;
import org.example.model.HistoryRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryRepository {

    private final DatabaseManager databaseManager;

    public HistoryRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(HistoryRecord record) {

        String sql =
                """
               
                        INSERT INTO history
                                (
                                    action,
                                    vacancy_url,
                                    action_date
                                )
                                VALUES (?, ?, ?)
                """;

        try (
                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    record.getAction());

            statement.setString(
                    2,
                    record.getVacancyUrl());

            statement.setString(
                    3,
                    record.getActionDate());

            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public List<HistoryRecord> findAll() {

        List<HistoryRecord> history =
                new ArrayList<>();

        String sql =
                "SELECT * FROM history";

        try (
                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet rs =
                        statement.executeQuery()
        ) {

            while (rs.next()) {

                HistoryRecord record =
                        new HistoryRecord();

                record.setId(
                        rs.getInt("id"));

                record.setAction(
                        rs.getString("action"));

                record.setVacancyUrl(
                        rs.getString("vacancy_url"));

                record.setActionDate(
                        rs.getString("action_date"));

                history.add(record);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return history;
    }
}