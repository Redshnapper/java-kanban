package kanban.manager;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

import java.util.List;

public interface TasksManager {

    List<Subtask> getEpicSubtasks(long epicId);

    List<Task> getHistory();

    long addNewTask(Task task) ;

    long addNewSubtask(Subtask subtask);

    long addNewEpic(Epic epic);

    Task createTask(Task task);

    Task createSubtask(Subtask subtask);

    Epic createEpic(Epic epic);

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    void removeTasks();

    void removeSubtasks();

    void removeEpics();

    Task getTaskById(long id);

    Subtask getSubtaskById(long id);

    Epic getEpicById(long id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(long id);

    void deleteSubtask(long id);

    void deleteEpic(long id);

    List<Long> getSubtasksByEpic(long id);

    void updateEpicStatus(long epicId);

    long getId();
}
