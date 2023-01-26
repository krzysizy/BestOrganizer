package com.example.organizertest;

import com.google.firebase.database.Exclude;

public class Tasks {

    @Exclude
    private String id;

    private String text;

    public Tasks() {
    }

    public Tasks(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
