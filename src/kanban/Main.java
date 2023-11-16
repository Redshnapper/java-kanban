package kanban;

import com.google.gson.Gson;
import kanban.manager.Managers;
import kanban.manager.TasksManager;
import kanban.manager.http.HttpTaskManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskStatuses;
import kanban.server.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) throws IOException {
        Gson gson = GsonUtils.getGson();
        new KVServer().start();
        TasksManager manager = Managers.getDefaultHttp();

        Task task = new Task("Task #1", "task description", TaskStatuses.NEW);
        manager.addNewTask(task);
        Task task3 = new Task("Task #3", "task description", TaskStatuses.NEW);
        manager.addNewTask(task3);
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


        HttpTaskManager taskManager = new HttpTaskManager("http://localhost:" + "8078/", true);
        System.out.println("Задачи");
        System.out.println(gson.toJson(taskManager.getTasks()));
        System.out.println("Подзадачи");
        System.out.println(gson.toJson(taskManager.getSubtasks()));
        System.out.println("Эпики");
        System.out.println(gson.toJson(taskManager.getEpics()));
        System.out.println("История");
        System.out.println(gson.toJson(taskManager.getHistory()));
    }
}

