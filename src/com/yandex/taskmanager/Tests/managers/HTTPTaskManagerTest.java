package com.yandex.taskmanager.Tests.managers;

import com.yandex.taskmanager.api.KVServer;

import com.yandex.taskmanager.service.HTTPTaskManager;

import com.yandex.taskmanager.service.Managers;

import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {

    private KVServer server;


    @BeforeEach

    public void createManager()  {
        super.manager = Managers.getDefault();
        server.start();
    }
    @AfterEach

    public void stopServer() {
        server.stop();
    }
    @Test
    public void methodLoad(){
        manager.save();
        manager.load();
        assertEquals(1, manager.getTasks().size());
    }



}