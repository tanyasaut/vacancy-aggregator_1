package org.example.repository;

import org.example.db.DatabaseManager;
import org.example.model.HistoryRecord;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryRepositoryTest {

    private DatabaseManager db;
    private HistoryRepository historyRepository;
    private static final String DB_FILE = "vacancies.db";

    @BeforeEach
    void setUp() {
        try { Files.deleteIfExists(Paths.get(DB_FILE)); } catch (Exception e) {}

        db = new DatabaseManager();
        db.createTables();
        historyRepository = new HistoryRepository(db);
    }

    @AfterEach
    void tearDown() {
        try { Files.deleteIfExists(Paths.get(DB_FILE)); } catch (Exception e) {}
    }

    @Test
    void testSaveHistoryRecord() {
        HistoryRecord record = new HistoryRecord("ADDED", "http://test.com", "2024-01-01T10:00:00");

        assertDoesNotThrow(() -> historyRepository.save(record));

        List<HistoryRecord> all = historyRepository.findAll();
        assertEquals(1, all.size());
        assertEquals("ADDED", all.get(0).getAction());
        assertEquals("http://test.com", all.get(0).getVacancyUrl());
    }

    @Test
    void testFindAllReturnsEmptyListWhenNoRecords() {
        List<HistoryRecord> all = historyRepository.findAll();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void testSaveMultipleRecords() {
        historyRepository.save(new HistoryRecord("ADDED", "http://test1.com", "2024-01-01T10:00:00"));
        historyRepository.save(new HistoryRecord("ADDED", "http://test2.com", "2024-01-02T10:00:00"));
        historyRepository.save(new HistoryRecord("DELETED", "http://test1.com", "2024-01-03T10:00:00"));

        List<HistoryRecord> all = historyRepository.findAll();
        assertEquals(3, all.size());
    }

    @Test
    void testHistoryRecordGettersAndSetters() {
        HistoryRecord record = new HistoryRecord();
        record.setId(1);
        record.setAction("ADDED");
        record.setVacancyUrl("http://test.com");
        record.setActionDate("2024-01-01");

        assertEquals(1, record.getId());
        assertEquals("ADDED", record.getAction());
        assertEquals("http://test.com", record.getVacancyUrl());
        assertEquals("2024-01-01", record.getActionDate());
    }

    @Test
    void testHistoryRecordConstructor() {
        HistoryRecord record = new HistoryRecord("ADDED", "http://test.com", "2024-01-01");

        assertEquals("ADDED", record.getAction());
        assertEquals("http://test.com", record.getVacancyUrl());
        assertEquals("2024-01-01", record.getActionDate());
    }

    @Test
    void testToString() {
        HistoryRecord record = new HistoryRecord("ADDED", "http://test.com", "2024-01-01");
        String str = record.toString();

        assertTrue(str.contains("ADDED"));
        assertTrue(str.contains("http://test.com"));
    }
}