package com.garailorinc.virtcolsim.csvparser;

import java.util.Objects;

public class ColorChange
{
    public ColorChange(Integer firstColor, Integer secondColor)
    {
        if (firstColor==null||secondColor==null) throw new RuntimeException();
        this.firstColor = firstColor;
        this.secondColor = secondColor;
    }
    private Integer firstColor, secondColor;

    public Integer getFirstColor()
    {
        return this.firstColor;
    }

    public Integer getSecondColor()
    {
        return this.secondColor;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (!(other instanceof ColorChange))return false;
        if (Objects.equals(((ColorChange) other).getFirstColor(), this.firstColor) && Objects.equals(((ColorChange) other).getSecondColor(), this.secondColor)) return true;
        if (other == this) return true;
        return false;
    }

    public String getKeyInString(String color1, String color2)
    {
        return color1 + "->"+color2;
    }

    public String getKeyInString()
    {
        return firstColor + "->" + secondColor;
    }
}
