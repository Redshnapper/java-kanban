package test;

import kanban.manager.InMemoryTaskManager;
import kanban.manager.TasksManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

import java.util.HashMap;
import java.util.Map;

import static kanban.model.TaskStatuses.NEW;

public class TasksManagerTest<T extends TasksManager> {

    protected T taskManager;

    protected Task task;
    protected Subtask subtask;
    protected Epic epic;

    protected void init() {
        task = new Task("Task 1", "Task 1 description", NEW);
        epic = new Epic("Epic 1", "Epic 1 description", NEW);

    }


}