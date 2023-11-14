package kanban.manager.history;

import kanban.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(long id);

    List<Task> getHistory();
}
