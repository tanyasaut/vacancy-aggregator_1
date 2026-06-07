package org.example.service;

import org.example.db.DatabaseManager;
import org.example.model.Vacancy;
import org.example.parser.VacancyParser;
import org.example.repository.HistoryRepository;
import org.example.repository.NotificationSettingsRepository;
import org.example.repository.VacancyRepository;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VacancyServiceTest {

    private VacancyService vacancyService;
    private VacancyRepository vacancyRepository;
    private DatabaseManager db;

    @BeforeEach
    void setUp() {
        db = new DatabaseManager();
        db.createTables();

        vacancyRepository = new VacancyRepository(db);
        HistoryRepository historyRepo = new HistoryRepository(db);
        NotificationSettingsRepository notifRepo = new NotificationSettingsRepository(db);

        VacancyParser emptyParser = new VacancyParser() {
            @Override
            public String getSourceName() { return "test"; }
            @Override
            public List<Vacancy> parse() { return new ArrayList<>(); }
        };

        vacancyService = new VacancyService(vacancyRepository, historyRepo, emptyParser, notifRepo);
    }

    @AfterEach
    void tearDown() {
        db.clearAllData();
    }

    @Test
    void testSaveAndFindAll() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Java Developer");
        v1.setCompany("Company A");
        v1.setCity("Moscow");
        v1.setSalary(100000);
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Python Developer");
        v2.setCompany("Company B");
        v2.setCity("Saint Petersburg");
        v2.setSalary(120000);
        v2.setSourceUrl("http://test.com/2");

        vacancyService.saveVacancy(v1);
        vacancyService.saveVacancy(v2);

        List<Vacancy> all = vacancyService.getAllVacancies();
        assertEquals(2, all.size());
    }

    @Test
    void testFindByCity() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Java Developer");
        v1.setCity("Moscow");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Python Developer");
        v2.setCity("Saint Petersburg");
        v2.setSourceUrl("http://test.com/2");

        vacancyService.saveVacancy(v1);
        vacancyService.saveVacancy(v2);

        List<Vacancy> result = vacancyService.findByCity("Moscow");
        assertEquals(1, result.size());
        assertEquals("Java Developer", result.get(0).getTitle());
    }

    @Test
    void testFindByCompany() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Java Developer");
        v1.setCompany("Google");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Python Developer");
        v2.setCompany("Yandex");
        v2.setSourceUrl("http://test.com/2");

        vacancyService.saveVacancy(v1);
        vacancyService.saveVacancy(v2);

        List<Vacancy> result = vacancyService.findByCompany("Google");
        assertEquals(1, result.size());
    }

    @Test
    void testFindByMinSalary() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Junior");
        v1.setSalary(50000);
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Senior");
        v2.setSalary(150000);
        v2.setSourceUrl("http://test.com/2");

        vacancyService.saveVacancy(v1);
        vacancyService.saveVacancy(v2);

        List<Vacancy> result = vacancyService.findByMinSalary(100000);
        assertEquals(1, result.size());
        assertEquals("Senior", result.get(0).getTitle());
    }

    @Test
    void testFindBySchedule() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Full-time Job");
        v1.setSchedule("Полный рабочий день");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Remote Job");
        v2.setSchedule("Удалённая работа");
        v2.setSourceUrl("http://test.com/2");

        vacancyService.saveVacancy(v1);
        vacancyService.saveVacancy(v2);

        List<Vacancy> result = vacancyService.findBySchedule("Удалён");
        assertEquals(1, result.size());
    }

    @Test
    void testSearch() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Java Developer");
        v1.setDescription("Spring Boot experience required");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Python Developer");
        v2.setDescription("Django experience required");
        v2.setSourceUrl("http://test.com/2");

        vacancyService.saveVacancy(v1);
        vacancyService.saveVacancy(v2);

        List<Vacancy> result = vacancyService.search("Java");
        assertEquals(1, result.size());
    }

    @Test
    void testSortBySalary() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Low Salary");
        v1.setSalary(50000);
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("High Salary");
        v2.setSalary(200000);
        v2.setSourceUrl("http://test.com/2");

        vacancyService.saveVacancy(v1);
        vacancyService.saveVacancy(v2);

        List<Vacancy> result = vacancyService.sortBySalary();
        assertEquals("High Salary", result.get(0).getTitle());
    }

    @Test
    void testSortByDateDesc() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Old");
        v1.setPublishDate("2024-01-01");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("New");
        v2.setPublishDate("2024-12-01");
        v2.setSourceUrl("http://test.com/2");

        vacancyService.saveVacancy(v1);
        vacancyService.saveVacancy(v2);

        List<Vacancy> result = vacancyService.sortByDateDesc();
        assertEquals("New", result.get(0).getTitle());
    }

    @Test
    void testSortByCompany() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Job A");
        v1.setCompany("Zebra Corp");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Job B");
        v2.setCompany("Alpha Inc");
        v2.setSourceUrl("http://test.com/2");

        vacancyService.saveVacancy(v1);
        vacancyService.saveVacancy(v2);

        List<Vacancy> result = vacancyService.sortByCompany();
        assertEquals("Alpha Inc", result.get(0).getCompany());
    }


    @Test
    void testFindByDescriptionKeyword() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Developer");
        v1.setDescription("Experience with Spring Boot required");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Designer");
        v2.setDescription("Experience with Figma required");
        v2.setSourceUrl("http://test.com/2");

        vacancyService.saveVacancy(v1);
        vacancyService.saveVacancy(v2);

        List<Vacancy> result = vacancyService.findByDescriptionKeyword("Spring");
        assertEquals(1, result.size());
    }

    @Test
    void testLoadVacanciesWithNotification() {
        VacancyParser mockParser = new VacancyParser() {
            @Override
            public String getSourceName() { return "test"; }
            @Override
            public List<Vacancy> parse() {
                Vacancy v = new Vacancy();
                v.setTitle("Test");
                v.setSourceUrl("http://test.com/new");
                return List.of(v);
            }
        };

        DatabaseManager db = new DatabaseManager();
        db.createTables();
        VacancyRepository repo = new VacancyRepository(db);
        HistoryRepository histRepo = new HistoryRepository(db);
        NotificationSettingsRepository notifRepo = new NotificationSettingsRepository(db);

        VacancyService service = new VacancyService(repo, histRepo, mockParser, notifRepo);
        NotificationService notifService = new NotificationService(repo);

        service.loadVacanciesWithNotification(notifService);

        List<Vacancy> all = service.getAllVacancies();
        assertEquals(1, all.size());
    }


    @Test
    void testLoadVacanciesWithNotificationAddsNew() {
        VacancyParser mockParser = new VacancyParser() {
            @Override
            public String getSourceName() { return "test"; }
            @Override
            public List<Vacancy> parse() {
                Vacancy v = new Vacancy();
                v.setTitle("New Job");
                v.setSourceUrl("http://test.com/new");
                return List.of(v);
            }
        };

        DatabaseManager db = new DatabaseManager();
        db.createTables();
        VacancyRepository repo = new VacancyRepository(db);
        HistoryRepository histRepo = new HistoryRepository(db);
        NotificationSettingsRepository notifRepo = new NotificationSettingsRepository(db);

        VacancyService service = new VacancyService(repo, histRepo, mockParser, notifRepo);
        NotificationService notifService = new NotificationService(repo);

        service.loadVacanciesWithNotification(notifService);

        List<Vacancy> all = service.getAllVacancies();
        assertEquals(1, all.size());
        assertEquals("New Job", all.get(0).getTitle());
    }

    @Test
    void testLoadVacanciesWithNotificationSkipsDuplicates() {
        Vacancy existing = new Vacancy();
        existing.setTitle("Existing");
        existing.setSourceUrl("http://test.com/existing");
        vacancyRepository.save(existing);

        VacancyParser mockParser = new VacancyParser() {
            @Override
            public String getSourceName() { return "test"; }
            @Override
            public List<Vacancy> parse() {
                Vacancy v = new Vacancy();
                v.setTitle("Existing");
                v.setSourceUrl("http://test.com/existing");
                return List.of(v);
            }
        };

        DatabaseManager db = new DatabaseManager();
        db.createTables();
        VacancyRepository repo = new VacancyRepository(db);
        HistoryRepository histRepo = new HistoryRepository(db);
        NotificationSettingsRepository notifRepo = new NotificationSettingsRepository(db);

        VacancyService service = new VacancyService(repo, histRepo, mockParser, notifRepo);
        NotificationService notifService = new NotificationService(repo);

        service.loadVacanciesWithNotification(notifService);


        List<Vacancy> all = service.getAllVacancies();
        assertEquals(1, all.size());
    }

    @Test
    void testFindByDescriptionKeywordNoMatches() {
        Vacancy v = new Vacancy();
        v.setTitle("Java Dev");
        v.setDescription("Spring Boot");
        v.setSourceUrl("http://test.com/1");
        vacancyRepository.save(v);

        List<Vacancy> result = vacancyService.findByDescriptionKeyword("Python");
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByCityCaseInsensitive() {
        Vacancy v = new Vacancy();
        v.setTitle("Job");
        v.setCity("moscow");
        v.setSourceUrl("http://test.com/1");
        vacancyRepository.save(v);

        List<Vacancy> result = vacancyService.findByCity("MOSCOW");
        assertEquals(1, result.size());
    }

    @Test
    void testFindByCompanyPartialMatch() {
        Vacancy v = new Vacancy();
        v.setTitle("Job");
        v.setCompany("Google LLC");
        v.setSourceUrl("http://test.com/1");
        vacancyRepository.save(v);

        List<Vacancy> result = vacancyService.findByCompany("Google");
        assertEquals(1, result.size());
    }

    @Test
    void testSearchInTitleAndDescription() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Java Developer");
        v1.setDescription("Python is a plus");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Python Developer");
        v2.setDescription("No Java needed");
        v2.setSourceUrl("http://test.com/2");

        vacancyRepository.save(v1);
        vacancyRepository.save(v2);

        List<Vacancy> result = vacancyService.search("Java");
        assertEquals(2, result.size());
    }
}