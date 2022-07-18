package com.yandex.taskmanager.service;


import java.io.IOException;

public class Managers {
    public static HTTPTaskManager getDefault() {
        return new HTTPTaskManager("http://localhost:8080/", true);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultFileBackedManager(String path) {
        return new FileBackedTasksManager(path);
    }
}
