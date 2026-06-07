package org.example;

import org.example.db.DatabaseManager;
import org.example.https.HttpFetcher;
import org.example.parser.*;
import org.example.repository.HistoryRepository;
import org.example.repository.NotificationSettingsRepository;
import org.example.repository.VacancyRepository;
import org.example.service.*;
import org.example.ui.ConsoleMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        System.out.println("===== АГРЕГАТОР ВАКАНСИЙ =====");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Сколько страниц парсить? (1-5): ");
        int maxPages = 2;

        try {
            maxPages = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            maxPages = 2;
        }

        if (maxPages < 1) maxPages = 1;
        if (maxPages > 5) maxPages = 5;

        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.createTables();

        VacancyRepository vacancyRepository = new VacancyRepository(databaseManager);
        HistoryRepository historyRepository = new HistoryRepository(databaseManager);
        NotificationSettingsRepository settingsRepository = new NotificationSettingsRepository(databaseManager);

        HttpFetcher fetcher = new HttpFetcher();
        String searchKeyword = "";

        HabrCareerParser habrParser = new HabrCareerParser(fetcher, searchKeyword, maxPages);
        TrudvsemParser trudvsemParser = new TrudvsemParser(fetcher, searchKeyword, maxPages);

        List<String> activeSources = new ArrayList<>(List.of("habr", "trudvsem"));

        VacancyParser parser = new AggregatorParser(List.of(habrParser, trudvsemParser));

        VacancyService vacancyService = new VacancyService(
                vacancyRepository,
                historyRepository,
                parser,
                settingsRepository
        );

        NotificationService notificationService = new NotificationService(vacancyRepository);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                vacancyService.loadVacanciesWithNotification(notificationService);
            } catch (Exception e) {
                System.out.println("Ошибка автообновления: " + e.getMessage());
            }
        }, 10, 10, TimeUnit.MINUTES);

        System.out.println("\nПервичная загрузка вакансий...");
        vacancyService.loadVacancies();

        AnalyticsService analyticsService = new AnalyticsService(vacancyRepository);
        ExportService exportService = new ExportService();

        ConsoleMenu menu = new ConsoleMenu(
                vacancyService,
                analyticsService,
                exportService,
                notificationService,
                settingsRepository,
                activeSources,
                habrParser,
                trudvsemParser
        );

        menu.start();
    }
}