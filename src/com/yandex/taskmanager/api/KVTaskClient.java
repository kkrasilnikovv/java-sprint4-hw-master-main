package com.yandex.taskmanager.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private KVServer server;
    private final String token;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        this.url = url;
        server = new KVServer();
        server.start();
        // создаём объект, описывающий HTTP-запрос
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .GET()    // указываем HTTP-метод запроса
                .uri(URI.create(url + "register")) // указываем адрес ресурса
                .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                .build(); // заканчиваем настройку и создаём ("строим") http-запрос
        // HTTP-клиент с настройками по умолчанию
        HttpClient client = HttpClient.newHttpClient();

        // получаем стандартный обработчик тела запроса с конвертацией содержимого в строку
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        // отправляем запрос и получаем ответ от сервера
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        token = response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI saver = URI.create(url + "save/" + key + "?" + "API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .POST(HttpRequest.BodyPublishers.ofString(json))  // указываем HTTP-метод запроса
                .header("content-type", "application/json")
                .uri(saver) // указываем адрес ресурса
                .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                .build(); // заканчиваем настройку и создаём ("строим") http-запрос
        // HTTP-клиент с настройками по умолчанию
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
    }

    public String load(String key) throws IOException, InterruptedException {
        URI saver = URI.create(url + "load/" + key + "?" + "API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .GET()  // указываем HTTP-метод запроса
                .uri(saver) // указываем адрес ресурса
                .header("content-type", "application/json")
                .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                .build(); // заканчиваем настройку и создаём ("строим") http-запрос
        // HTTP-клиент с настройками по умолчанию
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
