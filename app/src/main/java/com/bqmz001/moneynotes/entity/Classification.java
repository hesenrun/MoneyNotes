package com.bqmz001.moneynotes.entity;

import android.widget.LinearLayout;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class Classification extends LitePalSupport {
    @Column(unique = true)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private User user;
    @Column(nullable = false)
    private int Color;

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    private List<Note> noteList = new ArrayList<>();

    public List<Note> getNoteList() {
        return LitePal.where("classification_id=?", String.valueOf(id)).find(Note.class);
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
