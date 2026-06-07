package org.example.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DataCleanerTest {

    @Test
    void testCleanTextRemovesSpaces() {
        String result = DataCleaner.cleanText("  Hello   World  ");
        assertEquals("Hello World", result);
    }


    @Test
    void testCleanTextReturnsNullForNull() {
        assertNull(DataCleaner.cleanText(null));
    }

    @Test
    void testCleanCity() {
        assertEquals("Moscow", DataCleaner.cleanCity("  Moscow  "));
        assertEquals("Не указан", DataCleaner.cleanCity(""));
        assertEquals("Не указан", DataCleaner.cleanCity(null));
        assertEquals("Не указан", DataCleaner.cleanCity("   "));
    }

    @Test
    void testCleanCompany() {
        assertEquals("Google", DataCleaner.cleanCompany("  Google  "));
        assertEquals("Не указана", DataCleaner.cleanCompany(""));
        assertEquals("Не указана", DataCleaner.cleanCompany(null));
    }

    @Test
    void testCleanDescription() {
        assertEquals("Short desc", DataCleaner.cleanDescription("Short desc"));
        assertEquals("Описание отсутствует", DataCleaner.cleanDescription(""));
        assertEquals("Описание отсутствует", DataCleaner.cleanDescription(null));
    }

}