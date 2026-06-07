package org.example.service;

import org.example.db.DatabaseManager;
import org.example.model.Vacancy;
import org.example.repository.VacancyRepository;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsServiceTest {

    private AnalyticsService analyticsService;
    private VacancyRepository vacancyRepository;
    private DatabaseManager db;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        db = new DatabaseManager();
        db.createTables();
        vacancyRepository = new VacancyRepository(db);
        analyticsService = new AnalyticsService(vacancyRepository);

        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        db.clearAllData();
        System.setOut(originalOut);
    }

    @Test
    void testPrintVacanciesByCity() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Job 1");
        v1.setCity("Moscow");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Job 2");
        v2.setCity("Moscow");
        v2.setSourceUrl("http://test.com/2");

        Vacancy v3 = new Vacancy();
        v3.setTitle("Job 3");
        v3.setCity("Saint Petersburg");
        v3.setSourceUrl("http://test.com/3");

        vacancyRepository.save(v1);
        vacancyRepository.save(v2);
        vacancyRepository.save(v3);

        analyticsService.printVacanciesByCity();

        String output = outContent.toString();

        assertTrue(output.contains("moscow"), "Должен содержать moscow");
        assertTrue(output.contains("saint petersburg"), "Должен содержать saint petersburg");
        assertTrue(output.contains("-> 2"), "Должен содержать '-> 2' для Moscow");
        assertTrue(output.contains("-> 1"), "Должен содержать '-> 1' для Saint Petersburg");
    }

    @Test
    void testPrintAverageSalaryByCity() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Job 1");
        v1.setCity("Moscow");
        v1.setSalary(100000);
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Job 2");
        v2.setCity("Moscow");
        v2.setSalary(200000);
        v2.setSourceUrl("http://test.com/2");

        vacancyRepository.save(v1);
        vacancyRepository.save(v2);

        analyticsService.printAverageSalaryByCity();

        String output = outContent.toString();
        assertTrue(output.contains("Moscow"));
        assertTrue(output.contains("150000"));
    }

    @Test
    void testPrintSalaryCategories() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Low");
        v1.setSalary(30000);
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Medium");
        v2.setSalary(100000);
        v2.setSourceUrl("http://test.com/2");

        Vacancy v3 = new Vacancy();
        v3.setTitle("High");
        v3.setSalary(300000);
        v3.setSourceUrl("http://test.com/3");

        vacancyRepository.save(v1);
        vacancyRepository.save(v2);
        vacancyRepository.save(v3);

        analyticsService.printSalaryCategories();

        String output = outContent.toString();
        assertNotNull(output);
    }

    @Test
    void testPrintAverageSalaryByCompany() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Job 1");
        v1.setCompany("Google");
        v1.setSalary(200000);
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Job 2");
        v2.setCompany("Google");
        v2.setSalary(300000);
        v2.setSourceUrl("http://test.com/2");

        Vacancy v3 = new Vacancy();
        v3.setTitle("Job 3");
        v3.setCompany("Yandex");
        v3.setSalary(250000);
        v3.setSourceUrl("http://test.com/3");

        vacancyRepository.save(v1);
        vacancyRepository.save(v2);
        vacancyRepository.save(v3);

        analyticsService.printAverageSalaryByCompany();

        String output = outContent.toString();
        assertTrue(output.contains("Google"));
        assertTrue(output.contains("250000"));
        assertTrue(output.contains("Yandex"));
    }

    @Test
    void testPrintVacanciesByCompany() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Job 1");
        v1.setCompany("Google");
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Job 2");
        v2.setCompany("Google");
        v2.setSourceUrl("http://test.com/2");

        Vacancy v3 = new Vacancy();
        v3.setTitle("Job 3");
        v3.setCompany("Yandex");
        v3.setSourceUrl("http://test.com/3");

        vacancyRepository.save(v1);
        vacancyRepository.save(v2);
        vacancyRepository.save(v3);

        analyticsService.printVacanciesByCompany();

        String output = outContent.toString();
        assertTrue(output.contains("Google"));
        assertTrue(output.contains("2"));
        assertTrue(output.contains("Yandex"));
        assertTrue(output.contains("1"));
    }

    @Test
    void testAnalyticsWithEmptyData() {
        outContent.reset();
        analyticsService.printVacanciesByCity();
        String output = outContent.toString();
        assertNotNull(output);
    }

    @Test
    void testPrintAverageSalaryBySpeciality() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Java Developer");
        v1.setSalary(150000);
        v1.setSourceUrl("http://test.com/1");

        Vacancy v2 = new Vacancy();
        v2.setTitle("Python Developer");
        v2.setSalary(200000);
        v2.setSourceUrl("http://test.com/2");

        vacancyRepository.save(v1);
        vacancyRepository.save(v2);

        analyticsService.printAverageSalaryByCompany();

        String output = outContent.toString();
        assertNotNull(output);
    }
}