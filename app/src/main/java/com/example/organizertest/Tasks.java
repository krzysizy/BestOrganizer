package com.example.organizertest;

import com.google.firebase.database.Exclude;

public class Tasks {

    @Exclude
    private String id;

    private String text;

    private int year;

    private int month;

    private int day;

    public Tasks() {
    }

    public Tasks(String text, int year, int month, int day) {
        this.text = text;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
