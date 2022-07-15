package com.yandex.taskmanager.service;

import com.yandex.taskmanager.api.KVTaskClient;
import com.google.gson.Gson;

import java.io.IOException;

public class HTTPTaskManager extends FileBackedTasksManager {
    private KVTaskClient client;
    private Gson gson;

    public HTTPTaskManager(String url) throws IOException, InterruptedException {
        super(url);
        gson = new Gson();
        client = new KVTaskClient(url);
    }

    @Override
    public void save() {
        try {
            client.put("Tasks", gson.toJson(super.getTaskAll()));
            client.put("Subtask", gson.toJson(super.getSubtaskAll()));
            client.put("Epics", gson.toJson(super.getEpicAll()));
            client.put("History", gson.toJson(super.getHistory()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
