package com.example.apptracking.data.model;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.apptracking.utils.Const;

public class App {
    @PrimaryKey
    @NonNull
    protected String name;
    protected String packageName;
    protected long usageTimeOfDay;
    @Ignore
    protected float[] usageTimePerHour;

    public App(@NonNull String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
        usageTimePerHour = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        usageTimeOfDay = 0;
    }

    public long getUsageTimeOfDay() {
        return usageTimeOfDay;
    }

    public void setUsageTimeOfDay(long usageTimeOfDay) {
        this.usageTimeOfDay = usageTimeOfDay;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public float[] getUsageTimePerHour() {
        return usageTimePerHour;
    }

    public void setUsageTimePerHour(float[] usageTimePerHour) {
        this.usageTimePerHour = usageTimePerHour;
    }

    public void addUsageTimePerHour(int position, float usageTime) {
        this.usageTimePerHour[position] += usageTime;
    }



}