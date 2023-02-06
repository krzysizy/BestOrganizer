package com.example.organizertest;

public class ToDoModel extends TaskId {

    private String task , due, dest, sTime, eTime;
    private int status;

    public String getDest() {
        return dest;
    }

    public String getsTime() {
        return sTime;
    }

    public String geteTime() {
        return eTime;
    }

    public String getTask() {
        return task;
    }

    public String getDue() {
        return due;
    }

    public int getStatus() {
        return status;
    }
}
