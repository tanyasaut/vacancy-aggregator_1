package org.example.parser;

import org.example.https.HttpFetcher;
import org.example.model.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.example.util.DataCleaner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HabrCareerParser implements VacancyParser {

    private final HttpFetcher fetcher;
    private String searchKeyword;
    private int maxPages;

    public HabrCareerParser(HttpFetcher fetcher, String searchKeyword, int maxPages) {
        this.fetcher = fetcher;
        this.searchKeyword = searchKeyword;
        this.maxPages = maxPages;
    }

    @Override
    public String getSourceName() {
        return "habr.career.detailed";
    }

    @Override
    public List<Vacancy> parse() throws Exception {

        List<Vacancy> result = new ArrayList<>();
        Set<String> seenLinks = new HashSet<>();

        for (int page = 1; page <= maxPages; page++) {

            String url = "https://career.habr.com/vacancies?q="
                    + searchKeyword.replace(" ", "+")
                    + "&page=" + page;

            System.out.println("Парсинг страницы HarbrCareer: " + url);

            String html = fetcher.get(url);
            Document doc = Jsoup.parse(html);

            Elements items = doc.select("div.vacancy-card");

            if (items.isEmpty()) {
                System.out.println("Вакансии не найдены");
                break;
            }

            System.out.println("Найдено вакансий: " + items.size());

            java.util.concurrent.ExecutorService executor =
                    java.util.concurrent.Executors.newFixedThreadPool(5);

            List<java.util.concurrent.Future<Vacancy>> futures = new ArrayList<>();

            for (Element item : items) {

                Element titleLink = item.selectFirst("a.vacancy-card__title-link");
                if (titleLink == null) continue;

                String title = titleLink.text().trim();
                String link = titleLink.attr("href");

                if (!link.startsWith("http")) {
                    link = "https://career.habr.com" + link;
                }

                if (!seenLinks.add(link)) continue;

                String finalLink = link;

                futures.add(executor.submit(() -> parseVacancyDetails(finalLink, title)));
            }

            for (java.util.concurrent.Future<Vacancy> f : futures) {
                try {
                    Vacancy v = f.get();
                    if (v != null) {
                        result.add(v);
                    }
                } catch (Exception ignored) {
                }
            }

            executor.shutdown();
        }

        System.out.println("Всего спарсено: " + result.size());
        return result;
    }

    private Vacancy parseVacancyDetails(String vacancyUrl, String title) {
        try {
            String html = fetcher.get(vacancyUrl);
            Document doc = Jsoup.parse(html);

            String company = extractCompany(doc);

            String city = extractCity(doc);

            Integer salary = extractSalary(doc);

            String description = extractDescription(doc);

            String requirements = extractRequirements(doc);

            String schedule = extractSchedule(doc);

            String publishDate = extractPublishDate(doc);

            Vacancy vacancy = new Vacancy();
            vacancy.setTitle(title);
            vacancy.setCity(DataCleaner.cleanCity(city));
            vacancy.setCompany(DataCleaner.cleanCompany(company));
            vacancy.setSalary(salary);
            vacancy.setDescription(DataCleaner.cleanDescription(description));
            vacancy.setRequirements(requirements);
            vacancy.setSchedule(schedule);
            vacancy.setPublishDate(publishDate);
            vacancy.setSourceUrl(vacancyUrl);

            return vacancy;

        } catch (Exception e) {
            return null;
        }
    }

    private String extractCompany(Document doc) {

        String[] selectors = {
                "div.vacancy-company__title",
                "a.link-comp",
                "div.company_name",
                "a.vacancies_list__company-link"
        };

        for (String selector : selectors) {

            Element element = doc.selectFirst(selector);

            if (element != null) {

                String company = element.text().trim();

                if (!company.isEmpty()) {
                    return company;
                }
            }
        }

        return "Не указана";
    }

    private String extractCity(Document doc) {

        Elements chips = doc.select(".basic-chip");

        for (Element chip : chips) {

            if (chip.html().contains("placemark")) {

                Element cityElement =
                        chip.selectFirst(".chip-with-icon__text");

                if (cityElement != null) {

                    String city = cityElement.text().trim();

                    if (!city.isEmpty()) {
                        return city;
                    }
                }
            }
        }

        return "Не указан";
    }

    private Integer extractSalary(Document doc) {

        String[] salarySelectors = {
                "div.vacancy-salary",
                "div.salary",
                "span[data-testid='vacancy-salary']",
                "div[class*=salary]",
                "span[class*=salary]"
        };

        for (String selector : salarySelectors) {

            Elements elements = doc.select(selector);

            for (Element elem : elements) {

                String salaryText = elem.text().trim();

                if (salaryText.isEmpty()) {
                    continue;
                }

                if (salaryText.contains("Похожие специалисты")) {
                    continue;
                }

                Integer salary = parseSalaryFromText(salaryText);

                if (salary != null) {
                    return salary;
                }
            }
        }

        return null;
    }

    private Integer parseSalaryFromText(String text) {

        if (text == null || text.isBlank()) {
            return null;
        }

        text = text.replace('\u00A0', ' ');

        List<Integer> numbers = new ArrayList<>();

        Pattern pattern =
                Pattern.compile("(\\d[\\d\\s]{2,})");

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {

            String value =
                    matcher.group(1)
                            .replaceAll("\\s+", "");

            try {

                int salary = Integer.parseInt(value);

                if (salary > 1000) {
                    numbers.add(salary);
                }

            } catch (NumberFormatException ignored) {
            }
        }

        if (numbers.isEmpty()) {
            return null;
        }

        if (numbers.size() >= 2) {

            int from = numbers.get(0);
            int to = numbers.get(1);

            return (from + to) / 2;
        }

        return numbers.get(0);
    }

    private String extractDescription(Document doc) {
        String[] selectors = {
                "div.vacancy-description",
                "div.vacancy-section",
                "div[class*='description']"
        };

        for (String selector : selectors) {
            Element elem = doc.selectFirst(selector);
            if (elem != null) {
                String desc = elem.text().trim();
                if (!desc.isEmpty()) {
                    if (desc.length() > 500) {
                        desc = desc.substring(0, 500) + "...";
                    }
                    return desc;
                }
            }
        }
        return "Описание отсутствует";
    }

    private String extractRequirements(Document doc) {

        String[] classSelectors = {
                "div.vacancy-requirements",
                "div.vacancy-section--requirements",
                "div[class*='requirement']",
                "div[class*='skill']",
                "section[class*='requirements']",
                "div.paragraph.paragraph_black"
        };

        for (String selector : classSelectors) {
            Element elem = doc.selectFirst(selector);
            if (elem != null) {
                String text = elem.text().trim();
                if (text.length() > 20) {
                    text = text.replaceAll("\\s+", " ").trim();
                    if (text.length() > 500) {
                        text = text.substring(0, 500) + "...";
                    }
                    return text;
                }
            }
        }

        Elements headings = doc.select("h2, h3, div[class*='title'], div[class*='heading']");
        for (Element heading : headings) {
            String headingText = heading.text().toLowerCase();
            if (headingText.contains("требова") || headingText.contains("requirements") ||
                    headingText.contains("навык") || headingText.contains("skills")) {

                Element next = heading.nextElementSibling();
                if (next != null) {
                    String text = next.text().trim();
                    if (text.length() > 20) {
                        text = text.replaceAll("\\s+", " ").trim();
                        if (text.length() > 500) {
                            text = text.substring(0, 500) + "...";
                        }
                        return text;
                    }
                }
            }
        }

        Elements lists = doc.select("ul, ol");
        for (Element list : lists) {
            if (list.children().size() <= 15 && list.children().size() >= 2) {
                String text = list.text().trim();
                if (text.length() > 30 && text.length() < 1000) {
                    if (text.toLowerCase().contains("java") ||
                            text.toLowerCase().contains("python") ||
                            text.toLowerCase().contains("spring") ||
                            text.toLowerCase().contains("sql") ||
                            text.toLowerCase().contains("git") ||
                            text.toLowerCase().contains("docker")) {
                        return text;
                    }
                }
            }
        }


        Element descElem = doc.selectFirst("div.vacancy-description");
        if (descElem != null) {
            String desc = descElem.text().trim();
            if (desc.length() > 100) {

                String excerpt = desc.substring(0, Math.min(300, desc.length()));
                return "Из описания: " + excerpt + "...";
            }
        }

        return "Не указаны";
    }

    private String extractSchedule(Document doc) {

        String text = doc.text().toLowerCase();

        if (text.contains("remote") || text.contains("удал") || text.contains("удалён")) {
            return "Удалённая работа";
        }

        if (text.contains("гибрид") || text.contains("hybrid")) {
            return "Гибрид";
        }

        if (text.contains("офис") || text.contains("onsite")) {
            return "Офис";
        }

        return "Не указан";
    }

    private String extractPublishDate(Document doc) {

        Element timeElement = doc.selectFirst("time.basic-date");

        if (timeElement != null) {

            String dateTime = timeElement.attr("datetime");

            if (!dateTime.isEmpty()) {


                if (dateTime.length() >= 10) {
                    return dateTime.substring(0, 10);
                }

                return dateTime;
            }
        }

        return "Не указана";
    }
}