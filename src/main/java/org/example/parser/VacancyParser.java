package org.example.parser;

import org.example.model.Vacancy;

import java.util.List;

public interface VacancyParser {

    String getSourceName();

    List<Vacancy> parse() throws Exception;
}