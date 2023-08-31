package kanban.manager;

import kanban.manager.file.FileBackedTasksManager;

public final class Managers {

    private Managers() {
    }

    public static TasksManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager getDefaultFile() {
        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}