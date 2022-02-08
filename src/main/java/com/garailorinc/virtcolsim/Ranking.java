package com.garailorinc.virtcolsim;

import com.garailorinc.virtcolsim.csvparser.DataBase;
import com.garailorinc.virtcolsim.csvparser.OwnStatistics;
import com.garailorinc.virtcolsim.csvparser.Subject;
import com.garailorinc.virtcolsim.csvparser.TestDataCsvProcessor;
import org.apache.poi.ss.formula.functions.Rank;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.reverseArray;

public class Ranking {
    private final ColorChangePerception[] virtcolresults;
    private ColorChangePerception[] simulatedResultSetByP, simulatedResultSetByT;
    private List<RankingPerception> measuredRankingByp, measuredRankingByt, modelRankingp, modelRankingt, refinedMeasuredRankingByp, refinedMeasuredRankingByt;
    private GUIForm guiForm;
    private Double rankno;
    private StringBuilder correlationText;
    private DataBase dataBase;


    public Ranking(GUIForm guiForm, ColorChangePerception[] virtcolresults) {
        this.guiForm = guiForm;
        this.virtcolresults = virtcolresults;
        measuredRankingByp = new ArrayList<RankingPerception>();
        measuredRankingByt = new ArrayList<RankingPerception>();
        modelRankingp = new ArrayList<RankingPerception>();
        modelRankingt = new ArrayList<RankingPerception>();
        correlationText = new StringBuilder();
    }

    public void compareSimulatedAndTestResultRankings()
    {
        addMeasuredRanking();
        this.simulatedResultSetByP = new ColorChangePerception[virtcolresults.length];
        this.simulatedResultSetByT = new ColorChangePerception[virtcolresults.length];
        System.arraycopy(virtcolresults, 0, simulatedResultSetByP, 0, virtcolresults.length);
        System.arraycopy(virtcolresults, 0, simulatedResultSetByT, 0, virtcolresults.length);
        sortSimResultsByPDescending(simulatedResultSetByP);
        sortSimResultsByTDescending(simulatedResultSetByT);
        addSelectedSimResultsToModelRankingByP(simulatedResultSetByP);
        addSelectedSimResultsToModelRankingByT(simulatedResultSetByT);
        sortTestResultRanksByMedian(measuredRankingByp);
        sortTestResultRanksByMedian(measuredRankingByt);
        refinedMeasuredRankingByp = refine(measuredRankingByp);
        refinedMeasuredRankingByt = refine(measuredRankingByt);
        printRankingsOfTestResultsAndSelectedModel();
    }

    private List<RankingPerception> refine(List<RankingPerception> measuredRankingToRefine)
    {
        Double i = 0.0;
        List<RankingPerception> refinedRankings = new ArrayList<>();
        for (RankingPerception rankingPerception : measuredRankingToRefine)
        {
            i++;
            refinedRankings.add(new RankingPerception(rankingPerception.getFirstGamutPoint(), rankingPerception.getSecondGamutPoint(), i, i));
        }
        return refinedRankings;
    }

    private void addMeasuredRanking()
    {
        dataBase = processDataCsvs();
        dataBase.getSubjects().forEach(subject ->
               {
        correlationText.append("\n"+ subject.getName()+ "\ncolor change | tvirtcol | p\n");
                   subject.getSubjectResults().entrySet().forEach(entry -> {
                       correlationText.append(entry.getKey() + "|"+ entry.getValue().getTvirtcol() + "|" + entry.getValue().getIntensity() + "\n");}
                   );
               }
        );
        correlationText.append("\n\n");

        correlationText.append("\nraw rankings by intensity\n");
        getIntensityRankingFromTestResults(dataBase).entrySet().forEach(entry->correlationText.append(entry.getKey() + " | " + entry.getValue() + "\n"));


        getIntensityRankingFromTestResults(dataBase).forEach((key, value) -> {
            String[] keysplit = key.split("->");
            int firstGamutPoint = Integer.parseInt(keysplit[0]);
            int secondGamutPoint = Integer.parseInt(keysplit[1]);
            measuredRankingByp.add(new RankingPerception(firstGamutPoint, secondGamutPoint, value.stream().collect(Collectors.averagingDouble(num -> num)), OwnStatistics.median(value)));
        });

        correlationText.append("\nraw rankings by time\n");
        getTvirtcolRankingFromTestResults(dataBase).entrySet().forEach(entry->correlationText.append(entry.getKey() + " | " + entry.getValue() + "\n"));

        getTvirtcolRankingFromTestResults(dataBase).forEach((key, value) -> {
            String[] keysplit = key.split("->");
            int firstGamutPoint = Integer.parseInt(keysplit[0]);
            int secondGamutPoint = Integer.parseInt(keysplit[1]);
            measuredRankingByt.add(new RankingPerception(firstGamutPoint, secondGamutPoint, value.stream().collect(Collectors.averagingDouble(num -> num)), OwnStatistics.median(value)));
        });
    }

    private DataBase processDataCsvs()
    {
        TestDataCsvProcessor testDataCsvProcessor = new TestDataCsvProcessor();
        try {
            testDataCsvProcessor.parseCsvsIntoDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testDataCsvProcessor.getDatabase();
    }


    private Map<String, List<Double>> getIntensityRankingFromTestResults(DataBase database)
    {
        Map<String, List<Double>> rankingbyIntensityMap = new TreeMap<String, List<Double>>();
        database.getSubjects().get(0).getSubjectResults().keySet().forEach(key -> rankingbyIntensityMap.put(key, new ArrayList<>())); // initialize ranking map with intensities
        for (Subject subject: database.getSubjects())
        {
            Map<String, Double> resultsOfIntensity = subject.getSubjectResults().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> (entry.getValue().getIntensity())));
            rankno = 0.0;
            resultsOfIntensity.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).forEach(i ->
            {
                rankno++;
                rankingbyIntensityMap.get(i.getKey()).add(rankno);
            });
        }
        rankingbyIntensityMap.entrySet().forEach(entry -> entry.getValue().sort(Comparator.comparing(Double::doubleValue)));
        return rankingbyIntensityMap;
    }

    private Map<String, List<Double>> getTvirtcolRankingFromTestResults(DataBase database)
    {
        Map<String, List<Double>> rankingbyTvirtcolMap = new TreeMap<String, List<Double>>();
        database.getSubjects().get(0).getSubjectResults().keySet().forEach(key -> rankingbyTvirtcolMap.put(key, new ArrayList<>())); // initialize ranking map with intensities
        for (Subject subject: database.getSubjects())
        {
            Map<String, Double> resultsOfTvirtcol = subject.getSubjectResults().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> (entry.getValue().getTvirtcol())));
            rankno = 0.0;
            resultsOfTvirtcol.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).forEach(i ->
            {
                rankno++;
                rankingbyTvirtcolMap.get(i.getKey()).add(rankno);
            });
        }
        rankingbyTvirtcolMap.entrySet().forEach(entry -> entry.getValue().sort(Comparator.comparing(Double::doubleValue)));
        return rankingbyTvirtcolMap;
    }

    public double countCorrelationByP() {
        int yes = 0;
        int no = 0;
        for (RankingPerception rankingPerception : modelRankingp) {
            RankingPerception measuredRank = findColorChangeInMeasured(refinedMeasuredRankingByp, rankingPerception.getFirstGamutPoint(), rankingPerception.getSecondGamutPoint());
            if (rankstatus(rankingPerception.getMedian(), refinedMeasuredRankingByp.size()) == rankstatus(measuredRank.getMedian(), refinedMeasuredRankingByp.size())) {
                yes++;
            } else
                no++;
        }
        return yes / (double)(yes + no);
    }

    public double countCorrelationByT() {
        int yes = 0;
        int no = 0;
        for (RankingPerception rankingPerception : modelRankingt) {
            RankingPerception measuredRank = findColorChangeInMeasured(refinedMeasuredRankingByt, rankingPerception.getFirstGamutPoint(), rankingPerception.getSecondGamutPoint());
            if (rankstatus(rankingPerception.getMedian(), refinedMeasuredRankingByp.size()) == rankstatus(measuredRank.getMedian(), refinedMeasuredRankingByt.size())) {
                yes++;
            } else
                no++;
        }
        return yes / (double)(yes + no);
    }

    private RankStatus rankstatus(double median, int gradeSize) {
        if (gradeSize % 2 == 0) //grade size is paired number
        {
            if (median == gradeSize / 2 || median == gradeSize/2+1)
            {
                return RankStatus.MEDIUM;
            }
            else if (median < gradeSize / 2)
            {
                return RankStatus.HIGH;
            }
            else
            {
                return RankStatus.LOW;
            }
        }
        else //grade size is unpaired number
        {
            if (median == gradeSize/2+1)
            {
                return RankStatus.MEDIUM;
            }
            else if (median < gradeSize / 2)
            {
                return RankStatus.HIGH;
            }
            else
            {
                return RankStatus.LOW;
            }
        }
    }

    private void sortTestResultRanksByMedian(List<RankingPerception> measuredRanking)
    {
        measuredRanking.sort(Comparator.comparing(RankingPerception::getMedian));
    }

    private void addSelectedSimResultsToModelRankingByP(ColorChangePerception[] simulatedp) {
        int i = 1;
        for (ColorChangePerception colorChangePerception : simulatedp) {
            if (doesExistColorChangeInMeasured(measuredRankingByp, colorChangePerception.getFirstColor(), colorChangePerception.getSecondColor())) {
                modelRankingp.add(new RankingPerception(colorChangePerception.getFirstColor(), colorChangePerception.getSecondColor(), i, i));
                i++;
            }
        }
    }

    private void addSelectedSimResultsToModelRankingByT(ColorChangePerception[] simulatedt) {
        int i = 1;
        for (ColorChangePerception colorChangePerception : simulatedt) {
            if (doesExistColorChangeInMeasured(measuredRankingByt, colorChangePerception.getFirstColor(), colorChangePerception.getSecondColor())) {
                modelRankingt.add(new RankingPerception(colorChangePerception.getFirstColor(), colorChangePerception.getSecondColor(), i, i));
                i++;
            }
        }
    }


    private boolean doesExistColorChangeInMeasured(List<RankingPerception> measuredRankingByp, Integer firstColor, Integer secondColor) {
        for (RankingPerception rankingPerception : measuredRankingByp) {
            if (firstColor == rankingPerception.getFirstGamutPoint() && secondColor == rankingPerception.getSecondGamutPoint())
            {
                return true;
            }
        }
        return false;
    }

    private RankingPerception findColorChangeInMeasured(List<RankingPerception> measuredRankingByp, Integer firstColor, Integer secondColor)
    {
        for (RankingPerception rankingPerception : measuredRankingByp)
        {
            if (firstColor == rankingPerception.getFirstGamutPoint() && secondColor == rankingPerception.getSecondGamutPoint())
            {
                return rankingPerception;
            }
        }
        return null;
    }


    public void printRankingsOfTestResultsAndSelectedModel()
    {
        correlationText.append("\nModel ranking by p:\n\n");
        for (RankingPerception rankingPerception:modelRankingp)
        {
            correlationText.append(rankingPerception.getFirstGamutPoint()+" | "+ rankingPerception.getSecondGamutPoint()+" | "+rankingPerception.getAverage() + " | " + rankingPerception.getMedian()+" | "+rankstatus(rankingPerception.getMedian(), refinedMeasuredRankingByp.size())+"\n");
        }

        correlationText.append("\nRefined measured ranking by p:\n\n");
        for (RankingPerception rankingPerception:refinedMeasuredRankingByp)
        {
            correlationText.append(rankingPerception.getFirstGamutPoint()+" | "+ rankingPerception.getSecondGamutPoint()+" | "+rankingPerception.getAverage() + " | " + rankingPerception.getMedian()+" | "+rankstatus(rankingPerception.getMedian(), refinedMeasuredRankingByp.size())+"\n");
        }

        correlationText.append("\nMeasured ranking by p:\n\n");
        for (RankingPerception rankingPerception:measuredRankingByp)
        {
            correlationText.append(rankingPerception.getFirstGamutPoint()+" | "+ rankingPerception.getSecondGamutPoint()+" | "+rankingPerception.getAverage() + " | " + rankingPerception.getMedian()+" | "+rankstatus(rankingPerception.getMedian(), refinedMeasuredRankingByp.size())+"\n");
        }

        correlationText.append("\nModel ranking by t:\n\n");
        for (RankingPerception rankingPerception:modelRankingt)
        {
            correlationText.append(rankingPerception.getFirstGamutPoint()+" | "+ rankingPerception.getSecondGamutPoint()+" | "+rankingPerception.getAverage() + " | " + rankingPerception.getMedian()+" | "+rankstatus(rankingPerception.getMedian(), refinedMeasuredRankingByp.size())+"\n");
        }

        correlationText.append("\nRefined measured ranking by t:\n\n");
        for (RankingPerception rankingPerception:refinedMeasuredRankingByt)
        {
            correlationText.append(rankingPerception.getFirstGamutPoint()+" | "+ rankingPerception.getSecondGamutPoint()+" | "+rankingPerception.getAverage() + " | " + rankingPerception.getMedian()+" | "+rankstatus(rankingPerception.getMedian(), refinedMeasuredRankingByp.size())+"\n");
        }

        correlationText.append("\n"+"Measured ranking by t:\n\n");
        for (RankingPerception rankingPerception:measuredRankingByt)
        {
            correlationText.append(rankingPerception.getFirstGamutPoint()+" | "+ rankingPerception.getSecondGamutPoint()+" | "+rankingPerception.getAverage() + " | " + rankingPerception.getMedian()+" | "+rankstatus(rankingPerception.getMedian(), refinedMeasuredRankingByp.size())+"\n");
        }
        correlationText.append("\nRanking correlation\n\n");
        correlationText.append("P: "+Double.toString(countCorrelationByP())+"\n\n");
        correlationText.append("T: "+Double.toString(countCorrelationByT())+"\n\n");
        guiForm.outPutTextArea.append(correlationText.toString());
        guiForm.outPutTextArea.setCaretPosition(guiForm.outPutTextArea.getDocument().getLength());
    }

    private void sortSimResultsByPDescending(ColorChangePerception[] virtcolresults) {
        Arrays.sort(virtcolresults, Comparator.comparing(ColorChangePerception::getIntensity));
        reverseArray(virtcolresults);
    }

    private void sortSimResultsByTDescending(ColorChangePerception[] virtcolresults) {
        Arrays.sort(virtcolresults, Comparator.comparing(ColorChangePerception::getTvirtcol));
        reverseArray(virtcolresults);
    }

    public List<RankingPerception> getModelRankingp()
    {
        return modelRankingp;
    }

    public List<RankingPerception> getModelRankingt()
    {
        return modelRankingt;
    }

    public List<RankingPerception> getMeasuredRankingByp()
    {
        return measuredRankingByp;
    }

    public List<RankingPerception> getMeasuredRankingByt()
    {
        return measuredRankingByt;
    }

    public List<RankingPerception> getRefinedMeasuredRankingByp()
    {
        return refinedMeasuredRankingByp;
    }

    public List<RankingPerception> getRefinedMeasuredRankingByt()
    {
        return refinedMeasuredRankingByt;
    }

    public String getCorrelationText()
    {
        return correlationText.toString();
    }

    public DataBase getDataBase()
    {
        return dataBase;
    }
}
