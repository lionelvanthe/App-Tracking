package com.example.apptracking.data.model;

public class AppUsageLimit extends App{

    private long usageTimeLimit = 0;

    public AppUsageLimit(String name, String packageName) {
        super(name, packageName);
    }

    public long getUsageTimeLimit() {
        return usageTimeLimit;
    }

    public void setUsageTimeLimit(long usageTimeLimit) {
        this.usageTimeLimit = usageTimeLimit;

    }
}
