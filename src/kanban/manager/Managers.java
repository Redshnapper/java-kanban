package kanban.manager;

import kanban.manager.file.FileBackedTasksManager;
import kanban.manager.history.HistoryManager;
import kanban.manager.history.InMemoryHistoryManager;
import kanban.manager.http.HttpTaskManager;
import kanban.manager.memory.InMemoryTaskManager;

public final class Managers {

    private Managers() {
    }

    public static TasksManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static FileBackedTasksManager getDefaultFile() {
        return new FileBackedTasksManager();
    }
    public static HttpTaskManager getDefaultHttp() {
        return new HttpTaskManager("http://localhost:" + "8078/");
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}