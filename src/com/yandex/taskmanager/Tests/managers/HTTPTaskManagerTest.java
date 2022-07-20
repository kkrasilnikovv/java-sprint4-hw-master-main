package com.yandex.taskmanager.Tests.managers;

import com.google.common.reflect.TypeToken;
import com.yandex.taskmanager.api.HttpTaskServer;
import com.yandex.taskmanager.api.KVServer;
import com.google.gson.Gson;
import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Status;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.HTTPTaskManager;

import com.yandex.taskmanager.service.Managers;

import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;


public class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {

    private KVServer server;


    @BeforeEach
    public void createManager() throws IOException{
        server=new KVServer();
        server.start();
        super.manager = Managers.getDefault();

    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void methodLoad() {
        manager.save();
        manager.load();
        assertFalse(manager.getTasks().isEmpty());
        assertFalse(manager.getSubtasks().isEmpty());
        assertFalse(manager.getEpics().isEmpty());
        assertFalse(manager.getPrioritizedTasks().isEmpty());
        assertFalse(manager.getHistory().isEmpty());

    }
}

class HttpTaskServerTest {
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private final Gson gson=new Gson();
    private HTTPTaskManager manager;
    @BeforeEach
    public void createManager() throws IOException{
        kvServer=new KVServer();
        kvServer.start();
        httpTaskServer=new HttpTaskServer();
        httpTaskServer.start();
        manager=Managers.getDefault();
    }
    @AfterEach
    public void stopServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }
    @Test
    public void requestTasks() throws InterruptedException, IOException {
        Task task=new Task("Name","Desc");
        manager.moveTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        HashMap<Integer,Task> tasks=gson.fromJson(response.body(),new TypeToken<HashMap<Integer,Task>>(){}.getType());
        assertEquals(1,tasks.size());
    }
    @Test
    public void requestEpics() throws InterruptedException, IOException {
        Epic epic=new Epic("Name","Desc");
        manager.moveEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        HashMap<Integer,Epic> tasks=gson.fromJson(response.body(),new TypeToken<HashMap<Integer,Epic>>(){}.getType());
        assertEquals(1,tasks.size());
    }
    @Test
    public void requestSubtasks() throws InterruptedException, IOException {
        Epic epic=new Epic("Name","Desc");
        manager.moveEpic(epic);
        Subtask subtask=new Subtask("Name","Desc", Status.NEW,1);
        manager.moveSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        HashMap<Integer,Subtask> tasks=gson.fromJson(response.body(),new TypeToken<HashMap<Integer,Subtask>>(){}.getType());
        assertEquals(1,tasks.size());
    }
    @Test
    public void sendingTasks() throws InterruptedException, IOException {
        Task task=new Task("Name","Desc");
        manager.moveTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }
    @Test
    public void sendingSubtasks() throws InterruptedException, IOException {
        Epic epic=new Epic("Name","Desc1");
        manager.moveEpic(epic);
        Subtask subtask=new Subtask("Name","Desc", Status.NEW,1);
        manager.moveSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        String json = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }
    @Test
    public void sendingEpics() throws InterruptedException, IOException {
        Epic epic=new Epic("Name","Desc");
        manager.moveEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }
    @Test
    public void deletionTasks() throws InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
    @Test
    public void deletionSubtasks() throws InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
    @Test
    public void deletionEpics() throws InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }


}