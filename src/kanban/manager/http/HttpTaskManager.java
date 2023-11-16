package kanban.manager.http;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import kanban.GsonUtils;
import kanban.LocalDateTimeAdapter;
import kanban.client.KVTaskClient;
import kanban.manager.exception.HttpManagerStartException;
import kanban.manager.file.FileBackedTasksManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TasksTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private static final Gson gson = GsonUtils.getGson();
    private final String TASKS_KEY = "tasks";
    private final String SUBTASKS_KEY = "subtasks";
    private final String HISTORY_KEY = "history";
    private final String EPICS_KEY = "epics";
    private final KVTaskClient client;


    public HttpTaskManager(String url, boolean isLoad) {
        super();
        try {
            client = new KVTaskClient(url);
        } catch (RuntimeException e) {
            throw new HttpManagerStartException("Ошибка при инициализации клиента: \n" + e.getMessage());
        }

        if (isLoad) {
            load();
        }
    }

    protected void addTasks(List<? extends Task> tasks) {
        long generatorId = 0;
        for (Task task : tasks) {
            final long id = task.getId();
            if (id > generatorId) {
                generatorId = id;
            }
            TasksTypes type = task.getTaskType();
            if (type == TasksTypes.TASK) {
                this.taskMap.put(id, task);
                prioritizedTasks.add(task);
            } else if (type == TasksTypes.SUBTASK) {
                subtaskMap.put(id, (Subtask) task);
                prioritizedTasks.add(task);
            } else if (type == TasksTypes.EPIC) {
                epicMap.put(id, (Epic) task);
            }
        }

    }

    public void load() {
        JsonElement jsonTasks = JsonParser.parseString(client.load(TASKS_KEY));
        ArrayList<Task> tasks = gson.fromJson(client.load(TASKS_KEY), new TypeToken<ArrayList<Task>>() {
        }.getType());
        addTasks(tasks);
        ArrayList<Task> epics = gson.fromJson(client.load(EPICS_KEY), new TypeToken<ArrayList<Epic>>() {
        }.getType());
        addTasks(epics);
        ArrayList<Task> subtasks = gson.fromJson(client.load(SUBTASKS_KEY), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        addTasks(subtasks);

        JsonElement jsonHistoryList = JsonParser.parseString(client.load(HISTORY_KEY));
        if (!jsonHistoryList.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                long taskId = jsonTaskId.getAsLong();
                Task task = this.taskMap.get(taskId);
                if (task == null)
                    task = this.epicMap.get(taskId);
                if (task == null)
                    task = this.subtaskMap.get(taskId);
                this.historyManager.add(task);
            }
        }
    }

    @Override
    public void save() {
        client.put(TASKS_KEY, gson.toJson(taskMap.values()));
        client.put(SUBTASKS_KEY, gson.toJson(subtaskMap.values()));
        client.put(EPICS_KEY, gson.toJson(epicMap.values()));
        client.put(HISTORY_KEY, gson.toJson(this.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }
}
