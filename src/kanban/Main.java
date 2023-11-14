package kanban;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kanban.manager.Managers;
import kanban.manager.TasksManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskStatuses;
import kanban.server.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
        new KVServer().start();
        TasksManager manager = Managers.getDefaultHttp();

        Task task = new Task("Task #1", "task description", TaskStatuses.NEW);
        manager.addNewTask(task);
        Epic epic = new Epic("Epic #1", "epic description", LocalDateTime.now(), 2);
        manager.createEpic(epic);
        final long epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask #1", "subtask description", TaskStatuses.IN_PROGRESS, epicId);
        manager.addNewSubtask(subtask);

        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());
        manager.getTaskById(task.getId());

        System.out.println("Задачи");
        System.out.println(gson.toJson(manager.getTasks()));
        System.out.println("Подзадачи");
        System.out.println(gson.toJson(manager.getSubtasks()));
        System.out.println("Эпики");
        System.out.println(gson.toJson(manager.getEpics()));
        System.out.println("История");
        System.out.println(gson.toJson(manager.getHistory()));
    }
}

