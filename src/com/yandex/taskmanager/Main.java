package com.yandex.taskmanager;

import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Status;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.Managers;
import com.yandex.taskmanager.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        final TaskManager managerDefault = Managers.getDefault();
        Task task = new Task("ТЗ 3", "Сделать Яндекс.Практикум", Status.NEW);
        Task task1 = new Task("Домашнка", "Сделать дз по русскому языку", Status.NEW);
        Epic epic = new Epic("Переезд", "Переезд в новую кваритру");
        Subtask subtask = new Subtask("Купить обои", "Обои для гостинной", Status.NEW, 3);
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
        managerDefault.getEpicById(7);
        System.out.println(managerDefault.getHistory());

    }
}
