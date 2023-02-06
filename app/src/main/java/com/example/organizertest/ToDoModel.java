package com.example.organizertest;

public class ToDoModel extends TaskId {

    private String task , due, sTime, eTime, destination;
    private int status;

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
    public String getDestination() {
        return destination;
    }
}
