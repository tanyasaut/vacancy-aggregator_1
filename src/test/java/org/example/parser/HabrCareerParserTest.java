package org.example.parser;

import org.example.https.HttpFetcher;
import org.example.model.Vacancy;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HabrCareerParserTest {

    private HabrCareerParser parser;

    @Test
    void testGetSourceName() {
        HttpFetcher fetcher = new HttpFetcher();
        parser = new HabrCareerParser(fetcher, "java", 1);

        assertEquals("habr.career.detailed", parser.getSourceName());
    }

    @Test
    void testParseWithZeroPages() {
        HttpFetcher fetcher = new HttpFetcher();
        parser = new HabrCareerParser(fetcher, "java", 0);

        assertDoesNotThrow(() -> {
            List<Vacancy> vacancies = parser.parse();
            assertNotNull(vacancies);
            assertTrue(vacancies.isEmpty() || vacancies.size() >= 0);
        });
    }

    @Test
    void testParseWithEmptyKeyword() {
        HttpFetcher fetcher = new HttpFetcher();
        parser = new HabrCareerParser(fetcher, "", 1);

        assertDoesNotThrow(() -> {
            List<Vacancy> vacancies = parser.parse();
            assertNotNull(vacancies);
        });
    }

    @Test
    void testParseWithValidKeyword() {
        HttpFetcher fetcher = new HttpFetcher();
        parser = new HabrCareerParser(fetcher, "java", 1);

        assertDoesNotThrow(() -> {
            List<Vacancy> vacancies = parser.parse();
            assertNotNull(vacancies);
        });
    }


}