package com.example.apptracking.ui.custom;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class MyAxisTitleFormatter extends ValueFormatter {

    private final String[] arrayMinus = new String[]{"<1 min", "1-5 min", "5-15 min", "15-30 min", "30-60 min", ">60 min"};

    @Override
    public String getFormattedValue(float value) {
        if (value < arrayMinus.length*10) {
            return arrayMinus[(int) (value/10)];

        } else {
            return "";
        }
    }
}
