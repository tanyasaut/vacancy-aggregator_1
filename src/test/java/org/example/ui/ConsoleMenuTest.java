package org.example.ui;

import org.example.db.DatabaseManager;
import org.example.https.HttpFetcher;
import org.example.model.Vacancy;
import org.example.parser.AggregatorParser;
import org.example.parser.HabrCareerParser;
import org.example.parser.TrudvsemParser;
import org.example.parser.VacancyParser;
import org.example.repository.HistoryRepository;
import org.example.repository.NotificationSettingsRepository;
import org.example.repository.VacancyRepository;
import org.example.service.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleMenuTest {

    private ByteArrayOutputStream outContent;
    private InputStream originalIn;
    private PrintStream originalOut;
    private DatabaseManager db;

    @BeforeEach
    void setUp() {
        originalIn = System.in;
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        db = new DatabaseManager();
        db.createTables();
        db.clearAllData();
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        if (db != null) {
            db.clearAllData();
        }
    }

    private void simulateInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    private String getOutput() {
        return outContent.toString();
    }

    private ConsoleMenu createTestMenu() {
        VacancyRepository vacancyRepo = new VacancyRepository(db);
        HistoryRepository histRepo = new HistoryRepository(db);
        NotificationSettingsRepository notifRepo = new NotificationSettingsRepository(db);

        VacancyParser emptyParser = new AggregatorParser(new ArrayList<>());

        VacancyService vacancyService = new VacancyService(vacancyRepo, histRepo, emptyParser, notifRepo);
        AnalyticsService analyticsService = new AnalyticsService(vacancyRepo);
        ExportService exportService = new ExportService();
        NotificationService notifService = new NotificationService(vacancyRepo);

        List<String> sources = new ArrayList<>();
        HttpFetcher fetcher = new HttpFetcher();
        HabrCareerParser habr = new HabrCareerParser(fetcher, "", 1);
        TrudvsemParser trud = new TrudvsemParser(fetcher, "", 1);

        return new ConsoleMenu(
                vacancyService, analyticsService, exportService,
                notifService, notifRepo, sources, habr, trud
        );
    }

    private VacancyRepository getRepo() {
        return new VacancyRepository(db);
    }

    @Test
    void testExitCommand() {
        ConsoleMenu menu = createTestMenu();
        simulateInput("0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("До свидания!"));
    }

    @Test
    void testShowAllVacancies() {
        VacancyRepository repo = getRepo();

        Vacancy v = new Vacancy();
        v.setTitle("Test Developer");
        v.setCompany("Test Company");
        v.setCity("Moscow");
        v.setSalary(100000);
        v.setSourceUrl("http://test.com/1");
        repo.save(v);

        ConsoleMenu menu = createTestMenu();
        simulateInput("2\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Test Developer"));
    }

    @Test
    void testSearchVacancies() {
        VacancyRepository repo = getRepo();

        Vacancy v = new Vacancy();
        v.setTitle("Senior Java Developer");
        v.setCompany("Tech Corp");
        v.setCity("Moscow");
        v.setSalary(200000);
        v.setSourceUrl("http://test.com/1");
        repo.save(v);

        ConsoleMenu menu = createTestMenu();
        simulateInput("3\nJava\n0\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Senior Java Developer"));
    }

    @Test
    void testFilterByCity() {
        VacancyRepository repo = getRepo();

        Vacancy v1 = new Vacancy();
        v1.setTitle("Moscow Job");
        v1.setCity("Moscow");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("SPB Job");
        v2.setCity("Saint Petersburg");
        v2.setSourceUrl("http://test.com/2");

        repo.save(v1);
        repo.save(v2);

        ConsoleMenu menu = createTestMenu();
        simulateInput("4\n1\nMoscow\n0\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Moscow Job"));
    }

    @Test
    void testFilterByCompany() {
        VacancyRepository repo = getRepo();

        Vacancy v = new Vacancy();
        v.setTitle("Google Job");
        v.setCompany("Google");
        v.setSourceUrl("http://test.com/1");
        repo.save(v);

        ConsoleMenu menu = createTestMenu();
        simulateInput("4\n2\nGoogle\n0\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Google Job"));
    }

    @Test
    void testFilterBySalary() {
        VacancyRepository repo = getRepo();

        Vacancy v1 = new Vacancy();
        v1.setTitle("High Salary");
        v1.setSalary(150000);
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Low Salary");
        v2.setSalary(50000);
        v2.setSourceUrl("http://test.com/2");

        repo.save(v1);
        repo.save(v2);

        ConsoleMenu menu = createTestMenu();
        simulateInput("4\n3\n100000\n0\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("High Salary"));
    }

    @Test
    void testSortBySalary() {
        VacancyRepository repo = getRepo();

        Vacancy v1 = new Vacancy();
        v1.setTitle("Low");
        v1.setSalary(50000);
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("High");
        v2.setSalary(200000);
        v2.setSourceUrl("http://test.com/2");

        repo.save(v1);
        repo.save(v2);

        ConsoleMenu menu = createTestMenu();
        simulateInput("5\n1\n0\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("High"));
    }

    @Test
    void testConfigureSources() {
        ConsoleMenu menu = createTestMenu();
        simulateInput("9\n1\n0\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("НАСТРОЙКА ИСТОЧНИКОВ"));
        assertTrue(output.contains("Habr Career включён"));
    }

    @Test
    void testInvalidOption() {
        ConsoleMenu menu = createTestMenu();
        simulateInput("99\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Неверная опция"));
    }

    @Test
    void testShowAllVacanciesEmpty() {
        ConsoleMenu menu = createTestMenu();
        simulateInput("2\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Вакансии не найдены"));
    }

    @Test
    void testSelectVacancyDetails() {
        VacancyRepository repo = getRepo();

        Vacancy v = new Vacancy();
        v.setTitle("Test Job");
        v.setCompany("Test Corp");
        v.setCity("Moscow");
        v.setSalary(100000);
        v.setSchedule("Full-time");
        v.setDescription("Test description");
        v.setRequirements("Test requirements");
        v.setPublishDate("2024-01-01");
        v.setSourceUrl("http://test.com/1");
        repo.save(v);

        ConsoleMenu menu = createTestMenu();
        simulateInput("2\n1\n0\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Test Job"));
        assertTrue(output.contains("Test description"));
    }

    @Test
    void testFilterBySchedule() {
        VacancyRepository repo = getRepo();

        Vacancy v1 = new Vacancy();
        v1.setTitle("Remote Job");
        v1.setSchedule("Удалённая работа");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Office Job");
        v2.setSchedule("Офис");
        v2.setSourceUrl("http://test.com/2");

        repo.save(v1);
        repo.save(v2);

        ConsoleMenu menu = createTestMenu();
        simulateInput("4\n4\nУдалён\n0\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Remote Job"));
    }

    @Test
    void testFilterByDescription() {
        VacancyRepository repo = getRepo();

        Vacancy v1 = new Vacancy();
        v1.setTitle("Java Dev");
        v1.setDescription("Spring Boot experience required");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Python Dev");
        v2.setDescription("Django experience required");
        v2.setSourceUrl("http://test.com/2");

        repo.save(v1);
        repo.save(v2);

        ConsoleMenu menu = createTestMenu();
        simulateInput("4\n5\nSpring\n0\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Java Dev"));
    }

    @Test
    void testExportMenu() {
        ConsoleMenu menu = createTestMenu();
        simulateInput("7\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("ЭКСПОРТ ДАННЫХ"));
    }

    @Test
    void testAnalyticsMenu() {
        ConsoleMenu menu = createTestMenu();
        simulateInput("6\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("АНАЛИТИКА"));
    }

    @Test
    void testSortByDate() {
        VacancyRepository repo = getRepo();

        Vacancy v1 = new Vacancy();
        v1.setTitle("Old");
        v1.setPublishDate("2024-01-01");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("New");
        v2.setPublishDate("2024-12-01");
        v2.setSourceUrl("http://test.com/2");

        repo.save(v1);
        repo.save(v2);

        ConsoleMenu menu = createTestMenu();
        simulateInput("5\n2\n0\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("New"));
    }

    @Test
    void testSortByCompany() {
        VacancyRepository repo = getRepo();

        Vacancy v1 = new Vacancy();
        v1.setTitle("Job A");
        v1.setCompany("Zebra Corp");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Job B");
        v2.setCompany("Alpha Inc");
        v2.setSourceUrl("http://test.com/2");

        repo.save(v1);
        repo.save(v2);

        ConsoleMenu menu = createTestMenu();
        simulateInput("5\n3\n0\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Alpha Inc"));
    }

    @Test
    void testInvalidVacancyNumber() {
        VacancyRepository repo = getRepo();

        Vacancy v = new Vacancy();
        v.setTitle("Test");
        v.setSourceUrl("http://test.com/1");
        repo.save(v);

        ConsoleMenu menu = createTestMenu();
        simulateInput("2\n999\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Неверный номер"));
    }

    @Test
    void testInvalidInputInVacancySelection() {
        VacancyRepository repo = getRepo();

        Vacancy v = new Vacancy();
        v.setTitle("Test");
        v.setSourceUrl("http://test.com/1");
        repo.save(v);

        ConsoleMenu menu = createTestMenu();
        simulateInput("2\nabc\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Неверный ввод"));
    }

    @Test
    void testConfigureSourcesDisable() {
        ConsoleMenu menu = createTestMenu();
        simulateInput("9\n4\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Все источники отключены"));
    }

    @Test
    void testConfigureSourcesEnableAll() {
        ConsoleMenu menu = createTestMenu();
        simulateInput("9\n3\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Все источники включены"));
    }

    @Test
    void testConfigureSourcesToggleTrudvsem() {
        ConsoleMenu menu = createTestMenu();
        simulateInput("9\n2\n0\n0\n");
        menu.start();

        String output = getOutput();
        assertTrue(output.contains("Trudvsem включён"));
    }
}