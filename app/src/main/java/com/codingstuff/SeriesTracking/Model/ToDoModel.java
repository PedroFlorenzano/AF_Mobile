package com.codingstuff.SeriesTracking.Model;

public class ToDoModel extends TaskId {

    private String task , due, episodioAtual;
    private int status;

    public String getTask() {
        return task;
    }

    public String getDue() {
        return due;
    }

    public int getStatus() {
        return status;
    }

    public String getEpisodioAtual(){return episodioAtual;}
}
