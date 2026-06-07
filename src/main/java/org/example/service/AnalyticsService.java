package org.example.service;

import org.example.model.Vacancy;
import org.example.repository.VacancyRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsService {

    private final VacancyRepository repository;

    public AnalyticsService(VacancyRepository repository) {
        this.repository = repository;
    }

    public void printVacanciesByCity() {
        List<Vacancy> vacancies = repository.findAll();

        Map<String, Integer> statistics = new HashMap<>();

        for (Vacancy vacancy : vacancies) {
            String city = vacancy.getCity();

            if (city == null || city.isBlank()) {
                city = "Не указан";
            }

            city = city.trim().toLowerCase();

            statistics.put(city, statistics.getOrDefault(city, 0) + 1);
        }

        System.out.println("\nВакансии по городам:");

        for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue() + " вакансий");
        }
    }

    public void printAverageSalary() {
        List<Vacancy> vacancies = repository.findAll();

        int total = 0;
        int count = 0;

        for (Vacancy vacancy : vacancies) {
            if (vacancy.getSalary() != null) {
                total += vacancy.getSalary();
                count++;
            }
        }

        if (count == 0) {
            System.out.println("Нет данных о зарплатах");
            return;
        }

        System.out.println("Средняя зарплата: " + (total / count) + " руб");
    }

    public void printSalaryCategories() {
        int low = 0;
        int medium = 0;
        int high = 0;

        for (Vacancy vacancy : repository.findAll()) {
            Integer salary = vacancy.getSalary();

            if (salary == null) {
                continue;
            }

            if (salary < 100000) {
                low++;
            } else if (salary <= 200000) {
                medium++;
            } else {
                high++;
            }
        }

        System.out.println("\nКатегории зарплат:");
        System.out.println("  Менее 100 000 руб -> " + low + " вакансий");
        System.out.println("  100 000 - 200 000 руб -> " + medium + " вакансий");
        System.out.println("  Более 200 000 руб -> " + high + " вакансий");
    }

    public void printVacanciesByCompany() {
        List<Vacancy> vacancies = repository.findAll();

        Map<String, Integer> statistics = new HashMap<>();

        for (Vacancy vacancy : vacancies) {
            String company = vacancy.getCompany();

            if (company == null || company.isBlank()) {
                company = "Не указана";
            }

            statistics.put(company, statistics.getOrDefault(company, 0) + 1);
        }

        System.out.println("\nВакансии по компаниям:");

        for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue() + " вакансий");
        }
    }

    public void printAverageSalaryByCity() {
        List<Vacancy> vacancies = repository.findAll();

        Map<String, Integer> salarySum = new HashMap<>();
        Map<String, Integer> salaryCount = new HashMap<>();

        for (Vacancy vacancy : vacancies) {
            if (vacancy.getSalary() == null) {
                continue;
            }

            String city = vacancy.getCity();

            if (city == null || city.isBlank()) {
                city = "Не указан";
            }

            salarySum.put(city, salarySum.getOrDefault(city, 0) + vacancy.getSalary());
            salaryCount.put(city, salaryCount.getOrDefault(city, 0) + 1);
        }

        System.out.println("\nСредняя зарплата по городам:");

        for (String city : salarySum.keySet()) {
            int avg = salarySum.get(city) / salaryCount.get(city);
            System.out.println("  " + city + " -> " + avg + " руб");
        }
    }

    public void printAverageSalaryByCompany() {
        List<Vacancy> vacancies = repository.findAll();

        Map<String, Integer> salarySum = new HashMap<>();
        Map<String, Integer> salaryCount = new HashMap<>();

        for (Vacancy vacancy : vacancies) {
            if (vacancy.getSalary() == null) {
                continue;
            }

            String company = vacancy.getCompany();

            if (company == null || company.isBlank()) {
                company = "Не указана";
            }

            salarySum.put(company, salarySum.getOrDefault(company, 0) + vacancy.getSalary());
            salaryCount.put(company, salaryCount.getOrDefault(company, 0) + 1);
        }

        System.out.println("\nСредняя зарплата по компаниям:");

        for (String company : salarySum.keySet()) {
            int avg = salarySum.get(company) / salaryCount.get(company);
            System.out.println("  " + company + " -> " + avg + " руб");
        }
    }

    private String detectSpeciality(String title) {
        if (title == null || title.isBlank()) {
            return "Не указано";
        }

        List<String> skipWords = List.of(
                "ведущий",
                "главный",
                "старший",
                "младший",
                "системный",
                "специалист",
                "data"
        );

        String[] words = title.trim().split("\\s+");

        for (String word : words) {
            String cleaned = word.replaceAll("[^а-яА-Яa-zA-Z-]", "");

            if (!skipWords.contains(cleaned.toLowerCase())) {
                return cleaned;
            }
        }

        return words[0];
    }
}