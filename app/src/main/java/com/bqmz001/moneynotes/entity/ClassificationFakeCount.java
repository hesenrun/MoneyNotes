package com.bqmz001.moneynotes.entity;

public class ClassificationFakeCount {
    private int id;
    private String name;
    private int color;
    private int count;
    private float sum;

    public ClassificationFakeCount() {

    }

    public ClassificationFakeCount(int id, String name, int color, float sum, int count) {
        this.color = color;
        this.id = id;
        this.name = name;
        this.sum = sum;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
