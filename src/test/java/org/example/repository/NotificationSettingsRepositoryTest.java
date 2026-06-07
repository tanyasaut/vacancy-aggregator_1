package org.example.repository;

import org.example.db.DatabaseManager;
import org.example.model.NotificationSettings;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class NotificationSettingsRepositoryTest {

    private NotificationSettingsRepository repository;
    private DatabaseManager db;

    @BeforeEach
    void setUp() {
        db = new DatabaseManager();
        db.createTables();
        repository = new NotificationSettingsRepository(db);
    }

    @AfterEach
    void tearDown() {
        db.clearAllData();
    }

    @Test
    void testSaveAndLoad() {
        NotificationSettings settings = new NotificationSettings("Java", 100000, "Moscow");
        repository.save(settings);

        NotificationSettings loaded = repository.load();
        assertNotNull(loaded);
        assertEquals("Java", loaded.getKeyword());
        assertEquals(100000, loaded.getMinSalary());
        assertEquals("Moscow", loaded.getCity());
    }

    @Test
    void testSaveWithNullValues() {
        NotificationSettings settings = new NotificationSettings(null, null, null);
        repository.save(settings);

        NotificationSettings loaded = repository.load();
        assertNotNull(loaded);
        assertNull(loaded.getKeyword());
        assertNull(loaded.getMinSalary());
        assertNull(loaded.getCity());
    }

    @Test
    void testUpdateSettings() {
        NotificationSettings settings1 = new NotificationSettings("Python", 50000, "SPB");
        repository.save(settings1);

        NotificationSettings settings2 = new NotificationSettings("Java", 150000, "Moscow");
        repository.save(settings2);

        NotificationSettings loaded = repository.load();
        assertEquals("Java", loaded.getKeyword());
        assertEquals(150000, loaded.getMinSalary());
        assertEquals("Moscow", loaded.getCity());
    }

    @Test
    void testLoadWhenEmpty() {
        NotificationSettings loaded = repository.load();
        assertNotNull(loaded);
        assertNull(loaded.getKeyword());
        assertNull(loaded.getMinSalary());
        assertNull(loaded.getCity());
    }

    @Test
    void testSaveWithPartialData() {
        NotificationSettings settings = new NotificationSettings("Developer", null, "Moscow");
        repository.save(settings);

        NotificationSettings loaded = repository.load();
        assertEquals("Developer", loaded.getKeyword());
        assertNull(loaded.getMinSalary());
        assertEquals("Moscow", loaded.getCity());
    }
}