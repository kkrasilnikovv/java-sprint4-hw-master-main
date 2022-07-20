package com.yandex.taskmanager.service;

import com.yandex.taskmanager.model.Task;

import java.util.List;
import java.util.Map;

public interface HistoryManager {
    void add(Task task);
    void removeById(int id);

    List<Task> getHistory();
     void removeAll(Map<Integer, ? extends Task> map);
}
