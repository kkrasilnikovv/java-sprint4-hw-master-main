package com.yandex.taskmanager.service;



public class Managers{
    public static TaskManager getDefault(){
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        return inMemoryTaskManager;
    }
    public static HistoryManager getDefaultHistory(){
        InMemoryHistoryManager inMemoryHistoryManager=new InMemoryHistoryManager();
        return inMemoryHistoryManager;
    }
    public static FileBackedTasksManager getDefaultFileBackedManager(String path) {
        return new FileBackedTasksManager(path);
    }
}
