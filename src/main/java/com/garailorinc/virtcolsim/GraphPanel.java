package com.garailorinc.virtcolsim;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lorinc
 * graph points are imported via constructor arguments
 * credits to Rodrigo as author of the basic GraphPanel API
 */
public class GraphPanel extends JPanel {

    private int width = 1024;
    private int height = 1024;
    private int padding = 25;
    private int labelPadding = 25;
    private Color pointColor = new Color(0, 200, 10, 238);
    private Color peakPointColor = new Color(255,0,0,255);
    private Color finalPerceptionPointColor = new Color(0,0,255,255);
    private Color gridColor = new Color(200, 200, 200, 200);
    private Color curveColor = new Color(27, 49, 200, 200);
    private Color gamutColor = new Color(1, 1, 3, 254);

    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 2;
    private int highlightPointWidth = 4;
    private int numberXDivisions = 7;
    private int numberYDivisions = 7;
    private List<Double> xActualPerception;
    private List<Double> yActualPerception;
    private List<Double> finalPerceptionx, finalPerceptiony, peakPerceptionx, peakPerceptiony;
    private List<Double> xchromCurve;
    private List<Double> ychromCurve;
    private List<Point> dataPointsToDraw, chromCurvePoints, gamutPoints;
    private double xScale, yScale, xMin=0, xMax=0.7, yMin=0, yMax=0.7;
    private double[][] gamutUV;



    public GraphPanel(List<Double> xsimPoints, List<Double> ySimPoints, List<Double> peakPerceptionReturnx, List peakPerceptionReturny, List<Double> finalPerceptionx, List<Double> finalPerceptiony, double[][] gamutUV) {
        this.gamutUV=new double[4][2];
        this.gamutUV=gamutUV;
        this.xActualPerception = xsimPoints;
        this.yActualPerception = ySimPoints;
        this.finalPerceptionx = finalPerceptionx;
        this.finalPerceptiony = finalPerceptiony;
        this.peakPerceptionx = peakPerceptionReturnx;
        this.peakPerceptiony = peakPerceptionReturny;

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        setChromCurve();
        setxyScale();
        setDiagramBackground(g2);
        addActualPerceptionGraphPoints(g2);
        addPeakPerceptionGraphPoints(g2);
        addFinalPerceptionGraphPoints(g2);
        addChromCurve(g2);
        addGamut(g2);


    }

    private void setDiagramBackground(Graphics2D g2) {
        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i <= numberYDivisions; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
            if (yActualPerception.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((yMin + (yMax - yMin) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // and hatch/grid for x axis
        for (int i = 0; i <= numberXDivisions; i++) {
            if (xActualPerception.size() > 1) {
                int x0 = getX() + ((i * (getWidth() - padding * 2 - labelPadding)) / numberXDivisions + padding + labelPadding);
                int x1 = x0;
                int y0 = getHeight() - padding - labelPadding;
                int y1 = y0 - pointWidth;
                //    if ((i % ((int) ((xActualPerception.size() / 20.0)) + 1)) == 0) {
                g2.setColor(gridColor);
                g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                g2.setColor(Color.BLACK);
                String xLabel = ((int) ((xMin + (xMax - xMin) * (i * 1.0) / numberXDivisions) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                //   }
                g2.drawLine(x0, y0, x1, y1);
            }
        }

        // create x and y axes
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

        Stroke oldStroke = g2.getStroke();

        g2.setStroke(GRAPH_STROKE);
        //drawing lines between points
//        for (int i = 0; i < ActualPerceptionGraphPoints.size() - 1; i++) {
//            int x1 = graphPoints.get(i).x;
//            int y1 = graphPoints.get(i).y;
//            int x2 = graphPoints.get(i + 1).x;
//            int y2 = graphPoints.get(i + 1).y;

        //          g2.drawLine(x1, y1, x2, y2);
        //      }

        g2.setStroke(oldStroke);


    }

    private void setxyScale() {
        xScale = ((double) getWidth() - 2 * padding - labelPadding) / (xMax - xMin);
        yScale = ((double) getHeight() - 2 * padding - labelPadding) / (yMax - yMin);
    }

    private void addActualPerceptionGraphPoints(Graphics2D g2) {
        dataPointsToDraw = new ArrayList<>();
        for (int i = 0; i < xActualPerception.size(); i++) {
            int x1 = (int) (xActualPerception.get(i)*xScale + padding + labelPadding);
            int y1 = (int) (getHeight()-yActualPerception.get(i)*yScale - padding-labelPadding);
            System.out.println(x1+" "+y1);
            dataPointsToDraw.add(new Point(x1, y1));
        }
        g2.setColor(pointColor);
        for (int i = 0; i < dataPointsToDraw.size(); i++) {
            int x = dataPointsToDraw.get(i).x - pointWidth / 2;
            int y = dataPointsToDraw.get(i).y - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;
            g2.fillOval(x, y, ovalW, ovalH);

            }

    }


    private void addPeakPerceptionGraphPoints(Graphics2D g2) {
        dataPointsToDraw = new ArrayList<>();
        for (int i = 0; i < peakPerceptionx.size(); i++) {
            int x1 = (int) (peakPerceptionx.get(i)*xScale + padding + labelPadding);
            int y1 = (int) (getHeight()- peakPerceptiony.get(i)*yScale - padding-labelPadding);
            System.out.println(x1+" "+y1);
            dataPointsToDraw.add(new Point(x1, y1));
        }
        g2.setColor(peakPointColor);
        for (int i = 0; i < dataPointsToDraw.size(); i++) {
            int x = dataPointsToDraw.get(i).x - highlightPointWidth / 2;
            int y = dataPointsToDraw.get(i).y - highlightPointWidth  / 2;
            int ovalW = highlightPointWidth ;
            int ovalH = highlightPointWidth ;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }



    private void addFinalPerceptionGraphPoints(Graphics2D g2) {
        dataPointsToDraw = new ArrayList<>();
        for (int i = 0; i < finalPerceptionx.size(); i++) {
            int x1 = (int) (finalPerceptionx.get(i)*xScale + padding + labelPadding);
            int y1 = (int) (getHeight()-finalPerceptiony.get(i)*yScale - padding-labelPadding);
            System.out.println(x1+" "+y1);
            dataPointsToDraw.add(new Point(x1, y1));
        }
        g2.setColor(finalPerceptionPointColor);
        for (int i = 0; i < dataPointsToDraw.size(); i++) {
            int x = dataPointsToDraw.get(i).x - highlightPointWidth / 2;
            int y = dataPointsToDraw.get(i).y - highlightPointWidth  / 2;
            int ovalW = highlightPointWidth ;
            int ovalH = highlightPointWidth ;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }


    private void addGamut(Graphics2D g2) {
        g2.setColor(gamutColor);
        gamutPoints = new ArrayList<>();

        for (int i = 0; i < gamutUV.length; i++) {
            int x1 = (int) (gamutUV[i][0] * xScale + padding + labelPadding);
            int y1 = (int) (getHeight()-gamutUV[i][1] * yScale - padding-labelPadding);
            System.out.println(x1 + " gamut points " + y1);
            gamutPoints.add(new Point(x1, y1));
        }



        int xprev = gamutPoints.get(gamutPoints.size()-1).x - pointWidth / 2;
        int yprev = gamutPoints.get(gamutPoints.size()-1).y - pointWidth / 2;
        for (int i = 0; i < gamutPoints.size(); i++) {
            int x = gamutPoints.get(i).x - pointWidth / 2;
            int y = gamutPoints.get(i).y - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;
            g2.fillOval(x, y, ovalW, ovalH);
            g2.drawLine(x, y, xprev, yprev);
            System.out.println(x +" "+ y+ " gamut points drawline " + xprev+" "+yprev);
            xprev = x;
            yprev = y;

        }
    }

    private void addChromCurve(Graphics2D g2){

        chromCurvePoints = new ArrayList<>();
        for (int i = 0; i < xchromCurve.size(); i++) {
            int x1 = (int) (xchromCurve.get(i)*xScale + padding + labelPadding);
            int y1 = (int) (getHeight()-ychromCurve.get(i)*yScale - padding-labelPadding); //Graph2D y coordinates start from above
            System.out.println(x1+" chromcurve "+y1);
            chromCurvePoints.add(new Point(x1, y1));
        }
        g2.setColor(curveColor);
        int xprev=0;
        int yprev=0;
        for (int i = 0; i < chromCurvePoints.size(); i++) {
            int x = chromCurvePoints.get(i).x - pointWidth / 2;
            int y = chromCurvePoints.get(i).y - pointWidth / 2;
            if (xprev!=0)g2.drawLine(x, y, xprev, yprev);
            xprev=x;
            yprev=y;

System.out.println(xprev+" drawline " +yprev);
        }

    }

//    @Override
//    public Dimension getPreferredSize() {
//        return new Dimension(width, height);
//    }

    private double getMinScore(List<Double> scores) {
        double minScore = Double.MAX_VALUE;
        for (Double score : scores) {
            minScore = Math.min(minScore, score);
        }
        return minScore;
    }

    private double getMaxScore(List<Double> scores) {
        double maxScore = Double.MIN_VALUE;
        for (Double score : scores) {
            maxScore = Math.max(maxScore, score);
        }
        return maxScore;
    }

    public void setxActualPerception(List<Double> xActualPerception) {
        this.xActualPerception = xActualPerception;
        invalidate();
        this.repaint();
    }

    public List<Double> getxActualPerception() {
        return xActualPerception;
    }

    private void createAndShowGui() {
  //      List<Double> scores = new ArrayList<>();
  //      Random random = new Random();
 //       int maxDataPoints = 40;
 //       int maxScore = 10;
 //       for (int i = 0; i < maxDataPoints; i++) {
 //           xActualPerception.add((double) random.nextDouble() * maxScore);
//            xActualPerception.add((double) i);
 //       }

        this.setPreferredSize(new Dimension(width, height));
        JFrame frame = new JFrame("Virtual color perception graph "+generateDateTag());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private String generateDateTag() {
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
        String DateTag = dateFormat.format(date);
        return DateTag;
    }

    private void setChromCurve()
    {
        xchromCurve=new ArrayList<>();
        ychromCurve=new ArrayList<>();

        xchromCurve.add(0.25240637245008);
        xchromCurve.add(0.251905899062193);
        xchromCurve.add(0.251164863942499);
        xchromCurve.add(0.250172501515922);
        xchromCurve.add(0.248823115066219);
        xchromCurve.add(0.24715338245144);
        xchromCurve.add(0.244877170705161);
        xchromCurve.add(0.24166625977414);
        xchromCurve.add(0.237568483852778);
        xchromCurve.add(0.232490514830098);
        xchromCurve.add(0.226588404901658);
        xchromCurve.add(0.220224474968936);
        xchromCurve.add(0.212684570939439);
        xchromCurve.add(0.203826251205131);
        xchromCurve.add(0.192646270827486);
        xchromCurve.add(0.179576468272035);
        xchromCurve.add(0.162043625335355);
        xchromCurve.add(0.140958807204658);
        xchromCurve.add(0.112973625101763);
        xchromCurve.add(0.0811680465997685);
        xchromCurve.add(0.051936374729721);
        xchromCurve.add(0.0264265142322481);
        xchromCurve.add(0.0101901365886103);
        xchromCurve.add(0.00299928920954349);
        xchromCurve.add(0.00201744967654038);
        xchromCurve.add(0.00729777212731559);
        xchromCurve.add(0.0158167125885579);
        xchromCurve.add(0.0269184373248734);
        xchromCurve.add(0.0401674176943184);
        xchromCurve.add(0.0546806843218146);
        xchromCurve.add(0.0703015981339825);
        xchromCurve.add(0.0855540612194017);
        xchromCurve.add(0.101504588091911);
        xchromCurve.add(0.118772573443677);
        xchromCurve.add(0.137456364629426);
        xchromCurve.add(0.157902150047641);
        xchromCurve.add(0.180070557087988);
        xchromCurve.add(0.204467055238862);
        xchromCurve.add(0.23104525986804);
        xchromCurve.add(0.259192578804431);
        xchromCurve.add(0.28876592459143);
        xchromCurve.add(0.319284455888332);
        xchromCurve.add(0.350095541023298);
        xchromCurve.add(0.380017397787996);
        xchromCurve.add(0.408758130535813);
        xchromCurve.add(0.435764572790724);
        xchromCurve.add(0.460546202976883);
        xchromCurve.add(0.482725975227535);
        xchromCurve.add(0.50170134270113);
        xchromCurve.add(0.516349263384837);
        xchromCurve.add(0.528552537581128);
        xchromCurve.add(0.540664154843487);
        xchromCurve.add(0.551695839244053);
        xchromCurve.add(0.561851082764758);
        xchromCurve.add(0.570049080881619);
        xchromCurve.add(0.574642865746429);
        xchromCurve.add(0.57748129886766);
        xchromCurve.add(0.580167045826481);
        xchromCurve.add(0.582229260922925);
        xchromCurve.add(0.583732989264065);
        xchromCurve.add(0.5847713756459);
        xchromCurve.add(0.585415430146431);
        xchromCurve.add(0.585762623633524);
        xchromCurve.add(0.586110093982668);
        xchromCurve.add(0.586259094682358);
        xchromCurve.add(0.586159755224269);
        xchromCurve.add(0.585936324179646);
        xchromCurve.add(0.585588992302907);
        xchromCurve.add(0.585142829275429);
        xchromCurve.add(0.584598139039953);
        xchromCurve.add(0.58400470807882);
        xchromCurve.add(0.583362737105939);
        xchromCurve.add(0.582672442789235);
        xchromCurve.add(0.581934057569046);
        xchromCurve.add(0.58114782946395);
        xchromCurve.add(0.580338522721762);
        xchromCurve.add(0.579481816750312);
        xchromCurve.add(0.578577997192779);
        xchromCurve.add(0.577651713551278);
        xchromCurve.add(0.576678820660179);
        xchromCurve.add(0.575708117929876);
        xchromCurve.add(0.598);
        xchromCurve.add(0.4);
        xchromCurve.add(0.2524);





        ychromCurve.add(0.0411119076287053);
        ychromCurve.add(0.0408123481372665);
        ychromCurve.add(0.0404307036544341);
        ychromCurve.add(0.0398112589474271);
        ychromCurve.add(0.0391458081973959);
        ychromCurve.add(0.0385130609511052);
        ychromCurve.add(0.0379476588827104);
        ychromCurve.add(0.0372690303634829);
        ychromCurve.add(0.0372788850445885);
        ychromCurve.add(0.0388615026724639);
        ychromCurve.add(0.0417670682730924);
        ychromCurve.add(0.0458116338600053);
        ychromCurve.add(0.0516196768671089);
        ychromCurve.add(0.0590661953263857);
        ychromCurve.add(0.0697353984333138);
        ychromCurve.add(0.0834635592907505);
        ychromCurve.add(0.103091099965006);
        ychromCurve.add(0.127120857593731);
        ychromCurve.add(0.160426863358606);
        ychromCurve.add(0.202335075737342);
        ychromCurve.add(0.245381173599366);
        ychromCurve.add(0.287311589011857);
        ychromCurve.add(0.320494117247255);
        ychromCurve.add(0.346676746469672);
        ychromCurve.add(0.36516560952063);
        ychromCurve.add(0.376691176102549);
        ychromCurve.add(0.384227346885346);
        ychromCurve.add(0.387999734045079);
        ychromCurve.add(0.389770522298305);
        ychromCurve.add(0.390448870553559);
        ychromCurve.add(0.390254336555405);
        ychromCurve.add(0.389707457361673);
        ychromCurve.add(0.388744464139649);
        ychromCurve.add(0.38749822256349);
        ychromCurve.add(0.385944463354204);
        ychromCurve.add(0.384125274452131);
        ychromCurve.add(0.381992944291201);
        ychromCurve.add(0.379553294476114);
        ychromCurve.add(0.376895474013196);
        ychromCurve.add(0.374080742119557);
        ychromCurve.add(0.371123407540857);
        ychromCurve.add(0.368071554411167);
        ychromCurve.add(0.36499044589767);
        ychromCurve.add(0.3619982602212);
        ychromCurve.add(0.359124186946419);
        ychromCurve.add(0.356423542720928);
        ychromCurve.add(0.353945379702312);
        ychromCurve.add(0.351727402477246);
        ychromCurve.add(0.349829865729887);
        ychromCurve.add(0.348365073661516);
        ychromCurve.add(0.347144746241887);
        ychromCurve.add(0.345933584515651);
        ychromCurve.add(0.344830416075595);
        ychromCurve.add(0.343814891723524);
        ychromCurve.add(0.342995091911838);
        ychromCurve.add(0.342535713425357);
        ychromCurve.add(0.342251870113234);
        ychromCurve.add(0.341983295417352);
        ychromCurve.add(0.341777073907708);
        ychromCurve.add(0.341626701073593);
        ychromCurve.add(0.34152286243541);
        ychromCurve.add(0.341458456985357);
        ychromCurve.add(0.341423737636648);
        ychromCurve.add(0.341388990601733);
        ychromCurve.add(0.341374090531764);
        ychromCurve.add(0.341384024477573);
        ychromCurve.add(0.341406367582035);
        ychromCurve.add(0.341441100769709);
        ychromCurve.add(0.341485717072457);
        ychromCurve.add(0.341540186096005);
        ychromCurve.add(0.341599529192118);
        ychromCurve.add(0.341663726289406);
        ychromCurve.add(0.341732755721077);
        ychromCurve.add(0.341806594243095);
        ychromCurve.add(0.341885217053605);
        ychromCurve.add(0.341966147727824);
        ychromCurve.add(0.342051818324969);
        ychromCurve.add(0.342142200280722);
        ychromCurve.add(0.342234828644872);
        ychromCurve.add(0.342332117933982);
        ychromCurve.add(0.342429188207012);
        ychromCurve.add(0.34);
        ychromCurve.add(0.165);
        ychromCurve.add(0.04);

    }

    public void graph() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });
    }
}