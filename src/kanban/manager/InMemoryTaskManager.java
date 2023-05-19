package kanban.manager;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TasksManager {
    private static long id = 0;
    private final HashMap<Long, Task> taskMap = new HashMap<>();
    private final HashMap<Long, Subtask> subtaskMap = new HashMap<>();
    private final HashMap<Long, Epic> epicMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(long epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Subtask subtask : subtaskMap.values()) {
            if (subtask.getEpicId() == epicId) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    @Override
    public long addNewTask(Task task) {
        id++;
        task.setId(id);
        createTask(task);
        return id;
    }

    @Override
    public long addNewSubtask(Subtask subtask) {
        id++;
        subtask.setId(id);
        createSubtask(subtask);
        Epic epic = epicMap.get(subtask.getEpicId());
        epic.setSubtaskId(id);
        updateEpicStatus(epic.getId());
        return id;
    }

    @Override
    public long addNewEpic(Epic epic) {
        id++;
        epic.setId(id);
        createEpic(epic);
        return id;
    }

    @Override
    public Task createTask(Task task) {
        taskMap.put(task.getId(),task);
        return task;
    }

    @Override
    public Task createSubtask(Subtask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public void removeTasks() {
        taskMap.clear();
    }

    @Override
    public void removeSubtasks() {
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.clearSubsId();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void removeEpics() {
        epicMap.clear();
        subtaskMap.clear();
    }

    @Override
    public Task getTaskById(long id) {
        if (taskMap.containsKey(id)) {
            Task task = taskMap.get(id);
            historyManager.add(task);
            return task;
        } else {
            System.out.println("Задачи с таким ID нет");
            return null;
        }

    }

    @Override
    public Subtask getSubtaskById(long id) {
        if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.get(id);
            historyManager.add(subtask);
            return subtask;
        } else {
            System.out.println("Подзадачи с таким ID нет");
            return null;
        }
    }

    @Override
    public Epic getEpicById(long id) {
        if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            historyManager.add(epic);
            return epic;
        } else {
            System.out.println("Эпика с таким ID нет");
            return null;
        }
    }

    @Override
    public void updateTask(Task task) {
        final long id = task.getId();
        final Task savedTask = taskMap.get(id);
        if (savedTask == null) {
            return;
        }
        taskMap.put(id, task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final long id = subtask.getId();
        final long epicId = subtask.getEpicId();
        final Subtask savedSubtask = subtaskMap.get(id);
        if (savedSubtask == null) {
            return;
        }
        final Epic epic = epicMap.get(epicId);
        if (epic == null) {
            return;
        }
        subtaskMap.put(id, subtask);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void updateEpic(Epic epic) {
        final long id = epic.getId();
        final Epic savedEpic = epicMap.get(id);
        if (savedEpic == null) {
            return;
        }
        epicMap.put(id, epic);
    }

    @Override
    public void deleteTask(long id) {
        if (!taskMap.containsKey(id)) {
            System.out.println("Задачи с таким ID нет");
            return;
        }
        taskMap.remove(id);
    }

    @Override
    public void deleteSubtask(long id) {
        Subtask subtask = subtaskMap.remove(id);

        if (subtask == null) {
            return;
        }
        Epic epic = epicMap.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void deleteEpic(long id) {
        ArrayList<Long> subtasksIdToRemove = new ArrayList<>();

        if (epicMap.containsKey(id)){
            for (Subtask value : subtaskMap.values()) {
                if (value.getEpicId() == id) {
                    subtasksIdToRemove.add(value.getId());
                }
            }
            for (Long idToRemove : subtasksIdToRemove) {
                subtaskMap.remove(idToRemove);
            }
            epicMap.remove(id);
        } else {
            System.out.println("Эпика с таким ID нет");
        }
    }

    @Override
    public ArrayList<Long> getSubtasksByEpic(long id) {
        Epic epic = epicMap.get(id);
        return new ArrayList<>(epic.getSubtaskId());
    }

    @Override
    public void updateEpicStatus(long epicId) {
        Epic epic = epicMap.get(epicId);
        ArrayList<Long> subs = epic.getSubtaskId();
        if (subs.isEmpty()) {
            epic.setStatus("NEW");
            return;
        }
        String status = null;
        for (long id : subs) {
            final Subtask subtask = subtaskMap.get(id);
            if (status == null && !subtaskMap.isEmpty() && subtask != null) {
                status = subtask.getStatus();
                continue;
            }
            if (!subtaskMap.isEmpty() && status.equals(subtask.getStatus())
                    && !status.equals("IN_PROGRESS")) {
                continue;
            }
            epic.setStatus("IN_PROGRESS");
            return;
        }
        epic.setStatus(status);
    }
}
