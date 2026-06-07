package org.example.service;

import org.example.model.HistoryRecord;
import org.example.model.NotificationSettings;
import org.example.model.Vacancy;
import org.example.parser.AggregatorParser;
import org.example.parser.VacancyParser;
import org.example.repository.HistoryRepository;
import org.example.repository.NotificationSettingsRepository;
import org.example.repository.VacancyRepository;

import java.time.LocalDateTime;
import java.util.List;

public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final HistoryRepository historyRepository;
    private  VacancyParser parser;
    private final NotificationSettingsRepository settingsRepository;

    public VacancyService(
            VacancyRepository vacancyRepository,
            HistoryRepository historyRepository,
            VacancyParser parser,
            NotificationSettingsRepository settingsRepository
    ) {
        this.vacancyRepository = vacancyRepository;
        this.historyRepository = historyRepository;
        this.parser = parser;
        this.settingsRepository = settingsRepository;
    }


    public void loadVacancies() {
        try {
            List<Vacancy> vacancies = parser.parse();

            for (Vacancy vacancy : vacancies) {
                vacancyRepository.save(vacancy);

                historyRepository.save(new HistoryRecord(
                        "ADDED",
                        vacancy.getSourceUrl(),
                        LocalDateTime.now().toString()
                ));
            }

            System.out.println("Загружено вакансий" + vacancies.size() );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Vacancy> findByCompany(String company) {

        return vacancyRepository.findByCompany(company);
    }

    public List<Vacancy> findByMinSalary(int salary) {

        return vacancyRepository.findByMinSalary(salary);
    }

    public List<Vacancy> findByCity(String city) {
        return vacancyRepository.findByCity(city);
    }

    public List<Vacancy> findBySchedule(String schedule) {

        return vacancyRepository.findBySchedule(schedule);
    }

    public List<Vacancy> findByDescriptionKeyword(String keyword) {

        return vacancyRepository
                .findByDescriptionKeyword(keyword);
    }


    public List<Vacancy> sortByDateDesc() {
        List<Vacancy> all = vacancyRepository.findAll();
        all.sort((a, b) -> {
            String dateA = a.getPublishDate();
            String dateB = b.getPublishDate();
            if (dateA == null) return 1;
            if (dateB == null) return -1;
            return dateB.compareTo(dateA);
        });
        return all;
    }

    public List<Vacancy> sortByCompany() {
        List<Vacancy> all = vacancyRepository.findAll();
        all.sort((a, b) -> {
            String companyA = a.getCompany();
            String companyB = b.getCompany();
            if (companyA == null) return 1;
            if (companyB == null) return -1;
            return companyA.compareToIgnoreCase(companyB);
        });
        return all;
    }

    public Vacancy findById(int id) {
        return vacancyRepository.findById(id);
    }


    public void saveVacancy(Vacancy vacancy) {
        vacancyRepository.save(vacancy);
    }


    public List<Vacancy> getAllVacancies() {
        return vacancyRepository.findAll();
    }


    public List<Vacancy> search(String keyword) {
        return vacancyRepository.searchByKeyword(keyword);
    }

    public List<Vacancy> sortBySalary() {
        return vacancyRepository.sortBySalaryDesc();
    }

    public void loadVacanciesWithNotification(NotificationService notificationService) {
        try {
            System.out.println("\n=== АВТООБНОВЛЕНИЕ ВАКАНСИЙ ===");

            List<Vacancy> oldVacancies = vacancyRepository.findAll();
            System.out.println("Вакансий в БД до обновления: " + oldVacancies.size());

            List<Vacancy> newVacancies = parser.parse();
            System.out.println("Получено новых вакансий с парсеров: " + newVacancies.size());

            List<String> newUrls =
                    newVacancies.stream()
                            .map(Vacancy::getSourceUrl)
                            .toList();

            NotificationSettings settings = settingsRepository.load();
            if (settings != null) {
                notificationService.updateSettings(settings);
                System.out.println("Настройки уведомлений загружены: " + settings);
            } else {
                System.out.println("Настройки уведомлений не найдены, используются значения по умолчанию");
            }

            notificationService.checkNewVacancies(newVacancies);


            int added = 0;
            for (Vacancy v : newVacancies) {
                boolean exists = oldVacancies.stream()
                        .anyMatch(o -> o.getSourceUrl() != null
                                && o.getSourceUrl().equals(v.getSourceUrl()));

                if (!exists) {

                    vacancyRepository.save(v);

                    historyRepository.save(
                            new HistoryRecord(
                                    "ADDED",
                                    v.getSourceUrl(),
                                    LocalDateTime.now().toString()
                            )
                    );

                    added++;
                }
            }

            for (Vacancy old : oldVacancies) {

                if (old.getSourceUrl() == null) {
                    continue;
                }

                if (!newUrls.contains(old.getSourceUrl())) {

                    vacancyRepository.deleteByUrl(
                            old.getSourceUrl()
                    );

                    historyRepository.save(
                            new HistoryRecord(
                                    "DELETED",
                                    old.getSourceUrl(),
                                    LocalDateTime.now().toString()
                            )
                    );
                }
            }

            System.out.println("Загружено: " + newVacancies.size()
                    + " | Добавлено новых: " + added
                    + " | Уже было в БД: " + (newVacancies.size() - added));
            System.out.println("==============================\n");

        } catch (Exception e) {
            System.out.println(" Ошибка при автообновлении: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setParser(VacancyParser parser) {
        this.parser = parser;
    }
}