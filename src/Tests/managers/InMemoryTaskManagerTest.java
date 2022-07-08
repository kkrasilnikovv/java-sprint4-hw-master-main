package Tests.managers;

import com.yandex.taskmanager.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void createManager() {
        super.manager = new InMemoryTaskManager();
    }

}
