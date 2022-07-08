package Tests.managers;


import com.yandex.taskmanager.model.Status;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.HistoryManager;
import com.yandex.taskmanager.service.Managers;
import com.yandex.taskmanager.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager managerHistory;
    private TaskManager managerTask;

    @BeforeEach
    public void createHistoryManager() {
        managerHistory = Managers.getDefaultHistory();
        managerTask=Managers.getDefault();
    }

    @Test
    public void shouldBeEmptyList() {
        assertTrue(managerHistory.getHistory().isEmpty());
    }

    @Test
    public void shouldBeOneTaskWhenAddTwoCloneTasks() {
        Task cloneTask = new Task("Clone Task", "des", Status.NEW);
        managerTask.moveTask(cloneTask);
        managerHistory.add(cloneTask);
        managerHistory.add(cloneTask);
        assertEquals(1, managerHistory.getHistory().size());
    }

    @Test
    public void shouldBe2TaskAfterRemoveBeginningEndMiddle() {
        Task First =new Task("First Task", "des", Status.NEW);
        Task Second =new Task("Second Task", "des", Status.NEW);
        Task Third =new Task("Third Task", "des", Status.NEW);
        Task Fourth =new Task("Fourth Task", "des", Status.NEW);
        Task Fifth =new Task("Fifth Task", "des", Status.NEW);

        managerTask.moveTask(First);
        managerTask.moveTask(Second);
        managerTask.moveTask(Third);
        managerTask.moveTask(Fourth);
        managerTask.moveTask(Fifth);

        managerHistory.add(First);
        managerHistory.add(Second);
        managerHistory.add(Third);
        managerHistory.add(Fourth);
        managerHistory.add(Fifth);

        managerHistory.removeById(1);
        managerHistory.removeById(3);
        managerHistory.removeById(5);
        assertEquals(2, managerHistory.getHistory().size());
    }


}