package com.yandex.taskmanager.model;

public class Subtask extends Task {
    private final Integer epicId;

    public Integer getEpicId() {
        return epicId;
    }

    public Subtask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId=epicId;
        type="Subtask";
    }
    public Subtask(String name, String description, long durationInMinutes, String starTime,Status status,int epicId) {
        super(name, description, durationInMinutes, starTime,status);
        this.epicId=epicId;
        type="Subtask";
    }
    @Override
    public String toString() {

        return super.toString() +','+
                epicId;
    }
}
