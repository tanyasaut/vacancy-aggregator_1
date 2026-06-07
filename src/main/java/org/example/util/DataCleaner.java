package org.example.util;

public class DataCleaner {

    public static String cleanText(String text) {

        if (text == null) return null;

        return text
                .replaceAll("(?i)войти", "")
                .replaceAll("(?i)зарегистрироваться", "")
                .replaceAll("(?i)похожие вакансии.*", "")
                .replaceAll("(?i)подписаться.*", "")

                .replaceAll("\\s+", " ")
                .trim();
    }

    public static String cleanCity(String city) {

        city = cleanText(city);

        if (city == null || city.isBlank()) {
            return "Не указан";
        }

        return city;
    }

    public static String cleanCompany(String company) {

        company = cleanText(company);

        if (company == null || company.isBlank()) {
            return "Не указана";
        }

        return company;
    }

    public static String cleanDescription(String description) {

        description = cleanText(description);

        if (description == null || description.isBlank()) {
            return "Описание отсутствует";
        }

        // ограничим длину
        if (description.length() > 800) {
            return description.substring(0, 800) + "...";
        }

        return description;
    }
}