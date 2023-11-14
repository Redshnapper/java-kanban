package kanban.server;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import kanban.LocalDateTimeAdapter;
import kanban.manager.Managers;
import kanban.manager.exception.ValidateTaskTimeException;
import kanban.manager.file.CSVFormatHandler;
import kanban.manager.file.FileBackedTasksManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskStatuses;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Handlers {
    private FileBackedTasksManager fileManager = Managers.getDefaultFile();
    private final Gson gson;

    public Handlers() {
        File file = CSVFormatHandler.getFile();
        this.fileManager = fileManager.loadFromFile(file);
        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public TaskStatuses statusFromString(String status) {
        switch (status) {
            case "NEW":
                return TaskStatuses.NEW;
            case "IN_PROGRESS":
                return TaskStatuses.IN_PROGRESS;
            case "DONE":
                return TaskStatuses.DONE;
            default:
                return null;
        }
    }

    public LocalDateTime dateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    public void updateOrAddNewTaskCheck(long id, Task task, HttpExchange exchange) throws IOException {
        long newId;
        if (id == 0) {
            try {
                fileManager.validate(task);
                newId = fileManager.addNewTask(task);
                sendMessage(exchange, "Создана задача с id " + newId);
            } catch (ValidateTaskTimeException e) {
                sendErrorText(exchange, e.getMessage());
            }
        } else {
            task.setId(id);
            fileManager.updateTask(task);
            sendMessage(exchange, "Задача с id " + id + " обновлена");
        }
    }

    public void updateOrAddNewSubtaskCheck(long id, Subtask subtask, HttpExchange exchange) throws IOException {
        long newId;
        if (id == 0) {
            try {
                fileManager.validate(subtask);
                newId = fileManager.addNewSubtask(subtask);
                sendMessage(exchange, "Создана подзадача с id " + newId);
            } catch (ValidateTaskTimeException e) {
                sendErrorText(exchange, e.getMessage());
            }
        } else {
            subtask.setId(id);
            fileManager.updateSubtask(subtask);
            sendMessage(exchange, "Подзадача с id " + id + " обновлена");
        }
    }

    public void updateOrAddNewEpicCheck(long id, Epic epic, HttpExchange exchange) throws IOException {
        long newId;
        if (id == 0) {
            newId = fileManager.addNewEpic(epic);
            sendMessage(exchange, "Создан эпик с id " + newId);
        } else {
            epic.setId(id);
            fileManager.updateEpic(epic);
            sendMessage(exchange, "Эпик с id " + id + " обновлен");
        }
    }

    public void allFromJson(JsonObject object, HttpExchange exchange, String type) throws IOException {
        if (object.size() == 3 && object.has("id") && object.has("name")
                && object.has("description") && type.equals("epic")) {

            Epic epic = new Epic(object.get("name").getAsString(), object.get("description").getAsString());

            updateOrAddNewEpicCheck(object.get("id").getAsLong(), epic, exchange); //----------
        }
        if (object.size() == 4 && object.has("id") && object.has("name")
                && object.has("description") && object.has("status")
                && type.equals("task")) {

            Task task = new Task(object.get("name").getAsString(), object.get("description").getAsString(),
                    statusFromString(object.get("status").getAsString()));

            updateOrAddNewTaskCheck(object.get("id").getAsLong(), task, exchange); //----------
        }
        if (object.size() == 5 && object.has("id") && object.has("name")
                && object.has("description") && object.has("status")
                && object.has("epicId") && type.equals("subtask")) {

            Subtask subtask = new Subtask(object.get("name").getAsString(), object.get("description").getAsString(),
                    statusFromString(object.get("status").getAsString()), object.get("epicId").getAsInt());

            updateOrAddNewSubtaskCheck(object.get("id").getAsLong(), subtask, exchange); //----------
        }
        if (object.size() == 6 && object.has("id") && object.has("name")
                && object.has("description") && object.has("status")
                && object.has("startDate") && object.has("duration")
                && type.equals("task")) {

            Task task = new Task(object.get("name").getAsString(), object.get("description").getAsString(),
                    statusFromString(object.get("status").getAsString()),
                    dateFromString(object.get("startDate").getAsString()), object.get("duration").getAsInt());

            updateOrAddNewTaskCheck(object.get("id").getAsLong(), task, exchange); //----------
        }
        if (object.size() == 7 && object.has("id") && object.has("name")
                && object.has("description") && object.has("status")
                && object.has("epicId") && object.has("startDate")
                && object.has("duration") && type.equals("subtask")) {

            Subtask subtask = new Subtask(object.get("name").getAsString(), object.get("description").getAsString(),
                    statusFromString(object.get("status").getAsString()),
                    dateFromString(object.get("startDate").getAsString()), object.get("duration").getAsInt(),
                    object.get("epicId").getAsInt());

            updateOrAddNewSubtaskCheck(object.get("id").getAsLong(), subtask, exchange); //----------
        }
    }

    public void postTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes());
        JsonElement jsonElement = JsonParser.parseString(body);

        if (!jsonElement.isJsonObject()) {
            sendMessage(exchange, "Переданный объект не является JSON");
            return;
        }
        String type = exchange.getRequestURI().getPath().split("/")[2];
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        allFromJson(jsonObject, exchange, type);


    }


    public void getTasks(HttpExchange exchange) throws IOException {
        String[] split = exchange.getRequestURI().getPath().split("/");
        String response;
        switch (split[2]) {
            case "task":
                response = gson.toJson(fileManager.getTasks());
                sendText(exchange, response);
                return;
            case "subtask":
                response = gson.toJson(fileManager.getSubtasks());
                sendText(exchange, response);
                return;
            case "epic":
                response = gson.toJson(fileManager.getEpics());
                sendText(exchange, response);
                return;
            default:
                response = "Эндпоинт указан не верно!";
                sendErrorText(exchange, response);
        }
    }

    public void getPrioritized(HttpExchange exchange) throws IOException {
        String response = gson.toJson(fileManager.getPrioritizedTasks());
        sendText(exchange, response);

    }

    public void getHistory(HttpExchange exchange) throws IOException {
        String response = gson.toJson(fileManager.getHistory());
        sendText(exchange, response);
    }

    public void deleteTaskById(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery().replaceFirst("id=", "");
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(query);
        String response;
        switch (pathParts[2]) {
            case "task":
                fileManager.deleteTask(id);
                response = "Задача с id " + id + " удалена";
                sendMessage(exchange, response);
            case "epic":
                fileManager.deleteEpic(id);
                response = "Эпик с id " + id + " удален";
                sendMessage(exchange, response);
            case "subtask":
                fileManager.deleteSubtask(id);
                response = "Подзадача с id " + id + " удалена";
                sendMessage(exchange, response);
            default:
                response = "Ожидался тип задачи, а введено " + pathParts[2];
                sendErrorText(exchange, response);
        }
    }

    public void deleteTasks(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        switch (pathParts[2]) {
            case "task":
                fileManager.removeTasks();
                sendMessage(exchange, "Все задачи удалены");
            case "subtask":
                fileManager.removeSubtasks();
                sendMessage(exchange, "Все подзадачи удалены");
            case "epic":
                fileManager.removeEpics();
                sendMessage(exchange, "Все эпики удалены");
        }
    }

    public void getTaskById(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery().replaceFirst("id=", "");
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(query);
        String response;
        switch (pathParts[2]) {
            case "task":
                response = gson.toJson(fileManager.getTaskById(id));
                sendText(exchange, response);
            case "epic":
                response = gson.toJson(fileManager.getEpicById(id));
                sendText(exchange, response);
            case "subtask":
                response = gson.toJson(fileManager.getSubtaskById(id));
                sendText(exchange, response);
            default:
                response = "Ожидался тип задачи, а введено " + pathParts[2];
                sendErrorText(exchange, response);
        }

    }

    public void sendText(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, response.length());

        byte[] bytes = response.getBytes();
        httpExchange.getResponseBody().write(bytes);
    }

    public void sendMessage(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);

        byte[] bytes = response.getBytes(Charset.defaultCharset());
        httpExchange.getResponseBody().write(bytes);
    }

    public void sendErrorText(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(405, 0);

        byte[] bytes = response.getBytes(Charset.defaultCharset());
        httpExchange.getResponseBody().write(bytes);
    }


}
