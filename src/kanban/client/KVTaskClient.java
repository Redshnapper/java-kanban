package kanban.client;

import kanban.manager.exception.RequestFailedException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private String url;
    private String apiToken;
    HttpClient client;


    public KVTaskClient(int port) {
        url = "http://localhost:" + port + "/";
        client = HttpClient.newHttpClient();
        apiToken = register(url);
    }

    private String register(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "register"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RequestFailedException("Не удалось сделать запрос на регистрацию, статус код"
                        + response.statusCode());
            }
        } catch (IOException | InterruptedException exception) {
            throw new RequestFailedException("Не удалось отправить запрос", exception);
        }
    }

    public void put(String key, String value) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(value))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RequestFailedException("Не удалось обработать save-запрос, статус код: "
                        + response.statusCode());
            }
        } catch (IOException | InterruptedException exception) {
            throw new RequestFailedException("Не удалось обработать save-запрос ", exception);
        }
    }

    public String load(String key) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RequestFailedException("Не удалось обработать save-запрос, статус код: "
                        + response.statusCode());
            } else {
                return response.body();
            }
        } catch (IOException | InterruptedException exception) {
            throw new RequestFailedException("Не удалось обработать save-запрос ", exception);
        }

    }
}
