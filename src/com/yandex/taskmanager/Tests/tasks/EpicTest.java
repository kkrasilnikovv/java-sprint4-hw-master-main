package com.yandex.taskmanager.Tests.tasks;

import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Status;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.service.Managers;
import com.yandex.taskmanager.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;
import static org.testng.AssertJUnit.assertTrue;

class EpicTest {
    private Epic epic;
    private TaskManager manager;

    @BeforeEach
    public void createTaskManager() {
        epic = new Epic("Name", "Descrip");
        manager=Managers.getDefault();
        manager.moveEpic(epic);
    }


    @Test
    public void shouldBeEmptySubtasksMap() {

        ArrayList<Subtask> subtasks = manager.getSubtaskInEpicAll(epic);
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void shouldBeNewEpicWhenAllSubtaskNew() {
        Subtask one =new Subtask( "NameSubtask1", "DesSubtask1", Status.NEW,1);
        Subtask two =new Subtask( "NameSubtask2", "DesSubtask2", Status.NEW,1);
        manager.moveSubtask(one);
        manager.moveSubtask(two);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeDoneEpicWhenAllSubtaskDone() {

        Subtask one =new Subtask( "NameSubtask1", "DesSubtask1", Status.DONE,1);
        Subtask two =new Subtask( "NameSubtask2", "DesSubtask2", Status.DONE,1);

        manager.moveSubtask(one);
        manager.moveSubtask(two);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void shouldBeInProgressWhenSubtaskNewAndDone() {

        Subtask one =new Subtask( "NameSubtask1", "DesSubtask1", Status.NEW,1);
        Subtask two =new Subtask( "NameSubtask2", "DesSubtask2", Status.DONE,1);
        manager.moveSubtask(one);
        manager.moveSubtask(two);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldBeInProgressEpicWhenAllSubtaskInProgress() {

        Subtask one =new Subtask( "NameSubtask1", "DesSubtask1", Status.IN_PROGRESS,1);
        Subtask two = new Subtask( "NameSubtask2", "DesSubtask2", Status.IN_PROGRESS,1);
        manager.moveSubtask(one);
        manager.moveSubtask(two);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }



}