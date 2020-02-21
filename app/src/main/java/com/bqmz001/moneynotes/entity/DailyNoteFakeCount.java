package com.bqmz001.moneynotes.entity;

public class DailyNoteFakeCount {
    private String time;
    private long startTime;
    private long stopTime;
    private float count;

    public DailyNoteFakeCount() {

    }

    public DailyNoteFakeCount(String time, long startTime, long stopTime, float count) {
        this.time = time;
        this.count = count;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }
}
