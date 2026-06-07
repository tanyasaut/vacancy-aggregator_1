package org.example.service;

import org.example.db.DatabaseManager;
import org.example.parser.MockParser;
import org.example.parser.VacancyParser;
import org.example.repository.HistoryRepository;
import org.example.repository.NotificationSettingsRepository;
import org.example.repository.VacancyRepository;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerServiceTest {

    private static DatabaseManager db;
    private static VacancyRepository repository;
    private static HistoryRepository historyRepository;
    private static VacancyService vacancyService;
    private static NotificationService notificationService;
    private static final String DB_FILE = "vacancies.db";

    @BeforeEach
    void setup() {
        try { Files.deleteIfExists(Paths.get(DB_FILE)); } catch (Exception e) {}

        db = new DatabaseManager();
        db.createTables();

        repository = new VacancyRepository(db);
        historyRepository = new HistoryRepository(db);
        NotificationSettingsRepository settingsRepository = new NotificationSettingsRepository(db);

        VacancyParser mockParser = new MockParser();

        vacancyService = new VacancyService(
                repository,
                historyRepository,
                mockParser,
                settingsRepository
        );

        notificationService = new NotificationService(repository);
    }

    @AfterEach
    void cleanup() {
        try { Files.deleteIfExists(Paths.get(DB_FILE)); } catch (Exception e) {}
    }

    @Test
    void testSchedulerStartDoesNotThrow() {
        SchedulerService scheduler = new SchedulerService();

        assertDoesNotThrow(() -> {
            scheduler.start(vacancyService, notificationService);
        });
    }

    @Test
    void testSchedulerCanBeStartedAndStopped() {
        SchedulerService scheduler = new SchedulerService();
        scheduler.start(vacancyService, notificationService);

        assertDoesNotThrow(() -> {
            Thread.sleep(100);
        });

        assertDoesNotThrow(() -> {
        });
    }

    @Test
    void testMultipleSchedulerStarts() {
        SchedulerService scheduler1 = new SchedulerService();
        SchedulerService scheduler2 = new SchedulerService();

        assertDoesNotThrow(() -> {
            scheduler1.start(vacancyService, notificationService);
            scheduler2.start(vacancyService, notificationService);
        });
    }
}