package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:vacancies.db";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public void createTables() {

        String vacancyTable = """
        CREATE TABLE IF NOT EXISTS vacancies
        (
            id INTEGER PRIMARY KEY AUTOINCREMENT,

            title TEXT NOT NULL,
            company TEXT,
            city TEXT,
            salary INTEGER,

            description TEXT,
            requirements TEXT,
            schedule TEXT,

            publish_date TEXT,
            source_url TEXT UNIQUE
        )
        """;

        String historyTable = """
        
                CREATE TABLE IF NOT EXISTS history
        (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
        
            action TEXT NOT NULL,
            vacancy_url TEXT,
            action_date TEXT
        )
        """;

        String settingsTable = """
        CREATE TABLE IF NOT EXISTS notification_settings
        (
            id INTEGER PRIMARY KEY CHECK (id = 1),

            keyword TEXT,
            min_salary INTEGER,
            city TEXT
        )
        """;

        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        ) {

            statement.execute(vacancyTable);
            statement.execute(historyTable);
            statement.execute(settingsTable);

            System.out.println("База данных успешно инициализирована");

        } catch (SQLException e) {
            System.out.println("Ошибка инициализации базы данных:");
            e.printStackTrace();
        }
    }

    public void clearAllData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM vacancies");
            stmt.execute("DELETE FROM history");
            stmt.execute("DELETE FROM notification_settings");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}