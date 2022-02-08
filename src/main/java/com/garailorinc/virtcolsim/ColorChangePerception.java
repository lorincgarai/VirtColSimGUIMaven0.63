package com.garailorinc.virtcolsim;

public class ColorChangePerception
{

    private Integer firstColor, secondColor;
    private double intensity, tvirtcol;

    ColorChangePerception(Integer firstColor, Integer secondColor, double intensity, double tvirtcol)
    {
        if (firstColor==null||secondColor==null) throw new RuntimeException();
       this.firstColor = firstColor;
       this.secondColor = secondColor;
       this.intensity = intensity;
       this.tvirtcol = tvirtcol;
    }


    public Integer getFirstColor()
    {
        return this.firstColor;
    }

    public Integer getSecondColor()
    {
        return this.secondColor;
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
