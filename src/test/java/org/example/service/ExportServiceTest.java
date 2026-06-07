package org.example.service;

import org.example.model.Vacancy;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExportServiceTest {

    private ExportService exportService;
    private List<Vacancy> testVacancies;

    @BeforeEach
    void setup() {
        exportService = new ExportService();
        testVacancies = List.of(
                new Vacancy("Java Dev", "Google", "Moscow", 200000, "Description with <html>", "Requirements", "2024-01-01", "http://test1.com"),
                new Vacancy("Python Dev", "Yandex", "SPB", 180000, "Python description", "Python req", "2024-01-02", "http://test2.com"),
                new Vacancy("QA Engineer", "Amazon", "Moscow", 120000, "Testing", "QA req", "2024-01-03", "http://test3.com")
        );
    }

    @AfterEach
    void cleanup() {
        try {
            Files.deleteIfExists(Paths.get("vacancies.csv"));
            Files.deleteIfExists(Paths.get("vacancies.json"));
            Files.deleteIfExists(Paths.get("vacancies.html"));
        } catch (Exception e) {}
    }

    @Test
    void testExportToCsv() {
        assertDoesNotThrow(() -> exportService.exportToCsv(testVacancies));
        assertTrue(new File("vacancies.csv").exists());
    }

    @Test
    void testExportToCsvWithEmptyList() {
        assertDoesNotThrow(() -> exportService.exportToCsv(List.of()));
        assertTrue(new File("vacancies.csv").exists());
    }

    @Test
    void testExportToJson() {
        assertDoesNotThrow(() -> exportService.exportToJson(testVacancies));
        assertTrue(new File("vacancies.json").exists());
    }

    @Test
    void testExportToJsonWithEmptyList() {
        assertDoesNotThrow(() -> exportService.exportToJson(List.of()));
        assertTrue(new File("vacancies.json").exists());
    }

    @Test
    void testExportToHtml() {
        assertDoesNotThrow(() -> exportService.exportToHtml(testVacancies));
        assertTrue(new File("vacancies.html").exists());
    }

    @Test
    void testExportToHtmlWithEmptyList() {
        assertDoesNotThrow(() -> exportService.exportToHtml(List.of()));
        assertTrue(new File("vacancies.html").exists());
    }

    @Test
    void testExportToHtmlContainsData() throws Exception {
        exportService.exportToHtml(testVacancies);
        String content = Files.readString(Paths.get("vacancies.html"));

        assertTrue(content.contains("Java Dev"));
        assertTrue(content.contains("Google"));
        assertTrue(content.contains("200000"));
        assertTrue(content.contains("http://test1.com"));
    }

    @Test
    void testExportToCsvContainsData() throws Exception {
        exportService.exportToCsv(testVacancies);
        String content = Files.readString(Paths.get("vacancies.csv"));

        assertTrue(content.contains("Java Dev"));
        assertTrue(content.contains("Google"));
    }

    @Test
    void testExportToJsonContainsData() throws Exception {
        exportService.exportToJson(testVacancies);
        String content = Files.readString(Paths.get("vacancies.json"));

        assertTrue(content.contains("Java Dev") || content.contains("title"));
    }

    @Test
    void testEscapeHtml() throws Exception {
        java.lang.reflect.Method method = ExportService.class.getDeclaredMethod("escapeHtml", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(exportService, "a & b < c > d \" e");
        assertEquals("a &amp; b &lt; c &gt; d &quot; e", result);

        String nullResult = (String) method.invoke(exportService, (Object) null);
        assertEquals("", nullResult);
    }
}