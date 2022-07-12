package com.yandex.taskmanager.model;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private Status status;
    private final String name;
    private TypeTask type = TypeTask.TASK;
    private final String description;
    private Integer id;
    private Duration duration;
    private LocalDateTime startTime;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status=Status.NEW;
    }
    public Task(String name, String description, long durationInMinutes, String starTime,Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(durationInMinutes);
        this.startTime = LocalDateTime.parse(starTime, FORMATTER);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public DateTimeFormatter getFormatter() {
        return FORMATTER;
    }

    public void setDuration(Duration duration) {

        this.duration = duration;

    }
    public LocalDateTime getStartTime(LocalDateTime startTime) {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        if (startTime != null && duration!=null) {
            return startTime.plus(duration);
        } else {
            return null;
        }

    }
    public TypeTask getType(){
        return type;
    }

    public void setType(TypeTask type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.valueOf(id) + ',' + type + ',' + name + ',' +
                status + ',' + description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return status == task.status && Objects.equals(name, task.name) && Objects.equals(type, task.type) &&
                Objects.equals(description, task.description) && Objects.equals(id, task.id) &&
                Objects.equals(duration, task.duration) && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, name, type, description, id, duration, startTime);
    }
}
