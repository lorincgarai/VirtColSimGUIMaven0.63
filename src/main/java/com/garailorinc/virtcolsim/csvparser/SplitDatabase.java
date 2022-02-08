package com.garailorinc.virtcolsim.csvparser;

import java.util.List;
import java.util.stream.Collectors;

public class SplitDatabase
{
    SplitDatabase(List<Subject> upperList, List<Subject> lowerList)
    {
        this.upperList = upperList;
        this.lowerList = lowerList;
    }
    private List<Subject> upperList;
    private List<Subject> lowerList;

    public List<Subject> getLowerAgeList()
    {
        return lowerList;
    }

    public List<Subject> getUpperAgeList()
    {
        return upperList;
    }

    public Integer getSplitAge()
    {
        return upperList.get(0).getAge();
    }

    public List<Subject> getLowerAgeMaleList()
    {
        return lowerList.stream().filter(subject -> subject.getGender() == Gender.MALE).collect(Collectors.toList());
    }

    public List<Subject> getLowerAgeFemaleList()
    {
        return lowerList.stream().filter(subject -> subject.getGender() == Gender.FEMALE).collect(Collectors.toList());
    }

    public List<Subject> getUpperAgeMaleList()
    {
        return upperList.stream().filter(subject -> subject.getGender() == Gender.MALE).collect(Collectors.toList());
    }

    public List<Subject> getUpperAgeFemaleList()
    {
        return upperList.stream().filter(subject -> subject.getGender() == Gender.FEMALE).collect(Collectors.toList());
    }
}
