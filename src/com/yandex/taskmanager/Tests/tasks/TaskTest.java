package com.yandex.taskmanager.Tests.tasks;

import com.yandex.taskmanager.model.Status;
import com.yandex.taskmanager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    private Task task;

    @BeforeEach
    public void createTaskManager() {
        task = new Task("Name", "Descrip", 60, "06.05.2022 05:00", Status.NEW);
    }


    @Test
    public void shouldBeNormalEndTime() {
        assertEquals("06.05.2022 06:00", task.getEndTime().format(task.getFormatter()));
    }
}