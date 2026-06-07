package org.example.parser;

import org.example.https.HttpFetcher;
import org.example.model.Vacancy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrudvsemParserTest {

    @Test
    void testParserReturnsList() {
        HttpFetcher fetcher = new HttpFetcher();
        TrudvsemParser parser = new TrudvsemParser(fetcher, "java", 1);

        assertDoesNotThrow(() -> {
            List<Vacancy> vacancies = parser.parse();
            assertNotNull(vacancies);
        });
    }

    @Test
    void testGetSourceName() {
        HttpFetcher fetcher = new HttpFetcher();
        TrudvsemParser parser = new TrudvsemParser(fetcher, "", 1);

        assertEquals("trudvsem.ru", parser.getSourceName());
    }
}