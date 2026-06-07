package org.example.service;

import org.example.model.NotificationSettings;
import org.example.model.Vacancy;
import org.example.repository.VacancyRepository;

import java.util.*;
import java.util.stream.Collectors;

public class NotificationService {

    private final VacancyRepository repository;
    private NotificationSettings settings;

    public NotificationService(VacancyRepository repository) {
        this.repository = repository;
    }

    public void updateSettings(NotificationSettings settings) {
        this.settings = settings;
    }

    public void checkNewVacancies(List<Vacancy> newVacancies) {

        Set<String> existingUrls = repository.findAll().stream()
                .map(Vacancy::getSourceUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Vacancy> fresh = newVacancies.stream()
                .filter(v -> v.getSourceUrl() != null)
                .filter(v -> !existingUrls.contains(v.getSourceUrl()))
                .collect(Collectors.toList());

        List<Vacancy> filtered = fresh.stream()
                .filter(this::matchesKeyword)
                .filter(this::matchesCity)
                .filter(this::matchesSalary)
                .toList();

        if (!filtered.isEmpty()) {
            System.out.println("\n НОВЫЕ ВАКАНСИИ по вашим настройкам");

            for (Vacancy v : filtered) {
                System.out.println( v.getTitle());
                System.out.println( v.getCompany());
                System.out.println( v.getCity());
                System.out.println(  (v.getSalary() == null ? "Не указана" : v.getSalary()));
                System.out.println( v.getSourceUrl());
                System.out.println("-------------------------");
            }
        }
    }

    private boolean matchesKeyword(Vacancy v) {
        if (settings == null || settings.getKeyword() == null) return true;

        String k = settings.getKeyword().toLowerCase();

        return safe(v.getTitle()).contains(k)
                || safe(v.getDescription()).contains(k)
                || safe(v.getRequirements()).contains(k);
    }

    private boolean matchesCity(Vacancy v) {
        if (settings == null || settings.getCity() == null) return true;

        return safe(v.getCity())
                .equalsIgnoreCase(settings.getCity());
    }

    private boolean matchesSalary(Vacancy v) {
        if (settings == null || settings.getMinSalary() == null) return true;

        return v.getSalary() != null
                && v.getSalary() >= settings.getMinSalary();
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase();
    }
}