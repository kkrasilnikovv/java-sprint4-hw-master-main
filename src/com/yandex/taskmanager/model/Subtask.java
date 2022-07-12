package com.yandex.taskmanager.model;

import java.util.Objects;

public class Subtask extends Task {
    private final Integer epicId;

    public Integer getEpicId() {
        return epicId;
    }

    public Subtask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId=epicId;

    }
    public Subtask(String name, String description, long durationInMinutes, String starTime,Status status,int epicId) {
        super(name, description, durationInMinutes, starTime,status);
        this.epicId=epicId;

    }
    @Override
    public TypeTask getType(){
        return TypeTask.SUBTASK;
    }
    @Override
    public String toString() {

        return super.toString() +','+
                epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return epicId.equals(subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicId);
    }
}
