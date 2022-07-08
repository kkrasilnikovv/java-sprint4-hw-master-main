package com.yandex.taskmanager.service;

import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


public interface TaskManager {
    public LocalDateTime getEndTime(Epic epic);
    public TreeSet<Task> getPrioritizedTasks();
    public void moveTask(Task task);

    public ArrayList<Task> getTaskAll();

    public Task getTaskId(Integer id);

    public boolean updateTask(Task task);

    public void deleteTaskAll();

    public void deleteTaskById(Integer id);

    public void moveSubtask(Subtask subtask);

    public ArrayList<Subtask> getSubtaskAll();

    public Subtask getSubtaskId(Integer id);

    public boolean updateSubtask(Subtask subtask);

    public void deleteSubtaskAll();

    public boolean deleteSubtaskId(Integer id);


    public void moveEpic(Epic epic);

    public void addsSubtaskIdToEpic(Epic epic, Integer id);

    public ArrayList<Epic> getEpicAll();

    public Epic getEpicById(Integer id);

    public boolean updateEpic(Epic epic);

    public void deleteEpicAll();

    public boolean deleteEpicById(Integer id);

    public ArrayList<Subtask> getSubtaskInEpicAll(Epic epic);

    public Integer createId();

    public void checkStatusEpic(Epic epic);

    public List<Task> getHistory();
}
