package org.example.service;

import org.example.db.DatabaseManager;
import org.example.model.NotificationSettings;
import org.example.model.Vacancy;
import org.example.repository.VacancyRepository;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private static VacancyRepository repository;
    private static NotificationService notificationService;
    private static DatabaseManager db;
    private static final String DB_FILE = "vacancies.db";
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeAll
    static void setup() {
        try { Files.deleteIfExists(Paths.get(DB_FILE)); } catch (Exception e) {}

        db = new DatabaseManager();
        db.createTables();
        repository = new VacancyRepository(db);
        notificationService = new NotificationService(repository);
    }

    @BeforeEach
    void clear() {
        try { Files.deleteIfExists(Paths.get(DB_FILE)); } catch (Exception e) {}
        db = new DatabaseManager();
        db.createTables();
        repository = new VacancyRepository(db);
        notificationService = new NotificationService(repository);
    }

    @AfterEach
    void restore() {
        System.setOut(originalOut);
        try { Files.deleteIfExists(Paths.get(DB_FILE)); } catch (Exception e) {}
    }

    @Test
    void testUpdateSettings() {
        NotificationSettings settings = new NotificationSettings("java", 100000, "Moscow");
        notificationService.updateSettings(settings);

        assertDoesNotThrow(() -> notificationService.checkNewVacancies(List.of()));
    }

    @Test
    void testCheckNewVacanciesWithEmptyList() {
        assertDoesNotThrow(() -> notificationService.checkNewVacancies(List.of()));
    }

    @Test
    void testCheckNewVacanciesWithMatchingKeyword() {
        System.setOut(new PrintStream(out));

        NotificationSettings settings = new NotificationSettings("java", null, null);
        notificationService.updateSettings(settings);

        Vacancy v = new Vacancy("Java Developer", "Google", "Moscow", 150000, "Great job", "", "", "http://test1.com");
        notificationService.checkNewVacancies(List.of(v));

        String output = out.toString();
        assertTrue(output.contains("НОВЫЕ ВАКАНСИИ") || output.contains("Java Developer"));
    }

    @Test
    void testCheckNewVacanciesWithMatchingCity() {
        System.setOut(new PrintStream(out));

        NotificationSettings settings = new NotificationSettings(null, null, "Moscow");
        notificationService.updateSettings(settings);

        Vacancy v = new Vacancy("Developer", "Google", "Moscow", 150000, "", "", "", "http://test2.com");
        notificationService.checkNewVacancies(List.of(v));

        String output = out.toString();
        assertTrue(output.contains("НОВЫЕ ВАКАНСИИ") || output.contains("Developer"));
    }

    @Test
    void testCheckNewVacanciesWithMatchingSalary() {
        System.setOut(new PrintStream(out));

        NotificationSettings settings = new NotificationSettings(null, 100000, null);
        notificationService.updateSettings(settings);

        Vacancy v1 = new Vacancy("High Salary", "Google", "Moscow", 150000, "", "", "", "http://test3.com");
        Vacancy v2 = new Vacancy("Low Salary", "Yandex", "SPB", 50000, "", "", "", "http://test4.com");

        notificationService.checkNewVacancies(List.of(v1, v2));

        String output = out.toString();
        assertTrue(output.contains("High Salary"));
    }

    @Test
    void testCheckNewVacanciesFiltersExisting() {
        Vacancy existing = new Vacancy("Existing", "Google", "Moscow", 100000, "", "", "", "http://existing.com");
        repository.save(existing);

        System.setOut(new PrintStream(out));

        NotificationSettings settings = new NotificationSettings(null, null, null);
        notificationService.updateSettings(settings);

        Vacancy newVacancy = new Vacancy("New", "Yandex", "SPB", 120000, "", "", "", "http://new.com");

        notificationService.checkNewVacancies(List.of(existing, newVacancy));

        String output = out.toString();
        assertTrue(output.contains("New"));
    }

    @Test
    void testNoNotificationWhenNoMatch() {
        System.setOut(new PrintStream(out));

        NotificationSettings settings = new NotificationSettings("python", 200000, "London");
        notificationService.updateSettings(settings);

        Vacancy v = new Vacancy("Java Developer", "Google", "Moscow", 100000, "", "", "", "http://test5.com");
        notificationService.checkNewVacancies(List.of(v));

        String output = out.toString();
        assertFalse(output.contains("НОВЫЕ ВАКАНСИИ"));
    }
}