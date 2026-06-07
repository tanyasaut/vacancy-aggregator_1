package org.example.parser;

import org.example.model.Vacancy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AggregatorParser implements VacancyParser {

    private final List<VacancyParser> parsers;

    public AggregatorParser(List<VacancyParser> parsers) {
        this.parsers = parsers;
    }
    @Override
    public String getSourceName() {
        return "aggregator";
    }

    @Override
    public List<Vacancy> parse() throws Exception {
        List<Vacancy> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (VacancyParser parser : parsers) {

            for (Vacancy vacancy : parser.parse()) {

                String key = vacancy.getTitle()
                        + vacancy.getCompany()
                        + vacancy.getCity();

                if (seen.contains(key)) continue;

                seen.add(key);
                result.add(vacancy);
            }
        }

        return result;
    }
}