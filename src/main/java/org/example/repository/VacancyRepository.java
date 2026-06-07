package org.example.repository;

import org.example.db.DatabaseManager;
import org.example.model.Vacancy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VacancyRepository {

    private final DatabaseManager databaseManager;

    public VacancyRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(Vacancy vacancy) {

        String sql = """
                INSERT OR IGNORE INTO vacancies
                (
                    title,
                    company,
                    city,
                    salary,
                    description,
                    requirements,
                    publish_date,
                    source_url,
                    schedule
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, vacancy.getTitle());
            statement.setString(2, vacancy.getCompany());
            statement.setString(3, vacancy.getCity());

            if (vacancy.getSalary() == null) {
                statement.setNull(4, Types.INTEGER);
            } else {
                statement.setInt(4, vacancy.getSalary());
            }

            statement.setString(5, vacancy.getDescription());
            statement.setString(6, vacancy.getRequirements());
            statement.setString(7, vacancy.getPublishDate());
            statement.setString(8, vacancy.getSourceUrl());
            statement.setString(9, vacancy.getSchedule());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Vacancy> findAll() {

        List<Vacancy> vacancies = new ArrayList<>();

        String sql = "SELECT * FROM vacancies";

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {
                vacancies.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vacancies;
    }

    public List<Vacancy> findByCity(String city) {

        List<Vacancy> vacancies = new ArrayList<>();

        String sql =
                "SELECT * FROM vacancies WHERE city LIKE ? COLLATE NOCASE";

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, "%" + city + "%");

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                vacancies.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vacancies;
    }

    public List<Vacancy> findBySchedule(String schedule) {

        List<Vacancy> vacancies = new ArrayList<>();

        String sql =
                """
                SELECT *
                FROM vacancies
                WHERE schedule LIKE ? COLLATE NOCASE
                """;

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, "%" + schedule + "%");

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                vacancies.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vacancies;
    }

    public List<Vacancy> findByDescriptionKeyword(String keyword) {

        List<Vacancy> vacancies = new ArrayList<>();

        String sql = """
        SELECT *
        FROM vacancies
        WHERE description LIKE ? COLLATE NOCASE
        """;

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, "%" + keyword + "%");

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                vacancies.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vacancies;
    }

    public List<Vacancy> searchByKeyword(String keyword) {
        List<Vacancy> vacancies = new ArrayList<>();


        String sql = """
        SELECT *
        FROM vacancies
        WHERE title LIKE ? COLLATE NOCASE
        OR description LIKE ? COLLATE NOCASE
        OR city LIKE ? COLLATE NOCASE
        OR company LIKE ? COLLATE NOCASE
        """;

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            String pattern = "%" + keyword + "%";
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            statement.setString(3, pattern);
            statement.setString(4, pattern);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                vacancies.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vacancies;
    }

    public List<Vacancy> sortBySalaryDesc() {

        List<Vacancy> vacancies = new ArrayList<>();

        String sql =
                """
                SELECT *
                FROM vacancies
                ORDER BY salary DESC
                """;

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {
                vacancies.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vacancies;
    }

    private Vacancy mapRow(ResultSet rs)
            throws SQLException {

        Vacancy vacancy = new Vacancy();

        vacancy.setId(
                rs.getInt("id"));

        vacancy.setTitle(
                rs.getString("title"));

        vacancy.setCompany(
                rs.getString("company"));

        vacancy.setCity(
                rs.getString("city"));

        Integer salary = (Integer) rs.getObject("salary");
        vacancy.setSalary(salary);

        vacancy.setDescription(
                rs.getString("description"));

        vacancy.setRequirements(
                rs.getString("requirements"));

        vacancy.setPublishDate(
                rs.getString("publish_date"));

        vacancy.setSourceUrl(
                rs.getString("source_url"));

        vacancy.setSchedule(rs.getString("schedule"));

        return vacancy;
    }

    public List<Vacancy> findByMinSalary(int minSalary) {

        List<Vacancy> vacancies =
                new ArrayList<>();

        String sql =
                """
                SELECT *
                FROM vacancies
                WHERE salary >= ?
                """;

        try (
                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, minSalary);

            ResultSet rs =
                    statement.executeQuery();

            while (rs.next()) {

                vacancies.add(mapRow(rs));
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return vacancies;
    }

    public List<Vacancy> findByCompany(String company) {

        List<Vacancy> vacancies = new ArrayList<>();

        String sql =
                """
                SELECT *
                FROM vacancies
                WHERE company LIKE ? COLLATE NOCASE
                """;

        try (
                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, "%" + company + "%");

            ResultSet rs =
                    statement.executeQuery();

            while (rs.next()) {
                vacancies.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vacancies;
    }

    public Vacancy findById(int id) {

        String sql =
                "SELECT * FROM vacancies WHERE id = ?";

        try (
                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            ResultSet rs =
                    statement.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deleteByUrl(String url) {

        String sql =
                "DELETE FROM vacancies WHERE source_url = ?";

        try (
                Connection connection =
                        databaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, url);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}