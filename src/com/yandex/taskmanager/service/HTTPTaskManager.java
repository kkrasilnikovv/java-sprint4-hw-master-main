package com.yandex.taskmanager.service;

import com.google.common.reflect.TypeToken;
import com.yandex.taskmanager.api.KVTaskClient;
import com.google.gson.Gson;
import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;

import java.util.HashMap;
import java.util.TreeSet;


public class HTTPTaskManager extends FileBackedTasksManager {
    private KVTaskClient client;
    private Gson gson;

    public HTTPTaskManager(String url,boolean isLoad) {
        super(url);
        gson = new Gson();
        client = new KVTaskClient(url);
        if(isLoad){
            load();
        }
    }

    @Override
    public void save() {
        client.put("Tasks", gson.toJson(super.getTasks()));
        client.put("Subtask", gson.toJson(super.getSubtasks()));
        client.put("Epics", gson.toJson(super.getEpics()));
        client.put("History", gson.toJson(super.getHistory()));
        client.put("PrioritizedTasks", gson.toJson(super.getPrioritizedTasks()));

    }

    public void load(){
        Integer id=0;
        super.tasks=gson.fromJson(client.load("Tasks"),new TypeToken<HashMap<Integer,Task>>(){}.getType());
        super.subtasks=gson.fromJson(client.load("Subtask"),new TypeToken<HashMap<Integer,Subtask>>(){}.getType());
        super.epics=gson.fromJson(client.load("Epics"),new TypeToken<HashMap<Integer,Epic>>(){}.getType());

        for(Task task:super.tasks.values()){
            super.addPrioritizedTasks(task);
            if(task.getId()<id){
                super.setId(task.getId());
            }
        }
        for(Subtask subtask:super.subtasks.values()){
            super.addPrioritizedTasks(subtask);
            if(subtask.getId()<id){
                super.setId(subtask.getId());
            }
        }
        for(Epic epic:super.epics.values()){
            if(epic.getId()<id){
                super.setId(epic.getId());
            }
        }


    }

}
