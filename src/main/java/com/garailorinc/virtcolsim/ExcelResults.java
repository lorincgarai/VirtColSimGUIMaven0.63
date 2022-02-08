package com.garailorinc.virtcolsim;

import com.garailorinc.virtcolsim.csvparser.DataBase;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by garail on 2018. 12. 19..
 */
public class ExcelResults {

    private static String EXCEL_FILENAME;
    private XSSFWorkbook destinationWorkbook;
    private Sheet destinationSheet, matrixDeltacmaxDestinationSheet, matrixtvirtcolDestinationSheet, correlationSheet;
    private Row row, mcrow, mtrow, corrow;
    private int rowNum = 1; private int corrRowNum = 0;
    private boolean isMatrix, isCorrelation;


    ExcelResults(String path, boolean matrix, boolean isCorrelationOpted) {
        EXCEL_FILENAME = path+"\\virtcolsimresult" +generateDateTag()+".xlsx";
System.out.println(EXCEL_FILENAME);
        this.isMatrix=matrix;
        this.isCorrelation = isCorrelationOpted;
    }



    /**
     * Creates Excel table object with one sheet and virtual color perception header.
     */
    void createExcelSheetAndHeader() {
            System.out.println("Creating Excel object");
            destinationWorkbook = new XSSFWorkbook();
            destinationSheet = destinationWorkbook.createSheet("VirtColSim");
            row = destinationSheet.createRow(0);
            row.createCell( 0).setCellValue("1st gamut point");
            row.createCell( 1).setCellValue("2nd gamut point");
            row.createCell( 2).setCellValue("deltacmax");
            row.createCell( 3).setCellValue("t_virtcol");
            rowNum=1;
            if (isMatrix) {
                matrixDeltacmaxDestinationSheet = destinationWorkbook.createSheet("intensityMatrix");
                matrixtvirtcolDestinationSheet = destinationWorkbook.createSheet("t_virtcolMatrix");
                mcrow= matrixDeltacmaxDestinationSheet.createRow(0);
                mtrow= matrixtvirtcolDestinationSheet.createRow(0);
                mcrow.createCell(0).setCellValue("(deltac)max");
                mtrow.createCell(0).setCellValue("t_virtcol");
                mcrow= matrixDeltacmaxDestinationSheet.createRow(1);
                mtrow= matrixtvirtcolDestinationSheet.createRow(1);
                for (int i=0;i<=12;i++){
                    mcrow.createCell(i+1).setCellValue(i); //0: grey added
                    mtrow.createCell(i+1).setCellValue(i);
                }
                for (int i=0;i<=12;i++){ // add number to A1 column
                    mcrow= matrixDeltacmaxDestinationSheet.createRow(i+2);
                    mtrow= matrixtvirtcolDestinationSheet.createRow(i+2);
                    mcrow.createCell(0).setCellValue(i);
                    mtrow.createCell(0).setCellValue(i);
                }
            }
            if (isCorrelation){
                correlationSheet = destinationWorkbook.createSheet("correlation");
            }
        }

    /**
     Adds the calculation resultset to Excel table by streaming. Excel table must be created by @createExcelSheetAndHeader method.
     * @param colorChangePerception
     */
    void addExcelRows(ColorChangePerception colorChangePerception) {
        Row row;
        row = destinationSheet.createRow(rowNum++);
        row.createCell(0).setCellValue(colorChangePerception.getFirstColor());
        row.createCell(1).setCellValue(colorChangePerception.getSecondColor());
        row.createCell(2).setCellValue(colorChangePerception.getIntensity());
        row.createCell(3).setCellValue(colorChangePerception.getTvirtcol());
        if (isMatrix){
            row=matrixDeltacmaxDestinationSheet.getRow(colorChangePerception.getFirstColor() + 2); //refers to row created by initialization
            row.createCell(colorChangePerception.getSecondColor()+1).setCellValue(colorChangePerception.getIntensity());
            row=matrixtvirtcolDestinationSheet.getRow(colorChangePerception.getFirstColor() + 2);
            row.createCell(colorChangePerception.getSecondColor()+1).setCellValue(colorChangePerception.getTvirtcol());
        }
    }





    /**
     * Saves Excel table filled by addExcelRows method into running directory.
     */
     void writeAndCloseExcelFile() {
        try {
            FileOutputStream outputStream = new FileOutputStream(EXCEL_FILENAME);
            destinationWorkbook.write(outputStream);
            outputStream.close();
            destinationWorkbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Saved");
    }




    String generateDateTag() {
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
        String DateTag = dateFormat.format(date);
        return DateTag;
    }


    public void addInitialParam(double p0LInit, double p0MInit, double p0SInit, double tauLInit, double tauMInit, double tauSInit, double equilTime, double dt, double enull, double eszorzo, double D, double deltacMin, double[][] gamutPoint) {
        Row row; int matrixRowNum;
        rowNum++;
        row = destinationSheet.createRow(rowNum++);
        row.createCell(0).setCellValue("p0L");
        row.createCell(1).setCellValue(p0LInit);
        row.createCell(2).setCellValue("p0M");
        row.createCell(3).setCellValue(p0MInit);
        row.createCell(4).setCellValue("p0S");
        row.createCell(5).setCellValue(p0SInit);
        row = destinationSheet.createRow(rowNum++);
        row.createCell(0).setCellValue("tauL");
        row.createCell(1).setCellValue(tauLInit);
        row.createCell(2).setCellValue("tauM");
        row.createCell(3).setCellValue(tauMInit);
        row.createCell(4).setCellValue("tauS");
        row.createCell(5).setCellValue(tauSInit);
        row = destinationSheet.createRow(rowNum++);
        row.createCell(0).setCellValue("equilTime");
        row.createCell(1).setCellValue(equilTime);
        row = destinationSheet.createRow(rowNum++);
        row.createCell(0).setCellValue("dt");
        row.createCell(1).setCellValue(dt);
        row = destinationSheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Enull");
        row.createCell(1).setCellValue(enull);
        row = destinationSheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Eszorzo");
        row.createCell(1).setCellValue(eszorzo);
        row = destinationSheet.createRow(rowNum++);
        row.createCell(0).setCellValue("D");
        row.createCell(1).setCellValue(D);
        row = destinationSheet.createRow(rowNum++);
        row.createCell(0).setCellValue("delta c min (t_virtcol)");
        row.createCell(1).setCellValue(deltacMin);
       destinationSheet.createRow(rowNum++);
        row = destinationSheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Colour code");
        row.createCell(1).setCellValue("x");
        row.createCell(2).setCellValue("y");
        for (int colourNumber = 0; colourNumber <= 12; colourNumber++)
        {
            row = destinationSheet.createRow(rowNum++);
            row.createCell(0).setCellValue(colourNumber);
            row.createCell(1).setCellValue(gamutPoint[colourNumber][0]);
            row.createCell(2).setCellValue(gamutPoint[colourNumber][1]);
        }


        if (isMatrix) {
            matrixRowNum=19;
            mcrow = matrixDeltacmaxDestinationSheet.createRow(matrixRowNum);
            mtrow = matrixtvirtcolDestinationSheet.createRow(matrixRowNum++);
            mcrow.createCell(0).setCellValue("p0L");
            mtrow.createCell(0).setCellValue("p0L");
            mcrow.createCell(1).setCellValue(p0LInit);
            mtrow.createCell(1).setCellValue(p0LInit);
            mcrow.createCell(2).setCellValue("p0M");
            mtrow.createCell(2).setCellValue("p0M");
            mcrow.createCell(3).setCellValue(p0MInit);
            mtrow.createCell(3).setCellValue(p0MInit);
            mcrow.createCell(4).setCellValue("p0S");
            mtrow.createCell(4).setCellValue("p0S");
            mcrow.createCell(5).setCellValue(p0SInit);
            mtrow.createCell(5).setCellValue(p0SInit);
            mcrow = matrixDeltacmaxDestinationSheet.createRow(matrixRowNum);
            mtrow = matrixtvirtcolDestinationSheet.createRow(matrixRowNum++);
            mcrow.createCell(0).setCellValue("tauL");
            mcrow.createCell(1).setCellValue(tauLInit);
            mcrow.createCell(2).setCellValue("tauM");
            mcrow.createCell(3).setCellValue(tauMInit);
            mcrow.createCell(4).setCellValue("tauS");
            mcrow.createCell(5).setCellValue(tauSInit);
            mtrow.createCell(0).setCellValue("tauL");
            mtrow.createCell(1).setCellValue(tauLInit);
            mtrow.createCell(2).setCellValue("tauM");
            mtrow.createCell(3).setCellValue(tauMInit);
            mtrow.createCell(4).setCellValue("tauS");
            mtrow.createCell(5).setCellValue(tauSInit);
            mcrow = matrixDeltacmaxDestinationSheet.createRow(matrixRowNum);
            mtrow = matrixtvirtcolDestinationSheet.createRow(matrixRowNum++);
            mcrow.createCell(0).setCellValue("equilTime");
            mcrow.createCell(1).setCellValue(equilTime);
            mtrow.createCell(0).setCellValue("equilTime");
            mtrow.createCell(1).setCellValue(equilTime);
            mcrow = matrixDeltacmaxDestinationSheet.createRow(matrixRowNum);
            mtrow = matrixtvirtcolDestinationSheet.createRow(matrixRowNum++);
            mcrow.createCell(0).setCellValue("dt");
            mcrow.createCell(1).setCellValue(dt);
            mtrow.createCell(0).setCellValue("dt");
            mtrow.createCell(1).setCellValue(dt);
            mcrow = matrixDeltacmaxDestinationSheet.createRow(matrixRowNum);
            mtrow = matrixtvirtcolDestinationSheet.createRow(matrixRowNum++);
            mcrow.createCell(0).setCellValue("Enull");
            mcrow.createCell(1).setCellValue(enull);
            mtrow.createCell(0).setCellValue("Enull");
            mtrow.createCell(1).setCellValue(enull);
            mcrow = matrixDeltacmaxDestinationSheet.createRow(matrixRowNum);
            mtrow = matrixtvirtcolDestinationSheet.createRow(matrixRowNum++);
            mcrow.createCell(0).setCellValue("Eszorzo");
            mcrow.createCell(1).setCellValue(eszorzo);
            mtrow.createCell(0).setCellValue("Eszorzo");
            mtrow.createCell(1).setCellValue(eszorzo);
            mcrow = matrixDeltacmaxDestinationSheet.createRow(matrixRowNum);
            mtrow = matrixtvirtcolDestinationSheet.createRow(matrixRowNum++);
            mcrow.createCell(0).setCellValue("D");
            mcrow.createCell(1).setCellValue(D);
            mtrow.createCell(0).setCellValue("D");
            mtrow.createCell(1).setCellValue(D);
            mcrow = matrixDeltacmaxDestinationSheet.createRow(matrixRowNum);
            mtrow = matrixtvirtcolDestinationSheet.createRow(matrixRowNum++);
            mcrow.createCell(0).setCellValue("delta c min (t_virtcol)");
            mcrow.createCell(1).setCellValue(deltacMin);
            mtrow.createCell(0).setCellValue("delta c min (t_virtcol)");
            mtrow.createCell(1).setCellValue(deltacMin);
            mcrow = matrixDeltacmaxDestinationSheet.createRow(matrixRowNum);
            mtrow = matrixtvirtcolDestinationSheet.createRow(matrixRowNum++);
            mcrow = matrixDeltacmaxDestinationSheet.createRow(matrixRowNum);
            mtrow = matrixtvirtcolDestinationSheet.createRow(matrixRowNum++);
            mcrow.createCell(0).setCellValue("Colour code");
            mtrow.createCell(0).setCellValue("Colour code");
            mcrow.createCell(1).setCellValue("x");
            mtrow.createCell(1).setCellValue("x");
            mcrow.createCell(2).setCellValue("y");
            mtrow.createCell(2).setCellValue("y");
            for (int colourNumber = 0; colourNumber <= 12; colourNumber++)
            {
                mcrow = matrixDeltacmaxDestinationSheet.createRow(matrixRowNum);
                mcrow.createCell(0).setCellValue(colourNumber);
                mcrow.createCell(1).setCellValue(gamutPoint[colourNumber][0]);
                mcrow.createCell(2).setCellValue(gamutPoint[colourNumber][1]);

                mtrow = matrixtvirtcolDestinationSheet.createRow(matrixRowNum++);
                mtrow .createCell(0).setCellValue(colourNumber);
                mtrow .createCell(1).setCellValue(gamutPoint[colourNumber][0]);
                mtrow .createCell(2).setCellValue(gamutPoint[colourNumber][1]);
            }
        }
    }

    public void addCorrelationResults(Ranking ranking)
    {
        corrow = correlationSheet.createRow(corrRowNum++);
        corrow.createCell(0).setCellValue("Test subjects");
        corrRowNum++;

        addSubjectResults(ranking.getDataBase());

        correlationSheet.createRow(corrRowNum++);
        corrow = correlationSheet.createRow(corrRowNum++);
        corrow.createCell(0).setCellValue("Model ranking by p");
        addCorrToSheet(ranking.getModelRankingp());
        correlationSheet.createRow(corrRowNum++);

        corrow = correlationSheet.createRow(corrRowNum++);
        corrow.createCell(0).setCellValue("Model ranking by t");
        addCorrToSheet(ranking.getModelRankingt());
        correlationSheet.createRow(corrRowNum++);

        corrow = correlationSheet.createRow(corrRowNum++);
        corrow.createCell(0).setCellValue("Measured ranking by p");
        addCorrToSheet(ranking.getMeasuredRankingByp());
        correlationSheet.createRow(corrRowNum++);

        corrow = correlationSheet.createRow(corrRowNum++);
        corrow.createCell(0).setCellValue("Measured ranking by t");
        addCorrToSheet(ranking.getMeasuredRankingByt());
        correlationSheet.createRow(corrRowNum++);

        corrow = correlationSheet.createRow(corrRowNum++);
        corrow.createCell(0).setCellValue("Refined measured ranking by p");
        addCorrToSheet(ranking.getRefinedMeasuredRankingByp());
        correlationSheet.createRow(corrRowNum++);

        corrow = correlationSheet.createRow(corrRowNum++);
        corrow.createCell(0).setCellValue("Refined measured ranking by t");
        addCorrToSheet(ranking.getRefinedMeasuredRankingByt());
        correlationSheet.createRow(corrRowNum++);

        corrow = correlationSheet.createRow(corrRowNum++);
        corrow.createCell(0).setCellValue("Correlation by p");
        corrow.createCell(1).setCellValue(ranking.countCorrelationByP());

        corrow = correlationSheet.createRow(corrRowNum++);
        corrow.createCell(0).setCellValue("Correlation by t");
        corrow.createCell(1).setCellValue(ranking.countCorrelationByT());
    }

    private void addSubjectResults(DataBase dataBase)
    {
        corrow = correlationSheet.createRow(corrRowNum++);
        dataBase.getSubjects().forEach(subject ->
                {
                    correlationSheet.createRow(corrRowNum++);
                    corrow = correlationSheet.createRow(corrRowNum++);
                    corrow.createCell(0).setCellValue(subject.getName());
                    corrow = correlationSheet.createRow(corrRowNum++);
                    corrow.createCell(0).setCellValue("Color change");
                    corrow.createCell(1).setCellValue("t");
                    corrow.createCell(2).setCellValue("p");
                    subject.getSubjectResults().entrySet().forEach(entry -> {
                        corrow = correlationSheet.createRow(corrRowNum++);
                        corrow.createCell(0).setCellValue(entry.getKey());
                        corrow.createCell(1).setCellValue(entry.getValue().getTvirtcol());
                        corrow.createCell(2).setCellValue(entry.getValue().getIntensity());
                    });
                }
        );
    }

    private void addCorrToSheet(List<RankingPerception> rankingList)
    {
        corrow = correlationSheet.createRow(corrRowNum++);
        corrow.createCell(0).setCellValue("Color 1");
        corrow.createCell(1).setCellValue("Color 2");
        corrow.createCell(2).setCellValue("Average ranking");
        corrow.createCell(3).setCellValue("Median ranking");
        for (RankingPerception rankingPerception:rankingList)
        {
            corrow = correlationSheet.createRow(corrRowNum++);
            corrow.createCell(0).setCellValue(rankingPerception.getFirstGamutPoint());
            corrow.createCell(1).setCellValue(rankingPerception.getSecondGamutPoint());
            corrow.createCell(2).setCellValue(rankingPerception.getAverage());
            corrow.createCell(3).setCellValue(rankingPerception.getMedian());
        }
    }
}
