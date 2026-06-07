package org.example.model;

public class NotificationSettings {

    private String keyword;
    private Integer minSalary;
    private String city;

    public NotificationSettings() {
    }

    public NotificationSettings(
            String keyword,
            Integer minSalary,
            String city
    ) {
        this.keyword = keyword;
        this.minSalary = minSalary;
        this.city = city;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Integer minSalary) {
        this.minSalary = minSalary;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}