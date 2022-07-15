package com.yandex.taskmanager;

import com.yandex.taskmanager.api.KVServer;
import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Status;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.HTTPTaskManager;
import com.yandex.taskmanager.service.InMemoryTaskManager;
import com.yandex.taskmanager.service.Managers;
import com.yandex.taskmanager.service.TaskManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final TaskManager managerDefault = new InMemoryTaskManager();
        HTTPTaskManager httpTaskManager=new HTTPTaskManager("http://localhost:8070/");
        KVServer server=new KVServer();
        server.start();


    }
}
