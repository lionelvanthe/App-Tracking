package com.example.apptracking.data.model;

public class App {
    private String name;
    private String packageName;
    private long usageTimeOfDay;
    private long[] usageTimePerHour;

    public App(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
        usageTimePerHour = new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        usageTimeOfDay = 0;
    }

    public long getUsageTimeOfDay() {
        return usageTimeOfDay;
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
