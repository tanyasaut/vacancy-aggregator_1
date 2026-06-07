package org.example.model;

public class Vacancy {
    private int id;
    private String title;
    private String company;
    private String city;
    private Integer salary;
    private String description;
    private String requirements;
    private String schedule;
    private String publishDate;
    private String sourceUrl;


    public Vacancy() {}

    public Vacancy(String title, String company, String city, Integer salary,
                   String description, String requirements, String publishDate, String sourceUrl) {
        this.title = title;
        this.company = company;
        this.city = city;
        this.salary = salary;
        this.description = description;
        this.requirements = requirements;
        this.publishDate = publishDate;
        this.sourceUrl = sourceUrl;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Integer getSalary() { return salary; }
    public void setSalary(Integer salary) { this.salary = salary; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public String getPublishDate() { return publishDate; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    @Override
    public String toString() {

        String cityStr = (city == null || city.isBlank())
                ? "Не указан"
                : city;

        String salaryStr = salary != null ? salary + " руб" : "з/п не указана";

        return title + " | " + company + " | " + cityStr + " | " + salaryStr;
    }
}