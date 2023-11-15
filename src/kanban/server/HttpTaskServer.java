package kanban.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import kanban.manager.Managers;
import kanban.manager.TasksManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final TasksManager manager;
    Gson gson;
    Handlers handlers;

    public static void main(String[] args) throws IOException {
        final HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::handler);
        this.manager = Managers.getDefaultFile();
        handlers = new Handlers();
        gson = new Gson();
    }

    private void handler(HttpExchange httpExchange) {

        try {
            System.out.println("Обработка " + httpExchange.getRequestURI() + " эндпоинта");
            Endpoint endpoint = getEndpoint(httpExchange, httpExchange.getRequestMethod());

            switch (endpoint) {
                case GET_PRIORITIZED:
                    handlers.getPrioritized(httpExchange);
                    break;
                case GET_HISTORY:
                    handlers.getHistory(httpExchange);
                    break;
                case GET_TASK_BY_ID:
                    handlers.getTaskById(httpExchange);
                    break;
                case GET_TASKS:
                    handlers.getTasks(httpExchange);
                    break;
                case DELETE_TASKS:
                    handlers.deleteTasks(httpExchange);
                    break;
                case DELETE_TASK_BY_ID:
                    handlers.deleteTaskById(httpExchange);
                    break;
                case POST_TASK:
                    handlers.postTask(httpExchange);
                    break;
            }


        } catch (Exception e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        } finally {
            httpExchange.close();
        }
    }

    public Endpoint getEndpoint(HttpExchange exchange, String requestMethod) {
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        String[] pathSplit = path.split("/");
        if (requestMethod.equals("GET")) {
            switch (path) {
                case "/tasks":
                    return Endpoint.GET_PRIORITIZED;
                case "/tasks/history":
                    return Endpoint.GET_HISTORY;
                case "/tasks/task":
                case "/tasks/subtask":
                case "/tasks/epic":
                    return Endpoint.GET_TASKS;
            }
            if (query != null) {
                return Endpoint.GET_TASK_BY_ID;
            }
        }
        if (requestMethod.equals("DELETE")) {
            switch (path) {
                case "/tasks/task":
                case "/tasks/subtask":
                case "/tasks/epic":
                    return Endpoint.DELETE_TASKS;
            }
            if (query != null) {
                return Endpoint.DELETE_TASK_BY_ID;
            }
        }
        if (requestMethod.equals("POST") && pathSplit[1].equals("tasks") && pathSplit.length == 3) {
            return Endpoint.POST_TASK;
        }
        return null;
    }

    public void start() {
        System.out.println("Запущен HTTP сервер на порту " + PORT);
        server.start();
    }

    public void stop(int delay) {
        server.stop(delay);
        System.out.println("Сервер остановлен ");
    }
}
