package com.yandex.taskmanager.model;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private Status status;
    private final String name;
    protected String type;
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
        type = "Task";
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status=Status.NEW;
        type = "Task";
    }
    public Task(String name, String description, long durationInMinutes, String starTime,Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        type = "Task";
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
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        } else {
            return null;
        }

    }

    @Override
    public String toString() {
        return String.valueOf(id) + ',' + type + ',' + name + ',' +
                status + ',' + description;
    }
}
