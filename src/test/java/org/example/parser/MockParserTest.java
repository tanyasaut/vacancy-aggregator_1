package org.example.parser;

import org.example.model.Vacancy;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockParserTest {

    private MockParser parser;

    @BeforeEach
    void setUp() {
        parser = new MockParser();
    }

    @Test
    void testParseReturnsExactly4Vacancies() {
        List<Vacancy> vacancies = parser.parse();
        assertEquals(4, vacancies.size(), "MockParser should return exactly 4 vacancies");
    }

    @Test
    void testParseReturnsNonNullVacancies() {
        List<Vacancy> vacancies = parser.parse();
        assertNotNull(vacancies);
        for (Vacancy v : vacancies) {
            assertNotNull(v.getTitle());
        }
    }

    @Test
    void testParseFirstVacancyIsJavaDeveloper() {
        List<Vacancy> vacancies = parser.parse();
        Vacancy first = vacancies.get(0);
        assertEquals("Java Developer", first.getTitle());
        assertEquals("Google", first.getCompany());
        assertEquals("Berlin", first.getCity());
    }

    @Test
    void testParseAllVacanciesHaveUrls() {
        List<Vacancy> vacancies = parser.parse();
        for (Vacancy v : vacancies) {
            assertNotNull(v.getSourceUrl());
            assertTrue(v.getSourceUrl().startsWith("https://example.com/"));
        }
    }

    @Test
    void testParseAllVacanciesHaveDates() {
        List<Vacancy> vacancies = parser.parse();
        for (Vacancy v : vacancies) {
            assertNotNull(v.getPublishDate());
        }
    }
}