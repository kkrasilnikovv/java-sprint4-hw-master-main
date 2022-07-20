package com.yandex.taskmanager.service;

import com.google.common.reflect.TypeToken;
import com.yandex.taskmanager.api.KVTaskClient;
import com.google.gson.Gson;
import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


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
        client.put("History", gson.toJson(super.managerHistory.getHistory().stream().map(Task::getId).
                collect(Collectors.toList())));
    }

    public void load(){
        super.tasks=gson.fromJson(client.load("Tasks"),new TypeToken<HashMap<Integer,Task>>(){}.getType());
        super.subtasks=gson.fromJson(client.load("Subtask"),new TypeToken<HashMap<Integer,Subtask>>(){}.getType());
        super.epics=gson.fromJson(client.load("Epics"),new TypeToken<HashMap<Integer,Epic>>(){}.getType());
        List<Integer> idList=gson.fromJson(client.load("History"),new TypeToken<List<Integer>>(){}.getType());

        for(Task task:super.tasks.values()){
            super.addPrioritizedTasks(task);
            checkId(task);
            addTaskToHistory(task,idList);
        }
        for(Subtask subtask:super.subtasks.values()){
            super.addPrioritizedTasks(subtask);
            checkId(subtask);
            addTaskToHistory(subtask,idList);
        }
        for(Epic epic:super.epics.values()){
            checkId(epic);
            addTaskToHistory(epic,idList);
        }


    }
    private void checkId(Task task){
        if(task.getId()>id){
            super.setId(task.getId());
        }
    }
    private void addTaskToHistory(Task task,List<Integer> idList){
        if(idList.contains(task.getId())){
            super.managerHistory.add(task);
        }
    }

}
