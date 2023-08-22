package kanban.manager.file;

import kanban.manager.HistoryManager;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TasksTypes;

import java.util.List;

public class CSVFormatHandler {
    private static final String fileName = "C:\\Users\\Andrej\\dev\\java-kanban\\src\\kanban\\resources\\allTasks.csv";
    public static String getFileName() {
        return fileName;
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
//            return String.join(",", String.valueOf(subtask.getId()),
//                    String.valueOf(subtask.getTaskType()),
//                    subtask.getName(),
//                    String.valueOf(subtask.getStatus()),
//                    subtask.getDescription(),
//                    String.valueOf(subtask.getEpicId()));
            return subtask.getId() + "," +
                                subtask.getTaskType() + "," +
                                subtask.getName() + "," +
                                subtask.getStatus() + "," +
                                subtask.getDescription() + "," +
                                subtask.getEpicId();
        }
        return String.join(",", String.valueOf(task.getId()),
                String.valueOf(task.getTaskType()),
                task.getName(),
                String.valueOf(task.getStatus()),
                task.getDescription());
    }
}
