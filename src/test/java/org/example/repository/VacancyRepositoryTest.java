package org.example.repository;

import org.example.db.DatabaseManager;
import org.example.model.Vacancy;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VacancyRepositoryTest {

    private static DatabaseManager db;
    private static VacancyRepository repository;
    private static final String DB_FILE = "vacancies.db";

    @BeforeAll
    static void setup() {
        try { Files.deleteIfExists(Paths.get(DB_FILE)); } catch (Exception e) {}

        db = new DatabaseManager();
        db.createTables();
        repository = new VacancyRepository(db);
    }

    @AfterAll
    static void cleanup() {
        try { Files.deleteIfExists(Paths.get(DB_FILE)); } catch (Exception e) {}
    }

    @BeforeEach
    void clear() {
        db.clearAllData();
    }

    @Test
    void testSaveAndFindAll() {
        Vacancy v = new Vacancy("Java Dev", "Google", "Moscow", 200000, "Desc", "Req", "2024-01-01", "http://test1.com");
        repository.save(v);

        List<Vacancy> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("Java Dev", all.get(0).getTitle());
    }

    @Test
    void testSaveDuplicateIgnored() {
        Vacancy v1 = new Vacancy("Java Dev", "Google", "Moscow", 200000, "Desc", "Req", "2024-01-01", "http://test2.com");
        Vacancy v2 = new Vacancy("Python Dev", "Yandex", "SPB", 180000, "Desc2", "Req2", "2024-01-02", "http://test2.com");

        repository.save(v1);
        repository.save(v2);

        List<Vacancy> all = repository.findAll();
        assertEquals(1, all.size(), "Duplicate URL should be ignored");
    }

    @Test
    void testFindByCity() {
        Vacancy v = new Vacancy("Java Dev", "Google", "Moscow", 200000, "Desc", "Req", "2024-01-01", "http://test3.com");
        repository.save(v);

        List<Vacancy> found = repository.findByCity("Moscow");
        assertEquals(1, found.size());

        List<Vacancy> notFound = repository.findByCity("London");
        assertTrue(notFound.isEmpty());
    }

    @Test
    void testFindByMinSalary() {
        repository.save(new Vacancy("Low", "A", "City", 50000, "", "", "", "http://test4.com"));
        repository.save(new Vacancy("High", "B", "City", 150000, "", "", "", "http://test5.com"));
        repository.save(new Vacancy("Medium", "C", "City", 100000, "", "", "", "http://test6.com"));

        List<Vacancy> result = repository.findByMinSalary(100000);
        assertEquals(2, result.size());
    }

    @Test
    void testSearchByKeyword() {
        repository.save(new Vacancy("Java Developer", "Google", "Moscow", 200000, "Spring Boot", "", "", "http://test7.com"));
        repository.save(new Vacancy("Python Dev", "Yandex", "SPB", 180000, "Django", "", "", "http://test8.com"));

        List<Vacancy> javaResults = repository.searchByKeyword("java");
        assertEquals(1, javaResults.size());

        List<Vacancy> springResults = repository.searchByKeyword("spring");
        assertEquals(1, springResults.size());
    }

    @Test
    void testSortBySalaryDesc() {
        repository.save(new Vacancy("Low", "A", "City", 30000, "", "", "", "http://test9.com"));
        repository.save(new Vacancy("High", "B", "City", 200000, "", "", "", "http://test10.com"));
        repository.save(new Vacancy("Medium", "C", "City", 100000, "", "", "", "http://test11.com"));

        List<Vacancy> sorted = repository.sortBySalaryDesc();
        assertEquals(200000, sorted.get(0).getSalary());
        assertEquals(100000, sorted.get(1).getSalary());
        assertEquals(30000, sorted.get(2).getSalary());
    }

    @Test
    void testFindById() {
        Vacancy v = new Vacancy("Test", "Company", "City", 100000, "", "", "", "http://test12.com");
        repository.save(v);

        List<Vacancy> all = repository.findAll();
        int id = all.get(0).getId();

        Vacancy found = repository.findById(id);
        assertNotNull(found);
        assertEquals("Test", found.getTitle());

        assertNull(repository.findById(99999));
    }

    @Test
    void testDeleteByUrl() {
        Vacancy v = new Vacancy("ToDelete", "Company", "City", 100000, "", "", "", "http://test13.com");
        repository.save(v);

        List<Vacancy> before = repository.findAll();
        assertEquals(1, before.size());

        repository.deleteByUrl("http://test13.com");

        List<Vacancy> after = repository.findAll();
        assertTrue(after.isEmpty());
    }

    @Test
    void testFindByCompany() {
        repository.save(new Vacancy("Dev1", "Google", "Moscow", 200000, "", "", "", "http://test14.com"));
        repository.save(new Vacancy("Dev2", "Yandex", "SPB", 180000, "", "", "", "http://test15.com"));
        repository.save(new Vacancy("Dev3", "Google", "Kazan", 150000, "", "", "", "http://test16.com"));

        List<Vacancy> googleVacancies = repository.findByCompany("Google");
        assertEquals(2, googleVacancies.size());
    }

    @Test
    void testFindBySchedule() {
        Vacancy v = new Vacancy("Remote Job", "Company", "City", 100000, "", "", "", "http://test17.com");
        v.setSchedule("Удалённая работа");
        repository.save(v);

        List<Vacancy> remote = repository.findBySchedule("Удалённая");
        assertEquals(1, remote.size());
    }
}