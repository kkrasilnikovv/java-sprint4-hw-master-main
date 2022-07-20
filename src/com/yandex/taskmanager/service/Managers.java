package com.yandex.taskmanager.service;


import java.io.IOException;

public class Managers {
    public static HTTPTaskManager getDefault() throws IOException {
        return new HTTPTaskManager("http://localhost:8070/", false);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultFileBackedManager(String path) {
        return new FileBackedTasksManager(path);
    }
}
