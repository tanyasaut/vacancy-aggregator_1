package org.example.model;

public class HistoryRecord {

    private int id;
    private String action;
    private String vacancyUrl;
    private String actionDate;

    public HistoryRecord() {
    }

    public HistoryRecord(
            String action,
            String vacancyUrl,
            String actionDate)
    {
        this.action = action;
        this.vacancyUrl = vacancyUrl;
        this.actionDate = actionDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getVacancyUrl() {
        return vacancyUrl;
    }

    public void setVacancyUrl(String vacancyUrl) {
        this.vacancyUrl = vacancyUrl;
    }

    public String getActionDate() {
        return actionDate;
    }

    public void setActionDate(String actionDate) {
        this.actionDate = actionDate;
    }

    @Override
    public String toString() {
        return "HistoryRecord{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", vacancyId=" + vacancyUrl +
                ", actionDate='" + actionDate + '\'' +
                '}';
    }
}