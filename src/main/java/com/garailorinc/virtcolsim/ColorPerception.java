package com.garailorinc.virtcolsim;

import javax.swing.*;
import java.awt.peer.ListPeer;
import java.text.DecimalFormat;
import java.util.List;

public class ColorPerception {
    double u, v, ue, ve, Eszorzo, Enull, D, bL, bM, bS;
    double p1Le, p1Me, p1Se, pL, pM, pS, JL, JM, JS, tauL, tauM, tauS, x, y, z, X, Y, Z;
    double deltacmax, tvirtcol;
    double [][] matrix, invmatrix;
    double [] perception;
    private final JTextArea outPutTextArea;
    DecimalFormat intensityResultFormat, timeResultFormat;
    List<Double> xcoordinates;
    List<Double> ycoordinates;
    private List<Double> newPerceptionReturnx, newPerceptionReturny, peakPerceptionReturnx, peakPerceptionReturny;


    public ColorPerception(JTextArea outPutTextArea, double tauL, double tauM, double tauS, double Enull, double Eszorzo, double D,
                           List<Double> xcoordinates, List<Double> ycoordinates, List<Double> peakPerceptionReturnx, List<Double> peakPerceptionReturny, List<Double> newPerceptionReturnx, List<Double> newPerceptionReturny) {
        this.outPutTextArea = outPutTextArea;
        this.xcoordinates = xcoordinates;
        this.ycoordinates = ycoordinates;
        this.peakPerceptionReturnx = peakPerceptionReturnx;
        this.peakPerceptionReturny = peakPerceptionReturny;
        this.newPerceptionReturnx = newPerceptionReturnx;
        this.newPerceptionReturny = newPerceptionReturny;







        perception = new double [5];

        this.Eszorzo = Eszorzo;
        this.Enull = Enull;
        this.tauL = tauL;
        this.tauM = tauM;
        this.tauS = tauS;
        this.D = D;

        matrix = new double[3][3];
        matrix[0][0] = 0.4002;
        matrix[0][1] = 0.7076;
        matrix[0][2] = -0.0808;
        matrix[1][0] = -0.2263;
        matrix[1][1] = 1.1653;
        matrix[1][2] = 0.0457;
        matrix[2][0] = 0;
        matrix[2][1] = 0;
        matrix[2][2] = 0.9182;

        invmatrix = new double[3][3];
        invmatrix[0][0] = 1.86006661250824;
        invmatrix[0][1] = -1.12948007810077;
        invmatrix[0][2] = 0.219898303049304;
        invmatrix[1][0] = 0.361222924921148;
        invmatrix[1][1] = 0.638804306466829;
        invmatrix[1][2] = -7.12750153053329E-06;
        invmatrix[2][0] = 0;
        invmatrix[2][1] = 0;
        invmatrix[2][2] = 1.08908734480505;

        intensityResultFormat = new DecimalFormat("0.000000");
        timeResultFormat = new DecimalFormat("0.0");
    }

    void equilibrateToFirstGamutPoint(double xbe1, double ybe1, double p0L, double p0M, double p0S, double equilTime){
        perception = actualPerception(xbe1,ybe1, p0L, p0M, p0S, equilTime);
        System.out.print("u: ");
        System.out.print(perception[0]);
        System.out.print("  v: ");
        System.out.println(perception[1]);
        System.out.println("... ");
        p1Le=perception[2];
        p1Me=perception[3];
        p1Se=perception[4];
                    }

    void SecondGamutPointEquilibriumPerception(double xbe2, double ybe2, double p0L, double p0M, double p0S, double equilTime){
        perception = actualPerception(xbe2,ybe2, p0L, p0M, p0S, equilTime);
        System.out.print("ue: ");
        System.out.print(perception[0]);
        System.out.print("  ve: ");
        System.out.println(perception[1]);
        System.out.println("... ");
        ue=perception[0];
        ve=perception[1];
    }

    void IterateSecondGamutPoint(double xbe2, double ybe2, double dt, double deltacMin){
        double deltac = 5; //delta c init nagy szám
        double deltacprev, elapsetime, p2L, p2M, p2S;
        p2L = p1Le; p2M=p1Me; p2S=p1Se;
        for (int i = 0 ; i<= 1000000; i++){
            deltacprev = deltac;
            perception = actualPerception(xbe2, ybe2, p2L, p2M, p2S, dt);
            p2L = perception[2]; p2M=perception[3]; p2S=perception[4];
            elapsetime = i * dt;
            deltac = Math.sqrt(Math.pow((u - ue), 2) + Math.pow((v - ve), 2));
            if (i==0)
            {
                deltacmax = deltac; //deltacmax result for this colour change
                peakPerceptionReturnx.add(perception[0]);
                peakPerceptionReturny.add(perception[1]);
            }
            System.out.println("Time elapsed (s): "+timeResultFormat.format(elapsetime) + " | delta c "+ intensityResultFormat.format(deltac) + " | u' : " + u + " | v': " + v);
            outPutTextArea.append("\nTime elapsed (s): "+timeResultFormat.format(elapsetime)+"  |  "+" delta c: "+ intensityResultFormat.format(deltac));
            outPutTextArea.setCaretPosition(outPutTextArea.getDocument().getLength());


            if ((deltac > deltacprev)||(deltac<=deltacMin)) {// 'Observing change of delta c
                tvirtcol = elapsetime; //time of virtual colour perception
                newPerceptionReturnx.add(perception[0]);
                newPerceptionReturny.add(perception[1]);

                System.out.print("ufinal: ");
                System.out.print(perception[0]);
                System.out.print("  vfinal: ");
                System.out.println(perception[1]);
                System.out.println("... ");
                return;
            }
        }
    }

    double [] actualPerception(double xbe, double ybe, double p0L, double p0M, double p0S, double dt)
    {
        double zbe = 1-xbe-ybe;

        double EL = (matrix[0][0] * xbe + matrix[0][1] * ybe + matrix[0][2] * zbe) * Eszorzo;
        double EM = (matrix[1][0] * xbe + matrix[1][1] * ybe + matrix[1][2] * zbe) * Eszorzo;
        double ES = (matrix[2][0] * xbe + matrix[2][1] * ybe + matrix[2][2] * zbe) * Eszorzo;


        bL = 1 + EL / Enull;
        bM = 1 + EM / Enull;
        bS = 1 + ES / Enull;

        pL = 1 / bL * (1 - (1 - p0L * bL) * Math.exp(-dt * bL / tauL));
        pM = 1 / bM * (1 - (1 - p0M * bM) * Math.exp(-dt * bM / tauM));
        pS = 1 / bS * (1 - (1 - p0S * bS) * Math.exp(-dt * bS / tauS));

        JL = D * pL * EL;
        JM = D * pM * EM;
        JS = D * pS * ES;

        X = invmatrix[0][0] * JL + invmatrix[0][1] * JM + invmatrix[0][2] * JS;
        Y = invmatrix[1][0] * JL + invmatrix[1][1] * JM + invmatrix[1][2] * JS;
        Z = invmatrix[2][0] * JL + invmatrix[2][1] * JM + invmatrix[2][2] * JS;

        x = X / (X + Y + Z);
        y = Y / (X + Y + Z);
        z = Z / (X + Y + Z);

        u = 4 * x / (12 * y - 2 * x + 3);
        v = 6 * y / (12 * y - 2 * x + 3);

        perception[0]=u;
        perception[1]=v;
        perception[2]=pL;
        perception[3]=pM;
        perception[4]=pS;

        xcoordinates.add(perception[0]);
        ycoordinates.add(perception[1]);


        return perception;

    }

}
