package kanban.manager.file;

import kanban.manager.HistoryManager;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TasksTypes;

import java.io.File;
import java.util.List;

public class CSVFormatHandler {

    public static File getFile() {
        return new File("src\\kanban\\resources\\allTasks.csv");
    }

    public static String getCsv() {
        return "id,type,name,status,description,epic\n";
    }

    public static String historyToString(HistoryManager historyManager) {
        StringBuilder history = new StringBuilder();
        List<Task> historyList = historyManager.getHistory();
        for (Task task : historyList) {
            history.append(task.getId());
            history.append(",");
        }
        if (history.length() != 0) {
            return history.delete(history.length() - 1, history.length()).toString();
        }
        return history.toString();
    }

    public static String toString(Task task) {
        if (task.getTaskType().equals(TasksTypes.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            return subtask.getId() + "," +
                    subtask.getTaskType() + "," +
                    subtask.getName() + "," +
                    subtask.getStatus() + "," +
                    subtask.getDescription() + "," +
                    subtask.getEpicId();
        }
        return task.getId() + "," +
                task.getTaskType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription();
    }
}
