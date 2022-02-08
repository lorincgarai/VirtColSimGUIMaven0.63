package com.garailorinc.virtcolsim;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Simulate extends SwingWorker
{

    private double[][] gamutPoint;
    private ColorChangePerception[] virtcolresults;
    private int[][] variation;
    private double x1, x2, y1, y2, p0LInit, p0MInit, p0SInit, tauLInit, tauMInit, tauSInit, equilTime, dt, Enull, Eszorzo, D, deltacMin;
    private int numVariations, gamutpointnumber1, gamutpointnumber2;
    private final GUIForm guiFormReference;
    private boolean isTxtChecked, isExcelChecked, isMatrix, isCorrelation;
    private DecimalFormat intensityResultFormat;
    private DecimalFormat timeResultFormat;
    private List<Double> xcoordinates = new ArrayList<>();
    private List<Double> ycoordinates = new ArrayList<>();
    private List<Double>peakPerceptionReturnx = new ArrayList<>();
    private List<Double> peakPerceptionReturny = new ArrayList<>();
    private List<Double> newPerceptionReturnx = new ArrayList<>();
    private List<Double> newPerceptionReturny = new ArrayList<>();

    Simulate(GUIForm guiForm)
    {
        this.guiFormReference = guiForm;

        intensityResultFormat = new DecimalFormat("0.000000");
        timeResultFormat = new DecimalFormat("0.0");

    }

    public void sim()
    {
        String pathFileName = guiFormReference.pathTextField1.getText();
        guiFormReference.outPutTextArea.append("\n" + pathFileName + "\n");
        isTxtChecked = guiFormReference.createTxtFileCheckBox.isSelected();
        isExcelChecked = guiFormReference.createExcelFileCheckBox.isSelected();
        isMatrix = guiFormReference.matrixExcelRadioButton.isSelected();
        isCorrelation = guiFormReference.calculateCorrelationCheckBox.isSelected();

        ExcelResults excelResults = new ExcelResults(pathFileName, isMatrix, isCorrelation);
        ResultsTxtFile resultsTxtFile = new ResultsTxtFile();
        defineGamutPoints();


        for (int p = 0; p <= 12; p++)
        {
            System.out.print("Gamut point " + p + " : " + gamutPoint[p][0] + " | ");
            System.out.println(gamutPoint[p][1]);
            guiFormReference.outPutTextArea.append("\nGamut point " + p + " : " + Double.toString(gamutPoint[p][0]) + " | ");
            guiFormReference.outPutTextArea.append(Double.toString(gamutPoint[p][1]));
        }
        System.out.println("\n\n");
        guiFormReference.outPutTextArea.append("\n");

        if (isMatrix)
        {
            int variationNumber = defineMatrixVariations();
            virtcolresults = new ColorChangePerception[variationNumber + 1];
        } else
        {
            define36Variations();
            virtcolresults = new ColorChangePerception[37];
        }

        for (int i = 0; i < virtcolresults.length; i++)
            virtcolresults[i] = new ColorChangePerception(1, 1, 1, 1);

        for (ColorChangePerception colorChangePerception : virtcolresults)//solved sticking point
        {
            System.out.println(colorChangePerception.getFirstColor() + " " + colorChangePerception.getSecondColor() + " " + colorChangePerception.getIntensity() + " " + colorChangePerception.getTvirtcol());
        }

        parseInitVars();

        ColorPerception colorPerception = new ColorPerception(this.guiFormReference.outPutTextArea, tauLInit, tauMInit, tauSInit, Enull, Eszorzo, D,
                xcoordinates, ycoordinates, peakPerceptionReturnx, peakPerceptionReturny, newPerceptionReturnx, newPerceptionReturny); // referenced between ColorPerception and GraphyPanel: xcoordinates, ycoordinates, peakPerceptionReturnx, peakPerceptionReturny, newPerceptionReturnx, newPerceptionReturny

        for (int i = 1; i <= numVariations; i++)
        {
            gamutpointnumber1 = variation[i][0];
            gamutpointnumber2 = variation[i][1];

            x1 = gamutPoint[gamutpointnumber1][0];
            y1 = gamutPoint[gamutpointnumber1][1];
            x2 = gamutPoint[gamutpointnumber2][0];
            y2 = gamutPoint[gamutpointnumber2][1];

            colorPerception.equilibrateToFirstGamutPoint(x1, y1, p0LInit, p0MInit, p0SInit, equilTime);
            colorPerception.SecondGamutPointEquilibriumPerception(x2, y2, p0LInit, p0MInit, p0SInit, equilTime);
            colorPerception.IterateSecondGamutPoint(x2, y2, dt, deltacMin);

            virtcolresults[i] = new ColorChangePerception(variation[i][0], variation[i][1], colorPerception.deltacmax, colorPerception.tvirtcol);

            System.out.println(variation[i][0] + " --> " + variation[i][1] + " intensity: " + colorPerception.deltacmax + " | time: " + colorPerception.tvirtcol);
            guiFormReference.outPutTextArea.append("\n\n" + variation[i][0] + " --> " + variation[i][1] + " intensity: " + colorPerception.deltacmax + " | time: " + colorPerception.tvirtcol + "\n");
        }

        if (isExcelChecked) guiFormReference.outPutTextArea.append("\nCreating Excel\n");
        guiFormReference.outPutTextArea.setCaretPosition(guiFormReference.outPutTextArea.getDocument().getLength());
        if (isExcelChecked) excelResults.createExcelSheetAndHeader();
        if (isTxtChecked) resultsTxtFile.createFile(pathFileName);

        System.out.println("\n***************\n Gamut point 1 | Gamut point 2 | Intensity of virtcol p. | Time of virtcol p.");
        guiFormReference.outPutTextArea.append("\n***************\n Gamut point 1 | Gamut point 2 | Intensity of virtcol p. | Time of virtcol p.");


        for (int i = 1; i <= numVariations; i++)
        { //Eredmények összesítése
            System.out.print("\n" + virtcolresults[i].getFirstColor() + " --> ");
            System.out.print(virtcolresults[i].getSecondColor() + "   | ");
            System.out.print(virtcolresults[i].getIntensity() + " | ");
            System.out.print(virtcolresults[i].getTvirtcol() + " \n");
            guiFormReference.outPutTextArea.append("\n" + virtcolresults[i].getFirstColor() + " --> ");
            guiFormReference.outPutTextArea.append(virtcolresults[i].getSecondColor() + "  | ");
            guiFormReference.outPutTextArea.append(intensityResultFormat.format(virtcolresults[i].getIntensity()) + " | ");
            guiFormReference.outPutTextArea.append(timeResultFormat.format(virtcolresults[i].getTvirtcol())+ "\n");
            guiFormReference.outPutTextArea.setCaretPosition(guiFormReference.outPutTextArea.getDocument().getLength());

            if (isExcelChecked) excelResults.addExcelRows(virtcolresults[i]);
            if (isTxtChecked)
                resultsTxtFile.writeToFile(Integer.toString(variation[i][0]) + "," + Integer.toString(variation[i][1]) + "," + Double.toString(virtcolresults[i].getIntensity()) + "," + Double.toString(virtcolresults[i].getTvirtcol()));
        }
        guiFormReference.outPutTextArea.append("\nGraph length: " + xcoordinates.size() + "\n");
        if (isCorrelation)
        {
            Ranking ranking = new Ranking(guiFormReference, virtcolresults);
            ranking.compareSimulatedAndTestResultRankings();
            if (isExcelChecked) excelResults.addCorrelationResults(ranking);
            if (isTxtChecked)
                resultsTxtFile.writeToFile(ranking.getCorrelationText());
        }

        if (isExcelChecked) guiFormReference.outPutTextArea.append("\nAdding Excel footer\n");
        if (isExcelChecked)
            excelResults.addInitialParam(p0LInit, p0MInit, p0SInit, tauLInit, tauMInit, tauSInit, equilTime, dt, Enull, Eszorzo, D, deltacMin, gamutPoint);
        if (isTxtChecked) resultsTxtFile.closeFile();
        if (isTxtChecked) guiFormReference.outPutTextArea.append("\nTxt closed\n");
        if (isExcelChecked) excelResults.writeAndCloseExcelFile();
        if (isExcelChecked) guiFormReference.outPutTextArea.append("\nExcel closed\n");
        guiFormReference.outPutTextArea.setCaretPosition(guiFormReference.outPutTextArea.getDocument().getLength());
        System.out.println("\n...End...\n ");

        double[][] uvArray = new double[3][2];
        uvArray[0] = fromxyToUv(gamutPoint[1]);
        uvArray[1] = fromxyToUv(gamutPoint[5]);
        uvArray[2] = fromxyToUv(gamutPoint[9]);
        GraphPanel graphPanel = new GraphPanel(xcoordinates, ycoordinates,
                peakPerceptionReturnx, peakPerceptionReturny, newPerceptionReturnx, newPerceptionReturny, uvArray);
        graphPanel.graph();


    }

    @Override
    protected Object doInBackground() throws Exception
    {
        sim();
        return true;
    }

    private double parseTextField(JTextField textField)
    {
        double doubleResult = 0;
        try
        {
            doubleResult = Double.parseDouble(textField.getText());

        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            // handle the error
        }
        return doubleResult;
    }

    private double[] fromxyToUv(double[] xyGamut)
    {
        double[] uvArray = new double[3];
        uvArray[0] = 4 * xyGamut[0] / (12 * xyGamut[1] - 2 * xyGamut[0] + 3);
        uvArray[1] = 6 * xyGamut[1] / (12 * xyGamut[1] - 2 * xyGamut[0] + 3);
        return uvArray;
    }

    private void parseInitVars()
    {
        p0LInit = parseTextField(guiFormReference.p0LTextField);
        p0MInit = parseTextField(guiFormReference.p0MTextField);
        p0SInit = parseTextField(guiFormReference.p0StextField1);
        tauLInit = parseTextField(guiFormReference.tauRedTextField);
        tauMInit = parseTextField(guiFormReference.tauGreenTextField);
        tauSInit = parseTextField(guiFormReference.tauBlueTextField1);
        equilTime = parseTextField(guiFormReference.equilTimeTextField);
        dt = parseTextField(guiFormReference.dtTextField1);
        Eszorzo = parseTextField(guiFormReference.eszorzoTextField);
        Enull = parseTextField(guiFormReference.enulltextField1);
        D = parseTextField(guiFormReference.dTextField1);
        deltacMin = parseTextField(guiFormReference.deltacMinTextField);
    }

    private void defineGamutPoints()
    {
        gamutPoint = new double[14][2]; //2. dimenzió 0.: X, 1.: Y koordináták
        gamutPoint[1][0] = parseTextField(guiFormReference.gamutPointBluexTextField);
        gamutPoint[1][1] = parseTextField(guiFormReference.gamutPointBlueyTextField);
        gamutPoint[5][0] = parseTextField(guiFormReference.gamutPointRedxTextField);
        gamutPoint[5][1] = parseTextField(guiFormReference.gamutPointRedyTextField);
        gamutPoint[9][0] = parseTextField(guiFormReference.gamutPointGreenxTextField);
        gamutPoint[9][1] = parseTextField(guiFormReference.gamutPointGreenyTextField);

        gamutPoint[0][0] = (gamutPoint[1][0] + gamutPoint[5][0] + gamutPoint[9][0]) / 3; //white point (grey)
        gamutPoint[0][1] = (gamutPoint[1][1] + gamutPoint[5][1] + gamutPoint[9][1]) / 3; //white point (grey)
        gamutPoint[2][0] = gamutPoint[1][0] + (gamutPoint[5][0] - gamutPoint[1][0]) / 4;
        gamutPoint[2][1] = gamutPoint[1][1] + (gamutPoint[5][1] - gamutPoint[1][1]) / 4;
        gamutPoint[3][0] = gamutPoint[1][0] + 2 * (gamutPoint[5][0] - gamutPoint[1][0]) / 4;
        gamutPoint[3][1] = gamutPoint[1][1] + 2 * (gamutPoint[5][1] - gamutPoint[1][1]) / 4;
        gamutPoint[4][0] = gamutPoint[1][0] + 3 * (gamutPoint[5][0] - gamutPoint[1][0]) / 4;
        gamutPoint[4][1] = gamutPoint[1][1] + 3 * (gamutPoint[5][1] - gamutPoint[1][1]) / 4;

        gamutPoint[6][0] = gamutPoint[9][0] + 3 * (gamutPoint[5][0] - gamutPoint[9][0]) / 4;
        gamutPoint[6][1] = gamutPoint[9][1] + 3 * (gamutPoint[5][1] - gamutPoint[9][1]) / 4;
        gamutPoint[7][0] = gamutPoint[9][0] + 2 * (gamutPoint[5][0] - gamutPoint[9][0]) / 4;
        gamutPoint[7][1] = gamutPoint[9][1] + 2 * (gamutPoint[5][1] - gamutPoint[9][1]) / 4;
        gamutPoint[8][0] = gamutPoint[9][0] + (gamutPoint[5][0] - gamutPoint[9][0]) / 4;
        gamutPoint[8][1] = gamutPoint[9][1] + (gamutPoint[5][1] - gamutPoint[9][1]) / 4;

        gamutPoint[10][0] = gamutPoint[1][0] + 3 * (gamutPoint[9][0] - gamutPoint[1][0]) / 4;
        gamutPoint[10][1] = gamutPoint[1][1] + 3 * (gamutPoint[9][1] - gamutPoint[1][1]) / 4;
        gamutPoint[11][0] = gamutPoint[1][0] + 2 * (gamutPoint[9][0] - gamutPoint[1][0]) / 4;
        gamutPoint[11][1] = gamutPoint[1][1] + 2 * (gamutPoint[9][1] - gamutPoint[1][1]) / 4;
        gamutPoint[12][0] = gamutPoint[1][0] + (gamutPoint[9][0] - gamutPoint[1][0]) / 4;
        gamutPoint[12][1] = gamutPoint[1][1] + (gamutPoint[9][1] - gamutPoint[1][1]) / 4;
    }


    private int defineMatrixVariations() //grey added
    {

        int c1Num = 12;
        int c2Num = 12;
        numVariations = (c1Num + 1) * (c2Num); //0 array index is added to gamuts; former: numVariations = c1Num * (c2Num - 1);
        int varArrayLength = numVariations + 1; //+1 is needed for array indexing from 1
        int variationCounter = 0;

        variation = new int[varArrayLength][2];

        for (int i = 0; i <= c1Num; i++)
            for (int j = 0; j <= c2Num; j++)
            {
                if (i != j)
                {
                    variationCounter++;
                    variation[variationCounter][0] = i;
                    variation[variationCounter][1] = j;
                    System.out.println("Variation " + variation[variationCounter][0] + " --> " + variation[variationCounter][1] + "\n");
                    guiFormReference.outPutTextArea.append("Variation " + variation[variationCounter][0] + " --> " + variation[variationCounter][1] + "\n");
                }
            }
        guiFormReference.outPutTextArea.append("No. of variations: " + variationCounter);
        return variationCounter;
    }


    private void define36Variations() //grey added
    {
        numVariations = 36;
        variation = new int[37][2];
        variation[1][0] = 1;
        variation[1][1] = 6;
        variation[2][0] = 1;
        variation[2][1] = 7;
        variation[3][0] = 1;
        variation[3][1] = 8;
        variation[4][0] = 6;
        variation[4][1] = 1;
        variation[5][0] = 7;
        variation[5][1] = 1;
        variation[6][0] = 8;
        variation[6][1] = 1;
        variation[7][0] = 10;
        variation[7][1] = 5;
        variation[8][0] = 11;
        variation[8][1] = 5;
        variation[9][0] = 12;
        variation[9][1] = 5;
        variation[10][0] = 5;
        variation[10][1] = 10;
        variation[11][0] = 5;
        variation[11][1] = 11;
        variation[12][0] = 5;
        variation[12][1] = 12;
        variation[13][0] = 9;
        variation[13][1] = 2;
        variation[14][0] = 9;
        variation[14][1] = 3;
        variation[15][0] = 9;
        variation[15][1] = 4;
        variation[16][0] = 2;
        variation[16][1] = 9;
        variation[17][0] = 3;
        variation[17][1] = 9;
        variation[18][0] = 4;
        variation[18][1] = 9;
        variation[19][0] = 1;
        variation[19][1] = 5;
        variation[20][0] = 5;
        variation[20][1] = 1;
        variation[21][0] = 5;
        variation[21][1] = 9;
        variation[22][0] = 9;
        variation[22][1] = 5;
        variation[23][0] = 9;
        variation[23][1] = 1;
        variation[24][0] = 1;
        variation[24][1] = 9;
        variation[25][0] = 1;
        variation[25][1] = 0;
        variation[26][0] = 2;
        variation[26][1] = 0;
        variation[27][0] = 3;
        variation[27][1] = 0;
        variation[28][0] = 4;
        variation[28][1] = 0;
        variation[29][0] = 5;
        variation[29][1] = 0;
        variation[30][0] = 6;
        variation[30][1] = 0;
        variation[31][0] = 7;
        variation[31][1] = 0;
        variation[32][0] = 8;
        variation[32][1] = 0;
        variation[33][0] = 9;
        variation[33][1] = 0;
        variation[34][0] = 10;
        variation[34][1] = 0;
        variation[35][0] = 11;
        variation[35][1] = 0;
        variation[36][0] = 12;
        variation[36][1] = 0;
    }
}


