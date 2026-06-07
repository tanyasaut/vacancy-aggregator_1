package org.example.https;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpFetcher {

    private static final int CONNECT_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 30000;
    private static final int MAX_RETRIES = 2;

    public String get(String url) throws Exception {
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return fetch(url);
            } catch (Exception e) {
                lastException = e;
                System.err.println("Попытка " + attempt + " не удалась: " + e.getMessage());
                if (attempt < MAX_RETRIES) {
                    Thread.sleep(2000);
                }
            }
        }

        throw lastException;
    }

    private String fetch(String url) throws Exception {
        URL requestUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9");
        conn.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.9,en;q=0.8");
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);

        int statusCode = conn.getResponseCode();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        statusCode == 200 ? conn.getInputStream() : conn.getErrorStream(),
                        "UTF-8"
                ))) {

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            if (statusCode != 200) {
                throw new RuntimeException("HTTP " + statusCode);
            }

            return result.toString();
        } finally {
            conn.disconnect();
        }
    }
}