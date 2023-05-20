package kanban.manager;

import kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> tasksHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (tasksHistory.size() < 10) {
            tasksHistory.add(task);
        }
        tasksHistory.remove(0);
        tasksHistory.add(task);

    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }
}
