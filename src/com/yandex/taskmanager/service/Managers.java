package com.yandex.taskmanager.service;


import java.io.IOException;

public class Managers{
    public static HTTPTaskManager getDefault() throws IOException, InterruptedException {
        return new HTTPTaskManager("http://localhost:8070/");
    }
    public static HistoryManager getDefaultHistory(){
        InMemoryHistoryManager inMemoryHistoryManager=new InMemoryHistoryManager();
        return inMemoryHistoryManager;
    }
    public static FileBackedTasksManager getDefaultFileBackedManager(String path) {
        return new FileBackedTasksManager(path);
    }
}
