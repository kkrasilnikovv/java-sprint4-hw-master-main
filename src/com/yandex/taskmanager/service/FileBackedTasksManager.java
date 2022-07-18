package com.yandex.taskmanager.service;

import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Status;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private static final Charset charset = StandardCharsets.UTF_8;
    private static String path;
    private final String HEAD_FILE = "id,type,name,status,des,epic,startTime,duration\n";

    public FileBackedTasksManager(String path) {
        this.path = path;
    }

    @Override
    public void moveTask(Task task) {
        super.moveTask(task);
        save();
    }

    @Override
    public Task getTaskId(Integer id) {
        Task task = super.getTaskId(id);
        save();
        return task;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean temp = super.updateTask(task);
        save();
        return temp;
    }

    @Override
    public void deleteTaskAll() {
        super.deleteTaskAll();
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();

    }

    @Override
    public void moveSubtask(Subtask subtask) {
        super.moveSubtask(subtask);
        save();
    }


    @Override
    public Subtask getSubtaskId(Integer id) {
        Subtask subtask = super.getSubtaskId(id);
        save();
        return subtask;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean temp = super.updateSubtask(subtask);
        save();
        return temp;
    }

    @Override
    public void deleteSubtaskAll() {
        super.deleteSubtaskAll();
        save();
    }

    @Override
    public boolean deleteSubtaskId(Integer id) {
        boolean temp = super.deleteSubtaskId(id);
        save();
        return temp;
    }

    @Override
    public void moveEpic(Epic epic) {
        super.moveEpic(epic);
        save();
    }

    @Override
    public void addsSubtaskIdToEpic(Epic epic, Integer id) {
        super.addsSubtaskIdToEpic(epic, id);
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean temp = super.updateEpic(epic);
        save();
        return temp;
    }

    @Override
    public void deleteEpicAll() {
        super.deleteEpicAll();
        save();
    }

    @Override
    public boolean deleteEpicById(Integer id) {
        boolean temp = super.deleteEpicById(id);
        save();
        return temp;
    }


    @Override
    public Integer createId() {
        return super.createId();
    }

    @Override
    public void checkStatusEpic(Epic epic) {
        super.checkStatusEpic(epic);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> temp = super.getHistory();
        save();
        return temp;
    }

    @Override
    public Integer getId() {
        return super.getId();
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(path, charset)) {
            fileWriter.write(HEAD_FILE);
            for (Task task : super.getTaskAll()) {
                fileWriter.write(task.toString());
                fileWriter.write("\n");
            }
            for (Epic epic : super.getEpicAll()) {
                fileWriter.write(epic.toString());
                fileWriter.write("\n");
            }
            for (Subtask subtask : super.getSubtaskAll()) {
                fileWriter.write(subtask.toString());
                fileWriter.write("\n");
            }
            if (!super.getHistory().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(super.getHistory().get(0).getId());
                for (int i = 1; i < super.getHistory().size(); i++) {
                    Task task = super.getHistory().get(i);
                    sb.append(",");
                    sb.append(task.getId());
                }
                fileWriter.write("\n");
                fileWriter.write(String.valueOf(sb));
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранение");
        }
    }


    public static FileBackedTasksManager load(File file) {
        FileBackedTasksManager backedTasksManager = new FileBackedTasksManager(file.getPath());
        boolean flag = false;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                String[] splitLine = line.split(",");
                if (line.isBlank()) {
                    flag = true;
                } else if (flag) {
                    for (String numb : splitLine) {
                        backedTasksManager.getAnyTask(Integer.parseInt(numb));
                    }
                } else {
                    if (splitLine[1].equals("TASK")) {
                        //0    1    2     3     4   5     6         7
                        //"id,type,name,status,des,epic,startTime,duration\n"
                        //  0                   1        2    3        4          5          6        7
                        //String.valueOf(id)  getType() name status  description startTime duration epic;
                        if (splitLine.length == 2) {
                            Task task = new Task(splitLine[2],splitLine[4]);
                            task.setStatus(Status.valueOf(splitLine[3]));
                            task.setId(Integer.parseInt(splitLine[0]));
                            backedTasksManager.tasks.put(task.getId(), task);
                            backedTasksManager.addPrioritizedTasks(task);
                            if (backedTasksManager.id < task.getId()) {
                                backedTasksManager.id = task.getId();
                            }
                        }else {
                            Task task = new Task(splitLine[2], splitLine[4], Status.valueOf(splitLine[3]));
                            task.setId(Integer.parseInt(splitLine[0]));
                            if(!splitLine[6].equals("null")){
                            task.setDuration(Duration.parse(splitLine[6]));
                            }
                            if(!splitLine[5].equals("null")){
                                task.setStartTime(LocalDateTime.parse(splitLine[5]));
                            }

                            backedTasksManager.tasks.put(task.getId(), task);
                            backedTasksManager.addPrioritizedTasks(task);
                            if (backedTasksManager.id < task.getId()) {
                                backedTasksManager.id = task.getId();
                            }
                        }
                    } else if (splitLine[1].equals("EPIC")) {
                        Epic epic = new Epic(splitLine[2], splitLine[4]);
                        epic.setId(Integer.parseInt(splitLine[0]));
                        epic.setStatus(Status.valueOf(splitLine[3]));
                        backedTasksManager.epics.put(epic.getId(), epic);
                        backedTasksManager.addPrioritizedTasks(epic);
                        if (backedTasksManager.id < epic.getId()) {
                            backedTasksManager.id = epic.getId();
                        }
                    } else if (splitLine[1].equals("SUBTASK")) {
                        if (splitLine.length != 8) {
                            Subtask subtask = new Subtask(splitLine[2], splitLine[4], Status.valueOf(splitLine[3]),
                                    Integer.parseInt(splitLine[5]));
                            subtask.setId(Integer.parseInt(splitLine[0]));
                            if (backedTasksManager.epics.containsKey(subtask.getEpicId())) {
                                backedTasksManager.subtasks.put(subtask.getId(), subtask);
                                backedTasksManager.epics.get(subtask.getEpicId()).setIdSubtaskValue(subtask.getId());
                                backedTasksManager.addPrioritizedTasks(subtask);
                                if (backedTasksManager.id < subtask.getId()) {
                                    backedTasksManager.id = subtask.getId();
                                }
                            }
                        } else {
                            Subtask subtask = new Subtask(splitLine[2], splitLine[4], Status.valueOf(splitLine[3]),
                                    Integer.parseInt(splitLine[7]));
                            subtask.setId(Integer.parseInt(splitLine[0]));
                            if(!splitLine[6].equals("null")){
                                subtask.setDuration(Duration.parse(splitLine[6]));
                            }
                            if(!splitLine[5].equals("null")){
                                subtask.setStartTime(LocalDateTime.parse(splitLine[5]));
                            }

                            if (backedTasksManager.epics.containsKey(subtask.getEpicId())) {
                                backedTasksManager.subtasks.put(subtask.getId(), subtask);
                                backedTasksManager.epics.get(subtask.getEpicId()).setIdSubtaskValue(subtask.getId());
                                backedTasksManager.addPrioritizedTasks(subtask);
                                backedTasksManager.getEndTime(backedTasksManager.epics.get(subtask.getEpicId()));
                                if (backedTasksManager.id < subtask.getId()) {
                                    backedTasksManager.id = subtask.getId();
                                }
                            }
                        }
                    }
                }
            }

            } catch(IOException e){
                e.printStackTrace();
            }
            return backedTasksManager;
        }

        private Task getAnyTask ( int id){
            Task task = super.getTaskId(id);
            if (task != null) {
                return task;
            }
            task = super.getEpicById(id);
            if (task != null) {
                return task;
            }
            return super.getSubtaskId(id);
        }
    }
