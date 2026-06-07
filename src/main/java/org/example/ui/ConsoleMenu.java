package org.example.ui;

import org.example.model.NotificationSettings;
import org.example.model.Vacancy;
import org.example.parser.AggregatorParser;
import org.example.parser.HabrCareerParser;
import org.example.parser.TrudvsemParser;
import org.example.parser.VacancyParser;
import org.example.repository.NotificationSettingsRepository;
import org.example.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private final VacancyService vacancyService;
    private final AnalyticsService analyticsService;
    private final ExportService exportService;
    private final NotificationService notificationService;
    private final NotificationSettingsRepository settingsRepository;

    private final List<String> activeSources;
    private final HabrCareerParser habrParser;
    private final TrudvsemParser trudvsemParser;

    public ConsoleMenu(
            VacancyService vacancyService,
            AnalyticsService analyticsService,
            ExportService exportService,
            NotificationService notificationService,
            NotificationSettingsRepository settingsRepository,
            List<String> activeSources,
            HabrCareerParser habrParser,
            TrudvsemParser trudvsemParser) {

        this.vacancyService = vacancyService;
        this.analyticsService = analyticsService;
        this.exportService = exportService;
        this.notificationService = notificationService;
        this.settingsRepository = settingsRepository;
        this.activeSources = activeSources;
        this.habrParser = habrParser;
        this.trudvsemParser = trudvsemParser;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("===== АГРЕГАТОР ВАКАНСИЙ =====");
            System.out.println("1.  Загрузить вакансии (обновить)");
            System.out.println("2.  Показать все вакансии");
            System.out.println("3.  Поиск вакансий");
            System.out.println("4.  Фильтр вакансий");
            System.out.println("5.  Сортировка вакансий");
            System.out.println("6.  Аналитика (статистика)");
            System.out.println("7.  Экспорт данных");
            System.out.println("8.  Настройки уведомлений");
            System.out.println("9.  Настройка источников");
            System.out.println("0.  Выход");

            System.out.print("Выберите: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> loadVacanciesFromActiveSources();
                case "2" -> showAll(scanner);
                case "3" -> search(scanner);
                case "4" -> showFilters(scanner);
                case "5" -> showSortMenu(scanner);
                case "6" -> showAnalytics(scanner);
                case "7" -> showExportMenu(scanner);
                case "8" -> configureNotifications(scanner);
                case "9" -> configureSources(scanner);
                case "0" -> {
                    System.out.println("До свидания!");
                    return;
                }
                default -> System.out.println("Неверная опция.");
            }
        }
    }

    private void loadVacanciesFromActiveSources() {
        if (activeSources.isEmpty()) {
            System.out.println("Источники не выбраны! Настройте их в пункте 9.");
            return;
        }

        System.out.println("Загрузка вакансий из: " + activeSources);

        List<VacancyParser> currentParsers = new ArrayList<>();
        if (activeSources.contains("habr")) currentParsers.add(habrParser);
        if (activeSources.contains("trudvsem")) currentParsers.add(trudvsemParser);

        VacancyParser tempAggregator = new AggregatorParser(currentParsers);
        vacancyService.setParser(tempAggregator);

        try {
            List<Vacancy> loaded = tempAggregator.parse();
            for (Vacancy v : loaded) {
                vacancyService.saveVacancy(v);
            }
            System.out.println("Загружено: " + loaded.size() + " вакансий");
        } catch (Exception e) {
            System.out.println("Ошибка загрузки: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAll(Scanner scanner) {
        List<Vacancy> vacancies = vacancyService.getAllVacancies();

        if (vacancies.isEmpty()) {
            System.out.println("Вакансии не найдены. Сначала загрузите вакансии.");
            return;
        }

        while (true) {
            System.out.println("\n=== ВСЕ ВАКАНСИИ ===");

            for (int i = 0; i < vacancies.size(); i++) {
                Vacancy v = vacancies.get(i);
                String salary = v.getSalary() != null ? v.getSalary() + " руб" : "Не указана";
                System.out.printf("%d. %s | %s | %s | %s%n",
                        i + 1, v.getTitle(), v.getCompany(), v.getCity(), salary);
            }

            System.out.println("0. Вернуться в главное меню");
            System.out.print("Выберите вакансию: ");

            int num;
            try {
                num = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Неверный ввод");
                continue;
            }

            if (num == 0) return;
            if (num < 0 || num > vacancies.size()) {
                System.out.println("Неверный номер");
                continue;
            }

            try {
                showVacancyDetails(vacancies.get(num - 1), scanner);
            } catch (RuntimeException e) {
                if ("BACK_TO_MENU".equals(e.getMessage())) return;
            }
        }
    }

    private void showVacancyDetails(Vacancy v, Scanner scanner) {
        while (true) {
            System.out.println("\n=== ДЕТАЛИ ВАКАНСИИ ===");
            System.out.println("Должность: " + v.getTitle());
            System.out.println("Компания: " + v.getCompany());
            System.out.println("Город: " + v.getCity());
            System.out.println("График: " + v.getSchedule());
            System.out.println("Зарплата: " + (v.getSalary() != null ? v.getSalary() + " руб" : "Не указана"));
            System.out.println("Описание: " + v.getDescription());
            System.out.println("Требования: " + v.getRequirements());
            System.out.println("Опубликовано: " + v.getPublishDate());
            System.out.println("Ссылка: " + v.getSourceUrl());

            System.out.println("\n=== ДЕЙСТВИЯ ===");
            System.out.println("1. Посмотреть другую вакансию");
            System.out.println("0. Вернуться в главное меню");
            System.out.print("Выберите: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": return;
                case "0": throw new RuntimeException("BACK_TO_MENU");
                default: System.out.println("Неверная опция");
            }
        }
    }

    private void showVacancyList(List<Vacancy> vacancies, Scanner scanner) {
        if (vacancies.isEmpty()) {
            System.out.println("Ничего не найдено.");
            return;
        }

        System.out.println("\nНайдено: " + vacancies.size());

        for (int i = 0; i < vacancies.size(); i++) {
            Vacancy v = vacancies.get(i);
            String salary = v.getSalary() != null ? v.getSalary() + " руб" : "Зарплата не указана";
            System.out.println((i + 1) + ". " + v.getTitle() + " | " + v.getCompany() + " | " + salary);
        }

        System.out.print("\nВведите номер вакансии (0 = назад): ");
        try {
            int num = Integer.parseInt(scanner.nextLine());
            if (num > 0 && num <= vacancies.size()) {
                showVacancyDetails(vacancies.get(num - 1), scanner);
            }
        } catch (Exception e) {
            System.out.println("Неверный ввод");
        }
    }

    private void search(Scanner scanner) {
        System.out.print("Ключевое слово: ");
        String keyword = scanner.nextLine();
        List<Vacancy> vacancies = vacancyService.search(keyword);
        showVacancyList(vacancies, scanner);
    }

    private void showFilters(Scanner scanner) {
        while (true) {
            System.out.println("\n=== ФИЛЬТРЫ ===");
            System.out.println("1. По городу");
            System.out.println("2. По компании");
            System.out.println("3. По минимальной зарплате");
            System.out.println("4. По графику работы");
            System.out.println("5. По ключевому слову в описании");
            System.out.println("0. Назад");
            System.out.print("Выберите: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> filterByCity(scanner);
                case "2" -> filterByCompany(scanner);
                case "3" -> filterBySalary(scanner);
                case "4" -> filterBySchedule(scanner);
                case "5" -> filterByDescription(scanner);
                case "0" -> { return; }
                default -> System.out.println("Неверная опция");
            }
        }
    }

    private void filterByCity(Scanner scanner) {
        System.out.print("Город: ");
        String city = scanner.nextLine();
        showVacancyList(vacancyService.findByCity(city), scanner);
    }

    private void filterBySchedule(Scanner scanner) {
        System.out.print("График работы: ");
        String schedule = scanner.nextLine();
        showVacancyList(vacancyService.findBySchedule(schedule), scanner);
    }

    private void filterByCompany(Scanner scanner) {
        System.out.print("Компания: ");
        String company = scanner.nextLine();
        showVacancyList(vacancyService.findByCompany(company), scanner);
    }

    private void filterBySalary(Scanner scanner) {
        System.out.print("Минимальная зарплата: ");
        try {
            int salary = Integer.parseInt(scanner.nextLine());
            showVacancyList(vacancyService.findByMinSalary(salary), scanner);
        } catch (NumberFormatException e) {
            System.out.println("Пожалуйста, введите число");
        }
    }

    private void filterByDescription(Scanner scanner) {
        System.out.print("Ключевое слово в описании: ");
        String keyword = scanner.nextLine();
        showVacancyList(vacancyService.findByDescriptionKeyword(keyword), scanner);
    }

    private void showSortMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n=== СОРТИРОВКА ===");
            System.out.println("1. По зарплате (по убыванию)");
            System.out.println("2. По дате (сначала новые)");
            System.out.println("3. По компании (А-Я)");
            System.out.println("0. Назад");
            System.out.print("Выберите: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> showVacancyList(vacancyService.sortBySalary(), scanner);
                case "2" -> showVacancyList(vacancyService.sortByDateDesc(), scanner);
                case "3" -> showVacancyList(vacancyService.sortByCompany(), scanner);
                case "0" -> { return; }
                default -> System.out.println("Неверная опция.");
            }
        }
    }

    private void showExportMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n=== ЭКСПОРТ ДАННЫХ ===");
            System.out.println("1. Экспорт в CSV");
            System.out.println("2. Экспорт в JSON");
            System.out.println("3. Экспорт в HTML");
            System.out.println("0. Назад");
            System.out.print("Выберите: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> exportService.exportToCsv(vacancyService.getAllVacancies());
                case "2" -> exportService.exportToJson(vacancyService.getAllVacancies());
                case "3" -> exportService.exportToHtml(vacancyService.getAllVacancies());
                case "0" -> { return; }
                default -> System.out.println("Неверная опция.");
            }
        }
    }

    private void showAnalytics(Scanner scanner) {
        while (true) {
            System.out.println("\n=== АНАЛИТИКА ===");
            System.out.println("1. Вакансии по городам");
            System.out.println("2. Средняя зарплата по городам");
            System.out.println("3. Средняя зарплата по компаниям");
            System.out.println("4. Категории зарплат");
            System.out.println("5. Вакансии по компаниям");
            System.out.println("0. Назад");
            System.out.print("Выберите: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> analyticsService.printVacanciesByCity();
                case "2" -> analyticsService.printAverageSalaryByCity();
                case "3" -> analyticsService.printAverageSalaryByCompany();
                case "4" -> analyticsService.printSalaryCategories();
                case "5" -> analyticsService.printVacanciesByCompany();
                case "0" -> { return; }
                default -> System.out.println("Неверная опция");
            }
        }
    }

    private void exportCsv() { exportService.exportToCsv(vacancyService.getAllVacancies()); }
    private void exportJson() { exportService.exportToJson(vacancyService.getAllVacancies()); }
    private void exportHtml() { exportService.exportToHtml(vacancyService.getAllVacancies()); }

    private void configureNotifications(Scanner scanner) {
        System.out.print("Введите ключевое слово (или оставьте пустым): ");
        String keyword = scanner.nextLine();
        if (keyword.isBlank()) keyword = null;

        System.out.print("Введите минимальную зарплату (или 0): ");
        Integer minSalary;
        try {
            int salary = Integer.parseInt(scanner.nextLine());
            minSalary = salary == 0 ? null : salary;
        } catch (NumberFormatException e) {
            System.out.println("Пожалуйста, введите число");
            return;
        }

        System.out.print("Введите город (или оставьте пустым): ");
        String city = scanner.nextLine();
        if (city.isBlank()) city = null;

        NotificationSettings newSettings = new NotificationSettings(keyword, minSalary, city);
        notificationService.updateSettings(newSettings);
        settingsRepository.save(newSettings);
        System.out.println(" Настройки обновлены!");
    }

    private void configureSources(Scanner scanner) {
        while (true) {
            System.out.println("\n=== НАСТРОЙКА ИСТОЧНИКОВ ===");
            System.out.println("Активные источники: " + activeSources);
            System.out.println();
            System.out.println("1. Habr Career (сейчас: " + (activeSources.contains("habr") ? "ВКЛ" : "ВЫКЛ") + ")");
            System.out.println("2. Trudvsem (сейчас: " + (activeSources.contains("trudvsem") ? "ВКЛ" : "ВЫКЛ") + ")");
            System.out.println("3. Включить все источники");
            System.out.println("4. Отключить все источники");
            System.out.println("0. Назад");

            System.out.print("Выберите: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    if (activeSources.contains("habr")) {
                        activeSources.remove("habr");
                        System.out.println("Habr Career отключён");
                    } else {
                        activeSources.add("habr");
                        System.out.println("Habr Career включён");
                    }
                }
                case "2" -> {
                    if (activeSources.contains("trudvsem")) {
                        activeSources.remove("trudvsem");
                        System.out.println("Trudvsem отключён");
                    } else {
                        activeSources.add("trudvsem");
                        System.out.println("Trudvsem включён");
                    }
                }
                case "3" -> {
                    activeSources.clear();
                    activeSources.add("habr");
                    activeSources.add("trudvsem");
                    System.out.println("Все источники включены");
                }
                case "4" -> {
                    activeSources.clear();
                    System.out.println("Все источники отключены");
                }
                case "0" -> { return; }
                default -> System.out.println("Неверная опция");
            }
        }
    }
}