package com.yandex.taskmanager.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String URL;
    private String token;

    public KVTaskClient(String url) {
        this.URL = url;
        register(url);

    }

    public void put(String key, String json) {
        URI saver = URI.create(URL + "save/" + key + "?" + "API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .POST(HttpRequest.BodyPublishers.ofString(json))  // указываем HTTP-метод запроса
                .header("content-type", "application/json")
                .uri(saver) // указываем адрес ресурса
                .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                .build(); // заканчиваем настройку и создаём ("строим") http-запрос
        // HTTP-клиент с настройками по умолчанию
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() == 200) {
                System.out.println(response.statusCode());
            } else {
                throw new StatusCodeException("Вернулся не подходящий код ответа");
            }
        } catch (InterruptedException | IOException e) {
            throw new StatusCodeException("Вернулся не подходящий код ответа");
        }

    }

    public String load(String key) {
        URI saver = URI.create(URL + "load/" + key + "?" + "API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .GET()  // указываем HTTP-метод запроса
                .uri(saver) // указываем адрес ресурса
                .header("content-type", "application/json")
                .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                .build(); // заканчиваем настройку и создаём ("строим") http-запрос
        // HTTP-клиент с настройками по умолчанию
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new StatusCodeException("Вернулся не подходящий код ответа");
            }
        } catch (InterruptedException | IOException e) {
            throw new StatusCodeException("Вернулся не подходящий код ответа");
        }
    }

    private String register(String url) {
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .GET()    // указываем HTTP-метод запроса
                .uri(URI.create(url + "register")) // указываем адрес ресурса
                .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                .build(); // заканчиваем настройку и создаём ("строим") http-запрос
        // HTTP-клиент с настройками по умолчанию
        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return token = response.body();
            } else {
                throw new StatusCodeException("Вернулся не подходящий код ответа");
            }
        } catch (InterruptedException | IOException e) {
            throw new StatusCodeException("Вернулся не подходящий код ответа");
        }
    }
}
