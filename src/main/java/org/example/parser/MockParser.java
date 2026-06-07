package org.example.parser;

import org.example.model.Vacancy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MockParser implements VacancyParser {

    @Override
    public String getSourceName() {
        return "aggregator";
    }

    @Override
    public List<Vacancy> parse() {

        List<Vacancy> vacancies =
                new ArrayList<>();

        vacancies.add(
                new Vacancy(
                        "Java Developer",
                        "Google",
                        "Berlin",
                        70000,
                        "Backend development",
                        "Java, Spring",
                        LocalDate.now().toString(),
                        "https://example.com/job1"
                )
        );

        vacancies.add(
                new Vacancy(
                        "QA Engineer",
                        "Amazon",
                        "Munich",
                        55000,
                        "Testing software",
                        "QA, Selenium",
                        LocalDate.now().toString(),
                        "https://example.com/job2"
                )
        );

        vacancies.add(
                new Vacancy(
                        "Backend Developer",
                        "Microsoft",
                        "Hamburg",
                        65000,
                        "Develop backend services",
                        "Java, SQL",
                        LocalDate.now().toString(),
                        "https://example.com/job3"
                )
        );

        vacancies.add(
                new Vacancy(
                        "Java Intern",
                        "SAP",
                        "Berlin",
                        25000,
                        "Internship position",
                        "Java basics",
                        LocalDate.now().toString(),
                        "https://example.com/job4"
                )
        );

        return vacancies;
    }
}