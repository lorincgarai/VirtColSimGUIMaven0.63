package com.garailorinc.virtcolsim.csvparser;

public class ResultValue
{
    private double intensity, tvirtcol;

    ResultValue(Double tvirtcol, Double intensity)
    {
        if (intensity==null||tvirtcol==null) throw new RuntimeException();
       this.intensity = intensity;
       this.tvirtcol = tvirtcol;
    }


    public double getIntensity()
    {
        return this.intensity;
    }

    public double getTvirtcol()
    {
        return this.tvirtcol;
    }
}
