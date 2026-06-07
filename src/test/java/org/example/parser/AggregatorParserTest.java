package org.example.parser;

import org.example.model.Vacancy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AggregatorParserTest {

    @Test
    void testAggregatorCombinesParsers() {
        MockParser mock1 = new MockParser();
        MockParser mock2 = new MockParser();

        AggregatorParser aggregator = new AggregatorParser(List.of(mock1, mock2));

        assertDoesNotThrow(() -> {
            List<Vacancy> vacancies = aggregator.parse();
            assertNotNull(vacancies);
        });
    }

    @Test
    void testGetSourceName() {
        AggregatorParser aggregator = new AggregatorParser(List.of());
        assertEquals("aggregator", aggregator.getSourceName());
    }
}