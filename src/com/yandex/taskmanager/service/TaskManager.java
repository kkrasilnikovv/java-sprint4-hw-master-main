package com.yandex.taskmanager.service;

import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;

import java.time.LocalDateTime;
import java.util.*;


public interface TaskManager {
    public boolean isValid(Task task);
    public void getEndTime(Epic epic);
    public Set getPrioritizedTasks();
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
    public Map<Integer, Task> getTasks();
    public Map<Integer, Epic> getEpics();
    public Map<Integer, Subtask> getSubtasks();

    public List<Task> getHistory();
}
