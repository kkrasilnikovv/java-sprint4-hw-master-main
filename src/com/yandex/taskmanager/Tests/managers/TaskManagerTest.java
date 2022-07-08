package com.yandex.taskmanager.Tests.managers;


import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Status;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @Test
    public void shouldBeHaveSubtaskEpic() {
        Epic epic = new Epic("Name", "des");
        Subtask subtask = new Subtask("nameSub", "desSub", Status.NEW, 1);
        manager.moveEpic(epic);
        manager.moveSubtask(subtask);
        Subtask forCheck = manager.getSubtaskInEpicAll(epic).get(0);
        assertEquals(epic.getId(), forCheck.getEpicId());
    }

    @Test
    public void shouldBeHaveEpicStatusInProgress() {
        Epic epic = new Epic("Name", "des");
        Subtask newSubtask = new Subtask("nameSubNew", "desSubNew", Status.NEW, 1);
        Subtask subtaskDone = new Subtask("nameSubDone", "desSubDone", Status.DONE, 1);
        manager.moveEpic(epic);
        manager.moveSubtask(newSubtask);
        manager.moveSubtask(subtaskDone);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldBeHave1Task() {
        manager.moveTask(new Task("name", "des", Status.NEW));
        assertEquals(1, manager.getTaskAll().size());
    }

    @Test
    public void shouldBeEmptyTasks() {
        assertEquals(0, manager.getTaskAll().size());
    }

    @Test
    public void shouldBeHave1Epic() {
        manager.moveEpic(new Epic("name", "des"));
        assertEquals(1, manager.getEpicAll().size());
    }

    @Test
    public void shouldBeEmptyEpics() {
        assertEquals(0, manager.getEpicAll().size());
    }

    @Test
    public void shouldBe1Subtask() {
        Epic epic = new Epic("name", "des");
        Subtask subtask = new Subtask("name", "des", Status.NEW, 1);
        manager.moveEpic(epic);
        manager.moveSubtask(subtask);
        assertEquals(1, manager.getSubtaskAll().size());
    }

    @Test
    public void shouldBeEmptySubtasks() {
        assertEquals(0, manager.getSubtaskAll().size());
    }

    @Test
    public void shouldBe1SubtaskInAddedEpic() {
        Epic epic = new Epic("EpicName", "EpicDes");
        Subtask subtask = new Subtask("SubName", "SubDes", Status.NEW, 1);
        manager.moveEpic(epic);
        manager.moveSubtask(subtask);
        assertEquals(1, epic.getIdSubtask().size());
    }

    @Test
    public void shouldBeEmptyInEpicList() {
        Epic epic = new Epic("EpicName", "EpicDes");
        manager.moveEpic(epic);
        assertEquals(0, epic.getIdSubtask().size());
    }

    @Test
    public void shouldBeEmptyWhenHaveAndRemoveAll() {
        manager.moveTask(new Task("Name", "Des", Status.NEW));
        manager.deleteTaskAll();
        assertEquals(0, manager.getTaskAll().size());
    }

    @Test
    public void shouldBeEmptyWhenRemoveEmptyManager() {
        manager.deleteTaskAll();
        assertEquals(0, manager.getTaskAll().size());
    }

    @Test
    public void shouldBeNormalWhenManagerHaveThisTask() {
        Task task = new Task("Name", "des", Status.NEW);
        manager.moveTask(task);
        assertEquals(task, manager.getTaskId(1));
    }

    @Test
    public void shouldBeNullWhenGetNumberNotHaveManager() {
        assertEquals(null, manager.getTaskId(14));
    }

    @Test
    public void shouldBeNormalRemoving() {
        Task task = new Task("Name", "Des", Status.NEW);
        manager.moveTask(task);
        manager.deleteTaskById(1);
        assertEquals(null, manager.getTaskId(1));
    }

    @Test
    public void shouldBeErrorWhenRemoveIncorrectId() {
        Error ex = assertThrows(Error.class, () -> manager.deleteTaskById(14));
        assertEquals("Такой задачи нет", ex.getMessage());
    }

    @Test
    public void shouldBeNullWhenCheckEndTimeWithEmptySubtasks() {
        Epic epic = new Epic("EpicName", "EpicDes");
        assertEquals(null, epic.getEndTime());
    }

    @Test
    public void shouldBeHave60MinutesDurationEpicWithOneSubtask() {
        Epic epic = new Epic("EpicName", "EpicDes");
        manager.moveEpic(epic);
        Subtask one = new Subtask("name1", "des1", 60,
                "06.05.2022 05:00", Status.NEW, 1);
        manager.moveSubtask(one);
        manager.getEndTime(epic);
        assertEquals("06.05.2022 06:00", epic.getEndTime().format(epic.getFormatter()));
    }

    @Test
    public void shouldBeHave60MinutesDurationEpicWithThreeSubtasksAndOneStartTime() {
        Epic epic = new Epic("EpicName", "EpicDes");
        manager.moveEpic(epic);
        Subtask one =new Subtask("name1", "des1", 20,
                "06.05.2022 05:00", Status.NEW, 1);
        Subtask two =new Subtask("name2", "des2", 20,
                "06.05.2022 05:00", Status.NEW, 1);
        Subtask three =new Subtask("name3", "des3", 20,
                "06.05.2022 05:00", Status.NEW, 1);
        manager.moveSubtask(one);
        manager.moveSubtask(two);
        manager.moveSubtask(three);
        manager.getEndTime(epic);
        assertEquals("06.05.2022 06:00", epic.getEndTime().format(epic.getFormatter()));
    }

    @Test
    public void shouldBeHaveMinimalStartTimeWithTreeSubtasks() {
        Epic epic = new Epic("EpicName", "EpicDes");
        manager.moveEpic(epic);
        Subtask one =new Subtask("name1", "des1", 20,
                "07.05.2022 05:00", Status.NEW, 1);
        Subtask two =new Subtask("name2", "des2", 20,
                "08.05.2022 05:00", Status.NEW, 1);
        Subtask three =new Subtask("name3", "des3", 20,
                "06.05.2022 05:00", Status.NEW, 1);
        manager.moveSubtask(one);
        manager.moveSubtask(two);
        manager.moveSubtask(three);
        manager.getEndTime(epic);
        assertEquals("06.05.2022 06:00", epic.getEndTime().format(epic.getFormatter()));
    }


}

