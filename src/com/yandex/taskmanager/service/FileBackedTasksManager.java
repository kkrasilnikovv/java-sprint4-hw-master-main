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


    public static FileBackedTasksManager loadFromFile(File file) {
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
                    if (splitLine[1].equals("Task")) {
                        Task task = new Task(splitLine[2], splitLine[4], Status.valueOf(splitLine[3]));
                        task.setId(Integer.parseInt(splitLine[0]));
                        backedTasksManager.tasks.put(task.getId(), task);
                        backedTasksManager.setPrioritizedTasks(task);
                        if (backedTasksManager.id < task.getId()) {
                            backedTasksManager.id = task.getId();
                        }
                    } else if (splitLine[1].equals("Epic")) {
                        Epic epic = new Epic(splitLine[2], splitLine[4]);
                        epic.setId(Integer.parseInt(splitLine[0]));
                        epic.setStatus(Status.valueOf(splitLine[3]));
                        backedTasksManager.epics.put(epic.getId(), epic);
                        backedTasksManager.setPrioritizedTasks(epic);
                        if (backedTasksManager.id < epic.getId()) {
                            backedTasksManager.id = epic.getId();
                        }
                    } else if (splitLine[1].equals("Subtask")) {
                        if (splitLine.length == 6) {
                            Subtask subtask = new Subtask(splitLine[2], splitLine[4], Status.valueOf(splitLine[3]), Integer.parseInt(splitLine[5]));
                            subtask.setId(Integer.parseInt(splitLine[0]));
                            if (backedTasksManager.epics.containsKey(subtask.getEpicId())) {
                                backedTasksManager.subtasks.put(subtask.getId(), subtask);
                                backedTasksManager.epics.get(subtask.getEpicId()).setIdSubtaskValue(subtask.getId());
                                backedTasksManager.setPrioritizedTasks(subtask);
                                if (backedTasksManager.id < subtask.getId()) {
                                    backedTasksManager.id = subtask.getId();
                                }
                            }
                        } else { //"id,type,name,status,des,epic,startTime,duration\n"
                            Subtask subtask = new Subtask(splitLine[2], splitLine[4], Status.valueOf(splitLine[3]), Integer.parseInt(splitLine[5]));
                            subtask.setId(Integer.parseInt(splitLine[0]));
                            subtask.setDuration(Duration.parse(splitLine[7]));
                            subtask.setStartTime(LocalDateTime.parse(splitLine[6]));
                            if (backedTasksManager.epics.containsKey(subtask.getEpicId())) {
                                backedTasksManager.subtasks.put(subtask.getId(), subtask);
                                backedTasksManager.epics.get(subtask.getEpicId()).setIdSubtaskValue(subtask.getId());
                                backedTasksManager.setPrioritizedTasks(subtask);
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

        public static void main (String[]args){
            FileBackedTasksManager managerDefault = Managers.getDefaultFileBackedManager("C:\\Users\\Иван\\" +
                    "Desktop\\bootFile.txt");
        /*Task task = new Task("ТЗ 3", "Сделать Яндекс.Практикум", Status.NEW);
        Task task1 = new Task("Домашка", "Сделать дз по русскому языку", Status.NEW);
        Epic epic = new Epic("Переезд", "Переезд в новую квартиру");
        Subtask subtask = new Subtask("Купить обои", "Обои для гостиной", Status.NEW, 3);
        Subtask subtask1 = new Subtask("Купить клей", "Клей для обоев", Status.NEW, 3);
        Epic epic1 = new Epic("Устроиться на работу", "Стать java разработчиком");
        Subtask subtask2 = new Subtask("Закончить курсы", "Успешно окончить курсы яндекс",
                Status.NEW, 3);
        managerDefault.moveTask(task);
        managerDefault.moveTask(task1);
        managerDefault.moveEpic(epic);
        managerDefault.moveSubtask(subtask);
        managerDefault.moveSubtask(subtask1);
        managerDefault.moveSubtask(subtask2);
        managerDefault.moveEpic(epic1);

        managerDefault.getTaskId(1);
        managerDefault.getTaskId(2);
        managerDefault.getEpicById(3);
        managerDefault.getSubtaskId(4);
        managerDefault.getSubtaskId(5);
        managerDefault.getSubtaskId(6);
        managerDefault.getEpicById(7);*/
            File file = new File("resources/History.txt");
            TaskManager loadManager = loadFromFile(file);
            Task task2 = new Task("Проверка id", "Проверка нового метода", Status.NEW);
            loadManager.moveTask(task2);

            System.out.println(loadManager.getTaskAll());

        }

    }
