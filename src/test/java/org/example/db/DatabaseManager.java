package org.example.db;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    private DatabaseManager db;

    @BeforeEach
    void setUp() {
        db = new DatabaseManager();
        db.createTables();
    }

    @Test
    void testGetConnection() {
        assertDoesNotThrow(() -> {
            Connection conn = db.getConnection();
            assertNotNull(conn);
            conn.close();
        });
    }

    @Test
    void testCreateTablesCreatesVacanciesTable() throws Exception {
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='vacancies'")) {
            assertTrue(rs.next(), "vacancies table should exist");
        }
    }

    @Test
    void testCreateTablesCreatesHistoryTable() throws Exception {
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='history'")) {
            assertTrue(rs.next(), "history table should exist");
        }
    }

    @Test
    void testCreateTablesIsIdempotent() {
        assertDoesNotThrow(() -> db.createTables());
        assertDoesNotThrow(() -> db.createTables());
    }
}