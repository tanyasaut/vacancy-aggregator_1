package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VacancyTest {

    @Test
    void testVacancyConstructorAndGetters() {
        Vacancy v = new Vacancy(
                "Java Developer", "Google", "Moscow", 200000,
                "Cool job", "Java 17", "2024-01-15", "https://example.com/1"
        );

        assertEquals("Java Developer", v.getTitle());
        assertEquals("Google", v.getCompany());
        assertEquals("Moscow", v.getCity());
        assertEquals(200000, v.getSalary());
        assertEquals("Cool job", v.getDescription());
        assertEquals("Java 17", v.getRequirements());
        assertEquals("2024-01-15", v.getPublishDate());
        assertEquals("https://example.com/1", v.getSourceUrl());
    }

    @Test
    void testSetters() {
        Vacancy v = new Vacancy();
        v.setId(1);
        v.setTitle("Python Dev");
        v.setCompany("Yandex");
        v.setCity("SPB");
        v.setSalary(180000);
        v.setSchedule("Remote");
        v.setRequirements("Python 3.10");

        assertEquals(1, v.getId());
        assertEquals("Python Dev", v.getTitle());
        assertEquals("Yandex", v.getCompany());
        assertEquals("SPB", v.getCity());
        assertEquals(180000, v.getSalary());
        assertEquals("Remote", v.getSchedule());
        assertEquals("Python 3.10", v.getRequirements());
    }

    @Test
    void testToString() {
        Vacancy v = new Vacancy("Java Dev", "Google", "Moscow", 200000, "", "", "", "");
        String str = v.toString();
        assertTrue(str.contains("Java Dev"));
        assertTrue(str.contains("Google"));
        assertTrue(str.contains("200000"));
    }

    @Test
    void testNullSalary() {
        Vacancy v = new Vacancy();
        v.setSalary(null);
        assertNull(v.getSalary());
        String str = v.toString();
        assertTrue(str.contains("з/п не указана"));
    }

    @Test
    void testNullCity() {
        Vacancy v = new Vacancy();
        v.setCity(null);
        String str = v.toString();
        assertTrue(str.contains("Не указан"));
    }
}