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
    private final int PORT = 8080;
    private final Gson gson = new Gson();

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/subtask", new SubtasksHandler());
        httpServer.createContext("/tasks/epic", new EpicsHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());
        httpServer.createContext("/tasks/", new AllTasksHandler());
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
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
                    treatmentPost(httpExchange);
                    break;
                case "DELETE":
                    treatmentDelete(httpExchange);
                    break;
                default:
                    responseCode = 405;
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            writeText(httpExchange, response);
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

        protected void treatmentPost(HttpExchange httpExchange) throws IOException {
            Headers requestHeaders = httpExchange.getRequestHeaders();
            List<String> contentTypeValues = requestHeaders.get("Content-type");
            if ((contentTypeValues != null) && (contentTypeValues.contains("application/json"))) {
                String body = readText(httpExchange);
                if (!body.isEmpty()) {

                    try {
                        Task taskPost = gson.fromJson(body, Task.class);
                        if (taskPost.getId() != null) {
                            if (manager.getTasks().containsKey(taskPost.getId())) {
                                manager.updateTask(taskPost);
                            } else {
                                responseCode = 404;
                                return;
                            }
                        } else {
                            manager.moveTask(taskPost);
                        }
                        responseCode = 201;
                    } catch (JsonSyntaxException e) {
                        responseCode = 400;
                    }
                } else {
                    responseCode = 400;
                    return;
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
                    treatmentPost(httpExchange);
                    break;
                case "DELETE":
                    treatmentDelete(httpExchange);
                    break;
                default:
                    responseCode = 405;
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            writeText(httpExchange, response);
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
                    Subtask subtask = manager.getSubtasks().get(numb);
                    if (subtask != null) {
                        response = gson.toJson(subtask);
                        responseCode = 200;
                    } else {
                        responseCode = 400;
                    }
                } else {
                    response = gson.toJson(manager.getSubtasks());
                    responseCode = 200;
                }
            } else {
                if (query != null) {
                    int numb = Integer.parseInt(query.substring(3));
                    Epic main = manager.getEpics().get(numb);
                    if (main != null) {
                        ArrayList<Subtask> subtasks = new ArrayList<>();
                        for (Integer integer : main.getIdSubtask()) {
                            if (manager.getSubtaskId(integer) != null) {
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
        protected void treatmentPost(HttpExchange httpExchange) throws IOException {
            Headers requestHeaders = httpExchange.getRequestHeaders();
            List<String> contentTypeValues = requestHeaders.get("Content-type");
            if ((contentTypeValues != null) && (contentTypeValues.contains("application/json"))) {
                String body = readText(httpExchange);
                if (!body.isEmpty()) {
                    try {
                        Subtask subtaskPost = gson.fromJson(body, Subtask.class);
                        if (subtaskPost.getId() != null) {
                            if (manager.getSubtasks().containsKey(subtaskPost.getId())) {
                                manager.updateSubtask(subtaskPost);
                            } else {
                                responseCode = 404;
                                return;
                            }
                        } else {
                            manager.moveSubtask(subtaskPost);
                        }
                        responseCode = 201;
                    } catch (JsonSyntaxException e) {
                        responseCode = 400;
                    }
                } else {
                    responseCode = 400;
                    return;
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
                    treatmentPost(httpExchange);
                    break;
                case "DELETE":
                    treatmentDelete(httpExchange);
                    break;
                default:
                    responseCode = 405;
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            writeText(httpExchange, response);
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
        protected void treatmentPost(HttpExchange httpExchange) throws IOException {
            Headers requestHeaders = httpExchange.getRequestHeaders();
            List<String> contentTypeValues = requestHeaders.get("Content-type");
            if ((contentTypeValues != null) && (contentTypeValues.contains("application/json"))) {
                String body = readText(httpExchange);
                if (!body.isEmpty()) {
                    try {
                        Epic epic = gson.fromJson(body, Epic.class);
                        if (epic.getId() != null) {
                            if (manager.getEpics().containsKey(epic.getId())) {
                                manager.updateEpic(epic);
                            } else {
                                responseCode = 404;
                                return;
                            }
                        } else {
                            manager.moveEpic(epic);
                        }
                        responseCode = 201;
                    } catch (JsonSyntaxException e) {
                        responseCode = 400;
                    }
                } else {
                    responseCode = 400;
                    return;
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
            String response = "";
            String method = httpExchange.getRequestMethod();
            int responseCode;
            switch (method) {
                case "GET":
                    response = gson.toJson(manager.getHistory());
                    responseCode = 200;
                    break;
                default:
                    responseCode = 405;
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            writeText(httpExchange, response);
        }
    }

    public class AllTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response ="";
            String method = httpExchange.getRequestMethod();
            int responseCode;
            switch (method) {
                case "GET":
                    response = gson.toJson(manager.getPrioritizedTasks());
                    responseCode = 200;
                    break;
                default:
                    responseCode = 405;
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            writeText(httpExchange, response);
        }
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    private void writeText(HttpExchange h, String response) throws IOException {
        try (OutputStream os = h.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
