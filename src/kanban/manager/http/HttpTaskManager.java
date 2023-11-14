package kanban.manager.http;

import com.google.gson.*;
import kanban.LocalDateTimeAdapter;
import kanban.client.KVTaskClient;
import kanban.manager.exception.HttpManagerStartException;
import kanban.manager.file.FileBackedTasksManager;
import kanban.manager.memory.InMemoryTaskManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private static final Gson gson =
            new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private final String TASKS_KEY = "tasks";
    private final String SUBTASKS_KEY = "subtasks";
    private final String HISTORY_KEY = "epics";
    private final String EPICS_KEY = "history";
    private final KVTaskClient client;


    public HttpTaskManager(String url) {
        super();
        try {
            client = new KVTaskClient(url);
        } catch (RuntimeException e) {
            throw new HttpManagerStartException("Ошибка при инициализации клиента: \n" + e.getMessage());
        }
    }

    public void load() {
        long maxId = 0;
        JsonElement jsonTasks = JsonParser.parseString(client.load(TASKS_KEY));
        if (!jsonTasks.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                if(maxId < task.getId())
                    maxId = task.getId();
                this.taskMap.put(task.getId(), task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load(EPICS_KEY));
        if (!jsonEpics.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic task = gson.fromJson(jsonEpic, Epic.class);
                if(maxId < task.getId())
                    maxId = task.getId();
                this.epicMap.put(task.getId(), task);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load(SUBTASKS_KEY));
        if (!jsonSubtasks.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                Subtask task = gson.fromJson(jsonSubtask, Subtask.class);
                if(maxId < task.getId())
                    maxId = task.getId();
                Epic epic = this.epicMap.get(task.getEpicId());
                epic.addSubtaskId(task.getId());
                this.subtaskMap.put(task.getId(), task);
                this.prioritizedTasks.add(task);
                this.subtaskMap.put(task.getId(), task);
            }
        }

        id = maxId;
        JsonElement jsonHistoryList = JsonParser.parseString(client.load(HISTORY_KEY));
        if (!jsonHistoryList.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                long taskId = jsonTaskId.getAsLong();
                Task task = this.taskMap.get(taskId);
                if(task == null)
                    task = this.epicMap.get(taskId);
                if(task == null)
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
