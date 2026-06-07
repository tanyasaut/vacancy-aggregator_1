package org.example;

import org.junit.jupiter.api.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    @Test
    void testMainStartsAndExits() {
        String input = "0\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Thread mainThread = new Thread(() -> Main.main(new String[]{}));
        mainThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String output = outContent.toString();
        assertTrue(output.contains("АГРЕГАТОР ВАКАНСИЙ")
                        || output.contains("СТАРТ")
                        || output.contains("До свидания"),
                "Main должен показать стартовое сообщение или выйти");
    }
}