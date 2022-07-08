package com.yandex.taskmanager.service;

import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Status;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Integer id = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    protected final HistoryManager managerHistory = Managers.getDefaultHistory();
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    private Set<Task> sortedTasks = new TreeSet<>((o1, o2) -> {
        if (o1.toString().length() > o2.toString().length()) {
            return 1;
        } else if (o1.toString().length() < o2.toString().length()) {
            return -1;
        } else {
            String[] a = o1.toString().split(",");
            if (a.length != 5) {
                LocalDateTime firstTime = o1.getStartTime();
                LocalDateTime secondTime = o2.getStartTime();
                if (firstTime.isAfter(secondTime)) {
                    return -1;
                } else if (firstTime.isBefore(secondTime)) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                return -1;
            }
        }
    });
    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return (TreeSet<Task>) sortedTasks;
    }
    @Override
    public LocalDateTime getEndTime(Epic epic) {
        if (!subtasks.isEmpty()) {
            epic.setDuration(Duration.ofMinutes(0));
            epic.setStartTime(subtasks.entrySet().iterator().next().getValue().getStartTime());
            for (Subtask subtask : subtasks.values()) {
                if (epic.getStartTime().isAfter(subtask.getStartTime())) {
                    epic.setStartTime(subtask.getStartTime());
                }
                epic.setDuration(epic.getDuration().plus(subtask.getDuration()));
            }
            return epic.getStartTime().plus(epic.getDuration());
        } else {
            return null;
        }
    }


    @Override
    public void moveTask(Task task) {
        Integer temp = createId();
        tasks.put(temp, task);
        task.setId(temp);
    }

    @Override
    public ArrayList<Task> getTaskAll() {
        ArrayList<Task> temp = new ArrayList<>(tasks.values());
        return temp;
    }

    @Override
    public Task getTaskId(Integer id) {
        if (tasks.containsKey(id)) {
            managerHistory.add(tasks.get(id));
            return tasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public boolean updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.replace(task.getId(), task);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteTaskAll() {
        managerHistory.removeAll(tasks);
        tasks.clear();

    }

    @Override
    public void deleteTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            managerHistory.removeById(id);
        }
        else
            throw new Error("Такой задачи нет");
    }

    @Override
    public void moveSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            Integer temp = createId();
            subtasks.put(temp, subtask);
            subtask.setId(temp);
            epics.get(subtask.getEpicId()).setIdSubtaskValue(subtask.getId());
            checkStatusEpic(epics.get(subtask.getEpicId()));//проверка
        }
    }

    @Override
    public ArrayList<Subtask> getSubtaskAll() {
        ArrayList<Subtask> temp = new ArrayList<>(subtasks.values());
        return temp;
    }

    @Override
    public Subtask getSubtaskId(Integer id) {
        if (subtasks.containsKey(id)) {
            managerHistory.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.replace(subtask.getId(), subtask);
            checkStatusEpic(epics.get(subtask.getEpicId()));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteSubtaskAll() {
        managerHistory.removeAll(subtasks);
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
            epic.getIdSubtask().clear();
        }
    }

    @Override
    public boolean deleteSubtaskId(Integer id) {
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            epic.getIdSubtask().remove(id);
            subtasks.remove(id);
            managerHistory.removeById(id);
            checkStatusEpic(epic);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void moveEpic(Epic epic) {
        Integer temp = createId();
        epics.put(temp, epic);
        epic.setId(temp);
        epic.setStatus(Status.NEW);
    }

    @Override
    public void addsSubtaskIdToEpic(Epic epic, Integer id) {
        epic.setIdSubtaskValue(id);
    }

    @Override
    public ArrayList<Epic> getEpicAll() {
        ArrayList<Epic> temp = new ArrayList<>(epics.values());
        return temp;
    }

    @Override
    public Epic getEpicById(Integer id) {
        if (epics.containsKey(id)) {
            managerHistory.add(epics.get(id));
            return epics.get(id);
        } else {
            return null;
        }
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.replace(epic.getId(), epic);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteEpicAll() {
        managerHistory.removeAll(epics);
        epics.clear();
        managerHistory.removeAll(subtasks);
        deleteSubtaskAll();
    }

    @Override
    public boolean deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            for (Integer integer : epics.get(id).getIdSubtask()) {
                subtasks.remove(integer);
                managerHistory.removeById(integer);
            }
            epics.remove(id);
            managerHistory.removeById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ArrayList<Subtask> getSubtaskInEpicAll(Epic epic) {
        ArrayList<Subtask> temp = new ArrayList<>();
        for (int i = 0; i < epic.getIdSubtask().size(); i++) {
            if (subtasks.get(epic.getIdSubtaskValue(i)) == null) {
                continue;
            } else
                temp.add(subtasks.get(epic.getIdSubtaskValue(i)));
        }

        return temp;
    }

    public Integer createId() {
        id += 1;
        return id;
    }

    @Override
    public void checkStatusEpic(Epic epic) {
        int count = 0;
        int count1 = 0;
        if (epic.getIdSubtask().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (Subtask subtask : getSubtaskInEpicAll(epic)) {

            if (subtask.getStatus().equals(Status.IN_PROGRESS)) {
                epic.setStatus(Status.IN_PROGRESS);
                break;
            } else if (subtask.getStatus().equals(Status.NEW)) {
                count++;
                if (count == epic.getIdSubtask().size()) {
                    epic.setStatus(Status.NEW);
                }
            } else if (subtask.getStatus().equals(Status.DONE)) {
                count1++;
                if (count1 == epic.getIdSubtask().size()) {
                    epic.setStatus(Status.DONE);
                }
            }
        }
        if(count>0 && count1>0){
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return managerHistory.getHistory();
    }

}
