package com.yandex.taskmanager.api;

import com.google.gson.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.yandex.taskmanager.model.Epic;
import com.yandex.taskmanager.model.Subtask;
import com.yandex.taskmanager.model.Task;
import com.yandex.taskmanager.service.Managers;
import com.yandex.taskmanager.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private TaskManager manager = Managers.getDefault();
    private HttpServer httpServer;
    private final int port = 8080;
    private final Gson gson = new Gson();

    public HttpTaskServer() throws IOException, InterruptedException {
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(port), 0);
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/subtask", new SubtasksHandler());
        httpServer.createContext("/tasks/epic", new EpicsHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());
        httpServer.createContext("/tasks/", new AllTasksHandler());
        httpServer.start();
    }


    public class TaskHandler implements HttpHandler {
        protected String response = null;
        protected URI requestURI;
        protected String query;
        protected int responseCode;

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            response = "";
            String method = httpExchange.getRequestMethod();
            switch (method) {
                case "GET":
                    treatmentGet(httpExchange);
                    break;
                case "POST":
                    treatmentPut(httpExchange);
                    break;
                case "DELETE":
                    treatmentDelete(httpExchange);
                    break;
                default:
                    responseCode = 400;
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        protected void treatmentGet(HttpExchange httpExchange) {
            requestURI = httpExchange.getRequestURI();
            query = requestURI.getQuery();
            if (query != null) {
                int numb = Integer.parseInt(query.substring(3));
                Task task = manager.getTaskId(numb);
                if (task != null) {
                    response = gson.toJson(task);
                    responseCode = 200;
                } else {
                    responseCode = 400;
                }
            } else {
                response = gson.toJson(manager.getTasks());
                responseCode = 200;
            }
        }

        protected void treatmentPut(HttpExchange httpExchange) throws IOException {
            Headers requestHeaders = httpExchange.getRequestHeaders();
            List<String> contentTypeValues = requestHeaders.get("Content-type");
            if ((contentTypeValues != null) && (contentTypeValues.contains("application/json"))) {
                try (InputStream inputStream = httpExchange.getRequestBody()) {
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    try {
                        Task taskPost = gson.fromJson(body, Task.class);
                        if (manager.getTasks().containsKey(taskPost.getId())) {
                            manager.updateTask(taskPost);
                        } else {
                            manager.moveTask(taskPost);
                        }
                        responseCode = 201;
                    } catch (JsonSyntaxException e) {
                        responseCode = 400;
                    }
                }
            } else {
                responseCode = 200;
            }
        }

        protected void treatmentDelete(HttpExchange httpExchange) {
            requestURI = httpExchange.getRequestURI();
            query = requestURI.getQuery();
            if (query != null) {
                int numb = Integer.parseInt(query.substring(3));
                try {
                    manager.deleteTaskById(numb);
                } catch (Error ex) {
                    responseCode = 400;
                    return;
                }
            } else {
                manager.deleteTaskAll();
            }
            responseCode = 200;
        }
    }

    public class SubtasksHandler extends TaskHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            response = "";
            String method = httpExchange.getRequestMethod();
            switch (method) {
                case "GET":
                    treatmentGet(httpExchange);
                    break;
                case "POST":
                    treatmentPut(httpExchange);
                    break;
                case "DELETE":
                    treatmentDelete(httpExchange);
                    break;
                default:
                    responseCode = 400;
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        @Override
        protected void treatmentGet(HttpExchange httpExchange) {
            requestURI = httpExchange.getRequestURI();
            query = requestURI.getQuery();
            String path = requestURI.getPath();
            String[] split = path.split("/");
            if (split.length == 3) {
                if (query != null) {
                    int numb = Integer.parseInt(query.substring(3));
                    Subtask subtask = manager.getSubtask().get(numb);
                    if (subtask != null) {
                        response = gson.toJson(subtask);
                        responseCode = 200;
                    } else {
                        responseCode = 400;
                    }
                } else {
                    response = gson.toJson(manager.getSubtask());
                    responseCode = 200;
                }
            } else {
                if (query != null) {
                    int numb = Integer.parseInt(query.substring(3));
                    Epic main = manager.getEpics().get(numb);
                    if (main != null) {
                        ArrayList<Subtask> subtasks=new ArrayList<>();
                        for(Integer integer: main.getIdSubtask()){
                            if(manager.getSubtaskId(integer)!=null){
                                subtasks.add(manager.getSubtaskId(integer));
                            }
                        }
                        response = gson.toJson(subtasks);
                        responseCode = 200;
                    } else {
                        responseCode = 400;
                    }
                } else {
                    responseCode = 400;
                }
            }
        }

        @Override
        protected void treatmentPut(HttpExchange httpExchange) throws IOException {
            Headers requestHeaders = httpExchange.getRequestHeaders();
            List<String> contentTypeValues = requestHeaders.get("Content-type");
            if ((contentTypeValues != null) && (contentTypeValues.contains("application/json"))) {
                try (InputStream inputStream = httpExchange.getRequestBody()) {
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    try {
                        Subtask subtaskPost = gson.fromJson(body, Subtask.class);
                        if (manager.getSubtask().containsKey(subtaskPost.getId())) {
                            manager.updateSubtask(subtaskPost);
                        } else {
                            manager.moveSubtask(subtaskPost);
                        }
                        responseCode = 201;
                    } catch (JsonSyntaxException e) {
                        responseCode = 400;
                    }
                }
            } else {
                responseCode = 501;
            }
        }

        @Override
        protected void treatmentDelete(HttpExchange httpExchange) {
            requestURI = httpExchange.getRequestURI();
            query = requestURI.getQuery();
            if (query != null) {
                int numb = Integer.parseInt(query.substring(3));
                try {
                    manager.deleteSubtaskId(numb);
                } catch (Error ex) {
                    responseCode = 400;
                    return;
                }
            } else {
                manager.deleteSubtaskAll();
            }
            responseCode = 200;
        }

    }

    public class EpicsHandler extends TaskHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            response = "";
            String method = httpExchange.getRequestMethod();
            switch (method) {
                case "GET":
                    treatmentGet(httpExchange);
                    break;
                case "POST":
                    treatmentPut(httpExchange);
                    break;
                case "DELETE":
                    treatmentDelete(httpExchange);
                    break;
                default:
                    responseCode = 400;
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        @Override
        protected void treatmentGet(HttpExchange httpExchange) {
            requestURI = httpExchange.getRequestURI();
            query = requestURI.getQuery();
            if (query != null) {
                int numb = Integer.parseInt(query.substring(3));
                Epic epic = manager.getEpics().get(numb);
                if (epic != null) {
                    response = gson.toJson(epic);
                    responseCode = 200;
                } else {
                    responseCode = 400;
                }
            } else {
                response = gson.toJson(manager.getEpics());
                responseCode = 200;
            }
        }

        @Override
        protected void treatmentPut(HttpExchange httpExchange) throws IOException {
            Headers requestHeaders = httpExchange.getRequestHeaders();
            List<String> contentTypeValues = requestHeaders.get("Content-type");
            if ((contentTypeValues != null) && (contentTypeValues.contains("application/json"))) {
                try (InputStream inputStream = httpExchange.getRequestBody()) {
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    try {
                        Epic epic = gson.fromJson(body, Epic.class);
                        if (manager.getEpics().containsKey(epic.getId())) {
                            manager.updateEpic(epic);
                        } else {
                            manager.moveEpic(epic);
                        }
                        responseCode = 201;
                    } catch (JsonSyntaxException e) {
                        responseCode = 400;
                    }
                }
            } else {
                responseCode = 400;
            }
        }

        @Override
        protected void treatmentDelete(HttpExchange httpExchange) {
            requestURI = httpExchange.getRequestURI();
            query = requestURI.getQuery();
            if (query != null) {
                int numb = Integer.parseInt(query.substring(3));
                try {
                    manager.deleteEpicById(numb);
                } catch (Error ex) {
                    responseCode = 400;
                    return;
                }
            } else {
                manager.deleteEpicAll();
            }
            responseCode = 200;
        }

    }

    public class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = null;
            String method = httpExchange.getRequestMethod();
            int responseCode;
            switch (method) {
                case "GET":
                    response = gson.toJson(manager.getHistory());
                    responseCode = 200;
                    break;
                default:
                    responseCode = 400;
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public class AllTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = null;
            String method = httpExchange.getRequestMethod();
            int responseCode;
            switch (method) {
                case "GET":
                    response = gson.toJson(manager.getPrioritizedTasks());
                    responseCode = 200;
                    break;
                default:
                    responseCode = 400;
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
