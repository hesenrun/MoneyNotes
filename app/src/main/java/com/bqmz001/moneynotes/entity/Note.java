package com.bqmz001.moneynotes.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Note extends LitePalSupport {

    @Column(unique = true)
    private int id;

    @Column(nullable = false)
    private long time;//时间

    @Column(nullable = false)
    private float cost;//花了多少

    @Column(nullable = false)
    private String note;//内容

    @Column(nullable = false,defaultValue = "")
    private String summary;//备注

    @Column(nullable = false)
    private User user;//用户

    @Column(nullable = false)
    private Classification classification;//分类

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }
}
