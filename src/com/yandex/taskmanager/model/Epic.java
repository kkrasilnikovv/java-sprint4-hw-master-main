package com.yandex.taskmanager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    public Epic(String name, String description) {
        super(name, description);
        type="Epic";
    }


    private final ArrayList<Integer> idSubtask = new ArrayList<>();

    public ArrayList<Integer> getIdSubtask() {
        return idSubtask;
    }

    public Integer getIdSubtaskValue(Integer value) {
        return idSubtask.get(value);
    }

    public void setIdSubtaskValue(Integer value) {
        idSubtask.add(value);
    }



    @Override
    public Duration getDuration() {
        getEndTime();
        return super.getDuration();
    }

    @Override
    public LocalDateTime getStartTime(LocalDateTime startTime) {
        getEndTime();
        return super.getStartTime();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return idSubtask.equals(epic.idSubtask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSubtask);
    }

}
