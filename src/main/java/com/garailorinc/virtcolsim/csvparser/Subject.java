package com.garailorinc.virtcolsim.csvparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Subject
{
    private String name;
    private Enum gender;
    private Integer age;
    private Map<String, ResultValue> subjectResults = new TreeMap<>();

    Subject(String name, Integer age, Enum gender)
    {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public String getName()
    {
        return name;
    }

    public Integer getAge()
    {
        return age;
    }

    public Map<String, ResultValue> getSubjectResults()
    {
        return subjectResults;
    }

    public Double getAverageIntensity()
    {
        Double sum = subjectResults.values()
                .stream()
                .mapToDouble(value -> value.getIntensity())
                .sum();
        Double average = sum / subjectResults.size();
        return average;
    }

    public Double getAverageTime()
    {
        Double sum = subjectResults.values()
                .stream()
                .mapToDouble(value -> value.getTvirtcol())
                .sum();
        Double average = sum / subjectResults.size();
        return average;
    }

    public void addColorChangeResult(String colorChangeKey, ResultValue resultValue)
    {
        subjectResults.put(colorChangeKey, resultValue);
    }

    public Enum getGender()
    {
        return this.gender;
    }

    public Map<String, ResultValue> getNormalizedSubjectResults()
    {
        Map<String, ResultValue> normalizedSubjectResults = new TreeMap<>();
        subjectResults.forEach((k, v) ->
                normalizedSubjectResults.put(k, new ResultValue(v.getTvirtcol()/getAverageTime(), v.getIntensity()/getAverageIntensity()))
        );
        return normalizedSubjectResults;
    }

    public List<Double> getNormalizedSubjectIntensities()
    {
        getNormalizedSubjectResults().values().stream().forEach(i->System.out.println(i.getIntensity()));//for testing purpose
        return getNormalizedSubjectResults().values().stream().map(ResultValue::getIntensity).collect(Collectors.toList());
    }

    public List<Double> getNormalizedSubjectTvirtcol()
    {
        return getNormalizedSubjectResults().values().stream().map(ResultValue::getTvirtcol).collect(Collectors.toList());
    }
}
