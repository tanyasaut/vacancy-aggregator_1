package org.example.parser;

import org.example.https.HttpFetcher;
import org.example.model.Vacancy;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrudvsemParser implements VacancyParser {

    private final HttpFetcher fetcher;
    private String searchKeyword;
    private int maxPages;
    private static final String API_URL = "https://opendata.trudvsem.ru/api/v1/vacancies";

    public TrudvsemParser(HttpFetcher fetcher, String searchKeyword, int maxPages) {
        this.fetcher = fetcher;
        this.searchKeyword = searchKeyword;
        this.maxPages = Math.min(maxPages, 2);
    }

    @Override
    public String getSourceName() {
        return "trudvsem.ru";
    }

    @Override
    public List<Vacancy> parse() throws Exception {
        List<Vacancy> result = new ArrayList<>();
        Set<String> seenLinks = new HashSet<>();

        for (int page = 0; page < maxPages; page++) {
            int offset = page * 100;
            String url = API_URL + "?text=" + searchKeyword.replace(" ", "+")
                    + "&offset=" + offset + "&limit=100";
            System.out.println("Парсинг Trudvsem.ru страница " + (page + 1) + ": " + url);

            String jsonResponse = fetcher.get(url);
            JSONObject root = new JSONObject(jsonResponse);

            if (!root.has("status") || !root.getString("status").equals("200")) {
                System.out.println("  Ошибка API: " + root.optString("status"));
                break;
            }

            JSONObject results = root.optJSONObject("results");
            if (results == null) break;

            JSONArray vacanciesArray = results.optJSONArray("vacancies");
            if (vacanciesArray == null || vacanciesArray.isEmpty()) {
                System.out.println("  Не найдено вакансий");
                break;
            }

            System.out.println("  Найдено вакансий: " + vacanciesArray.length());

            for (int i = 0; i < vacanciesArray.length(); i++) {
                JSONObject item = vacanciesArray.getJSONObject(i);
                JSONObject vacancy = item.optJSONObject("vacancy");
                if (vacancy == null) continue;

                String link = vacancy.optString("vac_url", "");
                if (link.isEmpty()) {
                    String vacancyId = vacancy.optString("id", "");
                    link = "https://trudvsem.ru/vacancy/card/" + vacancyId;
                }

                if (seenLinks.contains(link)) continue;
                seenLinks.add(link);

                String title = vacancy.optString("job-name", "Вакансия");
                if (title.isEmpty()) title = "Вакансия";

                JSONObject companyObj = vacancy.optJSONObject("company");
                String company = companyObj != null ? companyObj.optString("name", "Не указана") : "Не указана";

                String city = extractCity(vacancy);

                Integer salary = extractSalary(vacancy);

                String schedule = vacancy.optString("schedule", "Не указан");
                if (schedule.isEmpty()) schedule = "Не указан";


                String description = vacancy.optString("duty", "");
                if (description.isEmpty()) {
                    description = "Описание отсутствует";
                } else {
                    description = safeTruncate(description, 500);
                }

                String requirements = extractRequirements(vacancy);

                String publishDate = vacancy.optString("creation-date", "");
                if (publishDate.isEmpty()) {
                    publishDate = vacancy.optString("date_modify", "");
                }
                if (!publishDate.isEmpty() && publishDate.length() > 10) {
                    publishDate = publishDate.substring(0, 10);
                }
                if (publishDate.isEmpty()) publishDate = "Не указана";

                Vacancy v = new Vacancy();
                v.setTitle(title);
                v.setCompany(company);
                v.setCity(city);
                v.setSalary(salary);
                v.setDescription(description);
                v.setRequirements(requirements);
                v.setSchedule(schedule);
                v.setPublishDate(publishDate);
                v.setSourceUrl(link);

                result.add(v);
            }

            Thread.sleep(1000);
        }

        System.out.println("Всего спарсено с Trudvsem.ru: " + result.size());
        return result;
    }

    private String extractCity(JSONObject vacancy) {
        String city = "Не указан";

        JSONObject regionObj = vacancy.optJSONObject("region");
        if (regionObj != null) {
            city = regionObj.optString("name", "");
        }

        if (city.isEmpty() || city.equals("Не указан")) {
            JSONObject addressesObj = vacancy.optJSONObject("addresses");
            if (addressesObj != null) {
                JSONArray addressArray = addressesObj.optJSONArray("address");
                if (addressArray != null && addressArray.length() > 0) {
                    JSONObject firstAddress = addressArray.getJSONObject(0);
                    city = firstAddress.optString("locality", "");
                    if (city.isEmpty()) city = firstAddress.optString("city", "");
                }
            }
        }

        city = cleanCityName(city);

        return city;
    }

    private String cleanCityName(String cityName) {
        if (cityName == null || cityName.isEmpty() || cityName.equals("Не указан")) {
            return cityName;
        }

        String[] prefixes = {
                "name: ",
                "name:",
                "Город ",
                "город ",
                "Республика ",
                "республика ",
                "обл. ",
                "область ",
                "г. ",
                "г.",
                "область"
        };

        boolean changed = true;
        while (changed) {
            changed = false;
            for (String prefix : prefixes) {
                if (cityName.startsWith(prefix)) {
                    cityName = cityName.substring(prefix.length()).trim();
                    changed = true;
                    break;
                }
            }
        }

        if (cityName.isEmpty() || cityName.equals("Не указан")) {
            return "Не указан";
        }

        return cityName;
    }

    private Integer extractSalary(JSONObject vacancy) {
        Integer salary = null;

        String salaryMin = vacancy.optString("salary_min", "");
        if (!salaryMin.isEmpty()) {
            try {
                salary = Integer.parseInt(salaryMin);
            } catch (NumberFormatException e) {}
        }

        if (salary == null) {
            String salaryMax = vacancy.optString("salary_max", "");
            if (!salaryMax.isEmpty()) {
                try {
                    salary = Integer.parseInt(salaryMax);
                } catch (NumberFormatException e) {}
            }
        }

        if (salary == null) {
            String salaryText = vacancy.optString("salary", "");
            if (!salaryText.isEmpty()) {
                String digits = salaryText.replaceAll("[^0-9]", "");
                if (!digits.isEmpty()) {
                    try {
                        salary = Integer.parseInt(digits);
                    } catch (NumberFormatException e) {}
                }
            }
        }

        return salary;
    }

    private String extractRequirements(JSONObject vacancy) {
        StringBuilder reqBuilder = new StringBuilder();

        // 1. Сначала проверяем объект "requirement" (там лежат образование и опыт)
        JSONObject reqObj = vacancy.optJSONObject("requirement");
        if (reqObj != null) {
            String education = reqObj.optString("education", "");
            String experience = reqObj.optString("experience", "");

            if (!education.isEmpty() && !education.equalsIgnoreCase("Не указано")) {
                reqBuilder.append("Образование: ").append(education).append(". ");
            }
            if (!experience.isEmpty() && !experience.equalsIgnoreCase("Не указано")) {
                reqBuilder.append("Опыт работы: ").append(experience).append(". ");
            }
        }

        String reqString = vacancy.optString("requirements", "");
        if (!reqString.isEmpty()) {
            reqBuilder.append(reqString);
        }

        if (reqBuilder.length() == 0) {
            return "Не указаны";
        }

        return safeTruncate(reqBuilder.toString().trim(), 300);
    }


    private String safeTruncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}