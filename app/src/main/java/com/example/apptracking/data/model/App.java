package com.example.apptracking.data.model;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.apptracking.utils.Const;

import java.io.Serializable;

public class App implements Serializable {
    @PrimaryKey
    @NonNull
    protected String name;
    protected String packageName;
    protected long usageTimeOfDay;
    @Ignore
    protected Float[] usageTimePerHour;

    public App(@NonNull String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
        usageTimePerHour = new Float[]{0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F};
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


    public Float[] getUsageTimePerHour() {
        return usageTimePerHour;
    }

    public void setUsageTimePerHour(Float[] usageTimePerHour) {
        this.usageTimePerHour = usageTimePerHour;
    }

    public void addUsageTimePerHour(int position, float usageTime) {
        this.usageTimePerHour[position] += usageTime;
    }



}