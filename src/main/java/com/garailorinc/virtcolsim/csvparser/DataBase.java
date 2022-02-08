package com.garailorinc.virtcolsim.csvparser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DataBase
{
    private List<Subject> subjects = new ArrayList<>();

    public void sortByAge()
    {
        subjects.sort(Comparator.comparing(subject -> subject.getAge()));
    }

    public SplitDatabase splitByAgeMedian()
    {
        if (isOdd(subjects.size())){throw new RuntimeException("Odd number of csv result files. Please give even number of files");}
        sortByAge();

        List<Subject> lowerList = subjects.stream().filter(subject -> (subjects.indexOf(subject)+1<=subjects.size()/2)).collect(Collectors.toList());
        List<Subject> upperList = subjects.stream().filter(subject -> (subjects.indexOf(subject)+1>subjects.size()/2)).collect(Collectors.toList());

        return new SplitDatabase(upperList, lowerList);
    }

    public List<Subject> getFemaleSubjects()
    {
        return subjects.stream().filter(subject -> (subject.getGender() == Gender.FEMALE)).collect(Collectors.toList());
    }

    public List<Subject> getMaleSubjects()
    {
        return subjects.stream().filter(subject -> (subject.getGender() == Gender. MALE)).collect(Collectors.toList());
    }
    public void addSubject(Subject subject)
    {
        subjects.add(subject);
    }

    private boolean isOdd(int size)
    {
        return size % 2 == 1;
    }

    public int getSubjectNumber()
    {
        return subjects.size();
    }

    public List<Subject>getSubjects()
    {
        return this.subjects;
    }
}
