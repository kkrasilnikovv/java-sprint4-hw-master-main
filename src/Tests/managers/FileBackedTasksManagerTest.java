package Tests.managers;


import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.service.FileBackedTasksManager;
import com.yandex.taskmanager.service.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @BeforeEach
    public void createManager() {
        super.manager = Managers.getDefaultFileBackedManager("resources/History.txt");
    }

    @Test
    public void shouldBeEmptyWhenSaveEmptyAndLoadEmptyTasks() {
        manager.deleteTaskAll();
        manager.deleteEpicAll();
        FileBackedTasksManager load = FileBackedTasksManager.loadFromFile(
                new File("resources/History.txt"));
        assertTrue(load.getTaskAll().isEmpty() && load.getEpicAll().isEmpty());
    }

    @Test
    public void shouldBeOneEpicWithoutSubtaskInLoadFile() {
        manager.deleteEpicAll();
        manager.deleteSubtaskAll();
        Epic universal = new Epic("Name", "desc");
        manager.moveEpic(universal);
        FileBackedTasksManager load = FileBackedTasksManager.loadFromFile(
                new File("resources/History.txt"));
        assertEquals(universal, load.getEpicById(1));
    }


    @Test
    public void shouldBeEmptyWhenSaveEmptyAndLoadEmptyHistory() {
        manager.deleteSubtaskAll();
        manager.deleteTaskAll();
        manager.deleteEpicAll();
        FileBackedTasksManager load = FileBackedTasksManager.loadFromFile(
                new File("resources/History.txt"));
        assertEquals(0, load.getHistory().size());
    }


}