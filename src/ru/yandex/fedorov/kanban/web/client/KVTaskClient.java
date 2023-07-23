package ru.yandex.fedorov.kanban.web.client;

import ru.yandex.fedorov.kanban.web.exception.ClientLoadException;
import ru.yandex.fedorov.kanban.web.exception.ClientRegistrationException;
import ru.yandex.fedorov.kanban.web.exception.ClientSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final HttpClient client;
    private final String URL;
    private final String API_TOKEN;

    public KVTaskClient(String URL) {
        client = HttpClient.newHttpClient();
        this.URL = URL;
        API_TOKEN = registerApiToken(URL);
    }

    private String registerApiToken(String URL) {
        try {
            URI url = URI.create(String.format("%s/register", URL));
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(url)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ClientRegistrationException(
                    String.format("Произошла ошибка при регистрации клиента, код состояния: %d", response.statusCode())
                );
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ClientRegistrationException("Произошла ошибка при регистрации клиента", e);
        }
    }

    public void put(String key, String json) {
        try {
            URI url = URI.create(String.format("%s/save/%s/?API_TOKEN=%s", URL, key, API_TOKEN));
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(url)
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ClientSaveException(
                    String.format("Произошла ошибка при сохранении, код состояния: %d", response.statusCode())
                );
            }
        } catch (IOException | InterruptedException e) {
            throw new ClientSaveException("Произошла ошибка при загрузке данных на сервер", e);
        }
    }

    public String load(String key) {
        try {
            URI url = URI.create(String.format("%s/load/%s/?API_TOKEN=%s", URL, key, API_TOKEN));
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(url)
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ClientLoadException(
                    String.format("Произошла ошибка при получении данных, код состояния: %d", response.statusCode())
                );
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ClientLoadException("Произошла ошибка при получении данных с сервера", e);
        }
    }
}
