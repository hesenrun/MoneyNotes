package com.bqmz001.moneynotes.entity;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class User extends LitePalSupport {
    @Column(unique = true)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int Color;
    @Column(nullable = false)
    private int budget;
    @Column(nullable = false)
    boolean isDefault;

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    private List<Note> noteList = new ArrayList<>();
    private List<Classification> classificationList = new ArrayList<>();

    public List<Classification> getClassificationList() {
        return LitePal.where("user_id=?", String.valueOf(id)).find(Classification.class);
    }

    public void setClassificationList(List<Classification> classificationList) {
        this.classificationList = classificationList;
    }

    public List<Note> getNoteList() {
        return LitePal.where("user_id=?", String.valueOf(id)).find(Note.class);
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
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
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }
}
