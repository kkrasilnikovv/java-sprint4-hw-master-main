package com.yandex.taskmanager.Tests.managers;
import com.yandex.taskmanager.api.KVServer;
import com.yandex.taskmanager.service.HTTPTaskManager;
import com.yandex.taskmanager.service.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {
    private KVServer server;

    @BeforeEach
    public void createManager() throws IOException, InterruptedException {
        super.manager =  Managers.getDefault();
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

}
