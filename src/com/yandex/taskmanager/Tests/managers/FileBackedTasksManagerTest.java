package com.yandex.taskmanager.Tests.managers;


import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Status;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.FileBackedTasksManager;
import com.yandex.taskmanager.service.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @BeforeEach
    public void createManager() {
        super.manager = Managers.getDefaultFileBackedManager("C:\\Users\\Иван\\Desktop\\text.txt");
    }
//"resources/History.txt"
    @Test
    public void shouldBeEmptyWhenSaveEmptyAndLoadEmptyTasks() {
        manager.deleteTaskAll();
        manager.deleteEpicAll();
        FileBackedTasksManager load = FileBackedTasksManager.loadFromFile(
                new File("C:\\Users\\Иван\\Desktop\\text.txt"));
        assertTrue(load.getTaskAll().isEmpty() && load.getEpicAll().isEmpty());
    }

    @Test
    public void shouldBeEpicTaskSubtaskInLoadFile() {
        manager.deleteEpicAll();
        manager.deleteSubtaskAll();
        manager.deleteTaskAll();
        Epic epicTest = new Epic("Name", "desc");
        manager.moveEpic(epicTest);
        Subtask subtaskTest=new Subtask("Name","desc", Status.NEW,1);
        manager.moveSubtask(subtaskTest);
        Task taskTest=new Task("Name","desc",Status.NEW);
        manager.moveTask(taskTest);
        FileBackedTasksManager load = FileBackedTasksManager.loadFromFile(
                new File("C:\\Users\\Иван\\Desktop\\text.txt"));
        assertEquals(epicTest, load.getEpicById(1));
        assertEquals(subtaskTest, load.getSubtaskId(2));
        assertEquals(taskTest,load.getTaskId(3));
    }


    @Test
    public void shouldBeEmptyWhenSaveEmptyAndLoadEmptyHistory() {
        manager.deleteSubtaskAll();
        manager.deleteTaskAll();
        manager.deleteEpicAll();
        FileBackedTasksManager load = FileBackedTasksManager.loadFromFile(
                new File("C:\\Users\\Иван\\Desktop\\text.txt"));
        assertEquals(0, load.getHistory().size());
    }

    @Test
    public void shouldBeWhenSaveAndLoadHistory() {
        Task taskTest=new Task("Name","desc",Status.NEW);
        manager.moveTask(taskTest);
        List<Task> temp=new ArrayList<>();
        temp.add(manager.getTaskId(1));
        FileBackedTasksManager load = FileBackedTasksManager.loadFromFile(
                new File("C:\\Users\\Иван\\Desktop\\text.txt"));
        assertEquals(temp,load.getHistory());
    }
    @Test
    public void shouldBeWhenSaveAndSortedList() {
        Task one=new Task("name1", "des1", 20,
                "07.05.2022 05:00", Status.NEW);
        Task two =new Task("name2", "des2", 20,
                "08.05.2022 05:00", Status.NEW);
        Task three =new Task("name3", "des3", 20,
                "06.05.2022 05:00", Status.NEW);

        manager.moveTask(one);
        manager.moveTask(two);
        manager.moveTask(three);


        FileBackedTasksManager load = FileBackedTasksManager.loadFromFile(
                new File("C:\\Users\\Иван\\Desktop\\text.txt"));
        assertEquals(manager.getPrioritizedTasks(),load.getPrioritizedTasks());
    }


}