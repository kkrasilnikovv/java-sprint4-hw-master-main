package com.yandex.taskmanager.service;

import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;

import java.util.List;
import java.util.Map;

public interface HistoryManager {
    void add(Task task);
    void removeById(int id);
    public void setHistory(MemoryLinkedList list);
    List<Task> getHistory();
     void removeAll(Map<Integer, ? extends Task> map);
}
