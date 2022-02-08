package com.garailorinc.virtcolsim.csvparser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OwnStatistics
{
    private static final int NUMBER_OF_COLOR_CHANGES = 9;
    private static final double T_PROBABILITY_MATCH_THRESHOLD = 0.05;

    public static void main(String[] args){
        List<Double> list = new ArrayList();
        for (int i = 1; i<10; i++){list.add((double) i);}
        double stdev_Result = stdev(list);
        System.out.println(stdev(list));
        System.out.println(mean(list));
    }

    public static double stdev(List<Double> list){
        double sum = 0.0;
        double mean;
        double num=0.0;
        double numi;
        double deno = 0.0;

        for (Double i : list) {
            sum+=i;
        }
        mean = sum/list.size();

        for (Double i : list) {
            numi = Math.pow(i - mean, 2);
            num+=numi;
        }

        return Math.sqrt(num/(list.size()-1));
    }

    public static double mean(List<Double> list)
    {
        double sum = 0.0;
        for (Double i : list) {
            sum+=i;
        }
        return sum/list.size();
    }


    public static double median(List<Double> list)
    {
        list.sort(Comparator.comparingDouble(Double::doubleValue));
        double median = list.get(list.size()/2);
        if(list.size()%2 == 0) median = (median + list.get(list.size()/2-1)) / 2;
        return median;
    }

    public static Double tTest(List list1, List list2)
    {
        double mean1 = mean(list1);
        double mean2 = mean(list2);
        double sd1 = stdev(list1);
        double sd2 = stdev(list2);

        // Formula to find t-test
        // of two set of data.
        double t_test = (mean1 - mean2) /
                (double) Math.sqrt((sd1 * sd1) *
                        (list1.size()-1) + (sd2 * sd2) * (list2.size()-2))
                *(double) Math.sqrt(list1.size()*list2.size()*(list1.size()+list2.size()-2)/(double)(list1.size()+list2.size()));
        return t_test;
    }

    public static Double lowerBound(List<Double> values)
    {
        return mean(values)-errordev(values);
    }

    public static Double upperBound(List<Double> values)
    {
        return mean(values)+errordev(values);
    }

    public static Double relstdev(List<Double> values)
    {
        return stdev(values)/mean(values);
    }

    public static Double errordev(List<Double> resultValues)
    {
        return 2*stdev(resultValues)/ Math.sqrt(resultValues.size()*NUMBER_OF_COLOR_CHANGES); // update as per no. of color changes (9)
    }
    public static boolean doesTwoSeriesMatch(double tvalue)
    {
        return (tvalue > T_PROBABILITY_MATCH_THRESHOLD);
    }
    public static String seriesMatchString(double tvalue)
    {
        if (doesTwoSeriesMatch(tvalue))
        {
            return "matching";
        }
        else
            return "different";
    }

    public static Double gettTestThreshold()
    {
        return T_PROBABILITY_MATCH_THRESHOLD;
    }
}
