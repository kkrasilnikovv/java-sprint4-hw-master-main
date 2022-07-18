package com.yandex.taskmanager;



import com.yandex.taskmanager.api.HttpTaskServer;
import com.yandex.taskmanager.api.KVServer;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.HTTPTaskManager;
import com.yandex.taskmanager.service.InMemoryTaskManager;
import com.yandex.taskmanager.service.Managers;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        InMemoryTaskManager manager=new InMemoryTaskManager();
        Task task=new Task("Name1","Desc1");
        Task task1=new Task("Name2","Desc2");
        Task task2=new Task("Name3","Desc3");
        manager.moveTask(task);
        manager.moveTask(task1);
        manager.moveTask(task2);
        KVServer kvServer=new KVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer=new HttpTaskServer();

        httpTaskServer.start();
        HTTPTaskManager httpTaskManager=Managers.getDefault();
        httpTaskManager.save();

    }
}
