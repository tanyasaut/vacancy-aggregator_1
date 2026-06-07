package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Vacancy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportService {

    public void exportToCsv(List<Vacancy> vacancies) {
        try (FileWriter writer = new FileWriter("vacancies.csv")) {

            writer.write("Должность,Компания,Город,Зарплата\n");

            for (Vacancy vacancy : vacancies) {
                writer.write(
                        "\"" + escapeCsv(vacancy.getTitle()) + "\","
                                + "\"" + escapeCsv(vacancy.getCompany()) + "\","
                                + "\"" + escapeCsv(vacancy.getCity()) + "\","
                                + "\"" + (vacancy.getSalary() == null
                                ? ""
                                : vacancy.getSalary()) + "\""
                                + "\n"
                );
            }

            System.out.println("Данные экспортированы в CSV (файл vacancies.csv)");

        } catch (IOException e) {
            System.out.println("Ошибка при экспорте в CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String escapeCsv(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\"", "\"\"");
    }

    public void exportToJson(List<Vacancy> vacancies) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File("vacancies.json"), vacancies);

            System.out.println("Данные экспортированы в JSON (файл vacancies.json)");

        } catch (IOException e) {
            System.out.println("Ошибка при экспорте в JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void exportToHtml(List<Vacancy> vacancies) {
        try (FileWriter writer = new FileWriter("vacancies.html")) {
            writer.write("""
            <!DOCTYPE html>
            <html lang="ru">
            <head>
                <meta charset="UTF-8">
                <title>Вакансии</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
                    h1 { color: #333; }
                    table { border-collapse: collapse; width: 100%%; background-color: white; }
                    th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
                    tr:nth-child(even) { background-color: #f9f9f9; }
                    tr:hover { background-color: #f1f1f1; }
                    th { background-color: #4CAF50; color: white; }
                    a { color: #4CAF50; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                </style>
            </head>
            <body>
            <h1>Агрегатор вакансий</h1>
            <p>Всего найдено: <strong>""" + vacancies.size() + "</strong> вакансий</p>" +
                    "<table>" +
                    "<tr>" +
                    "<th>Должность</th>" +
                    "<th>Компания</th>" +
                    "<th>Город</th>" +
                    "<th>Зарплата</th>" +
                    "<th>Опубликовано</th>" +
                    "<th>Ссылка</th>" +
                    "</tr>"
            );

            for (Vacancy v : vacancies) {
                String salary = v.getSalary() != null ? v.getSalary() + " руб" : "Не указана";
                String published = v.getPublishDate() != null ? v.getPublishDate() : "Не указана";
                writer.write(String.format("""
                <tr>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td><a href='%s' target='_blank'>Открыть</a></td>
                </tr>
                """,
                        escapeHtml(v.getTitle()),
                        escapeHtml(v.getCompany()),
                        escapeHtml(v.getCity()),
                        salary,
                        published,
                        v.getSourceUrl()
                ));
            }

            writer.write("</table></body></html>");
            System.out.println("Данные экспортированы в HTML (файл vacancies.html)");

        } catch (IOException e) {
            System.out.println("Ошибка при экспорте в HTML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}