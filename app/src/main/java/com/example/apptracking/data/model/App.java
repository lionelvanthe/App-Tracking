package com.example.apptracking.data.model;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

public class App {
    @PrimaryKey
    @NonNull
    protected String name;
    protected String packageName;
    protected long usageTimeOfDay;
    @Ignore
    protected long[] usageTimePerHour;

    public App(@NonNull String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
        usageTimePerHour = new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        usageTimeOfDay = 0;
    }

    public long getUsageTimeOfDay() {
        return usageTimeOfDay;
    }

    public void setUsageTimeOfDay(long usageTimeOfDay) {
        this.usageTimeOfDay = usageTimeOfDay;
    }

    public long[] getUsageTimePerHour() {
        return usageTimePerHour;
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

    public void addUsageTimePerHour(int position, long usageTime) {
        this.usageTimePerHour[position] = usageTime;
    }

    public void plusUsageTime(long usageTimePerHour) {
        this.usageTimeOfDay += usageTimePerHour;
    }
}
