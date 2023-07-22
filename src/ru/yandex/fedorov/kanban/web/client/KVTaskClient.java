package ru.yandex.fedorov.kanban.web.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final HttpClient server;
    private final String URL;
    private final String API_TOKEN;

    public KVTaskClient(String URL) throws IOException, InterruptedException {
        server = HttpClient.newHttpClient();
        this.URL = URL;
        API_TOKEN = registerApiToken(URL);
    }

    private String registerApiToken(String URL) throws IOException, InterruptedException {
        URI url = URI.create(String.format("%s/register", URL));
        HttpRequest request = HttpRequest.newBuilder()
                                        .GET()
                                        .uri(url)
                                        .version(HttpClient.Version.HTTP_1_1)
                                        .build();
        HttpResponse<String> response = server.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI url = URI.create(String.format("%s/save/%s/?API_TOKEN=%s", URL, key, API_TOKEN));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        server.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws IOException, InterruptedException {
        URI url = URI.create(String.format("%s/load/%s/?API_TOKEN=%s", URL, key, API_TOKEN));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = server.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
