package com.garailorinc.virtcolsim;

public class RankingPerception
{
    private int firstGamutPoint;
    private int secondGamutPoint;
    private double median;
    private double average;

    RankingPerception(int firstGamutPoint, int secondGamutPoint, double average, double median)
    {
    this.firstGamutPoint = firstGamutPoint;
    this.secondGamutPoint = secondGamutPoint;
    this.average = average;
    this.median = median;
    }

    public int getFirstGamutPoint()
    {
        return firstGamutPoint;
    }

    public int getSecondGamutPoint()
    {
        return secondGamutPoint;
    }

    public double getMedian()
    {
        return median;
    }

    public double getAverage()
    {
        return average;
    }
}
