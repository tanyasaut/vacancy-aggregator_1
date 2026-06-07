package org.example.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerService {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void start(VacancyService vacancyService, NotificationService notificationService) {
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\nЗАПУСК АВТООБНОВЛЕНИЯ...");
            vacancyService.loadVacanciesWithNotification(notificationService);
        }, 5, 6, TimeUnit.HOURS);

        System.out.println(" Планировщик автообновления запущен (каждые 6 часов)");
    }
}