package com.codingstuff.SeriesTracking.Model;

public class ToDoModel extends TaskId {

    private String task , due, episodioAtual, temporada, plataforma;
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
    public String getTemporada(){return temporada;}
    public String getPlataforma(){return plataforma;}
}
