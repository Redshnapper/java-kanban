package kanban.manager;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TasksManager {
    private static long id = 0;
    private final HashMap<Long, Task> taskMap = new HashMap<>();
    private final HashMap<Long, Subtask> subtaskMap = new HashMap<>();
    private final HashMap<Long, Epic> epicMap = new HashMap<>();

    public ArrayList<Subtask> getEpicSubtasks(long epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Subtask subtask : subtaskMap.values()) {
            if (subtask.getEpicId() == epicId) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    public long addNewTask(Task task) {
        id++;
        task.setId(id);
        createTask(task);
        return id;
    }

    public long addNewSubtask(Subtask subtask) {
        id++;
        subtask.setId(id);
        createSubtask(subtask);
        Epic epic = epicMap.get(subtask.getEpicId());
        epic.setSubtaskId(id);
        updateEpicStatus(epic.getId());
        return id;
    }

    public long addNewEpic(Epic epic) {
        id++;
        epic.setId(id);
        createEpic(epic);
        return id;
    }

    public Task createTask(Task task) {
        taskMap.put(task.getId(),task);
        return task;
    }

    public Task createSubtask(Subtask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
        return subtask;
    }

    public Epic createEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epicMap.values());
    }

    public void removeTasks() {
        taskMap.clear();
    }

    public void removeSubtasks() {
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.clearSubsId();
            updateEpicStatus(epic.getId());
        }
    }

    public void removeEpics() {
        epicMap.clear();
        subtaskMap.clear();
    }

    public Task getTaskById(long id) {
        if (taskMap.containsKey(id)) {
            Task task = taskMap.get(id);
            return task;
        } else {
            System.out.println("Задачи с таким ID нет");
            return null;
        }
    }

    public Subtask getSubtaskById(long id) {
        if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.get(id);
            return subtask;
        } else {
            System.out.println("Подзадачи с таким ID нет");
            return null;
        }
    }

    public Epic getEpicById(long id) {
        if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            return epic;
        } else {
            System.out.println("Эпика с таким ID нет");
            return null;
        }
    }

    public void updateTask(Task task) {
        final long id = task.getId();
        final Task savedTask = taskMap.get(id);
        if (savedTask == null) {
            return;
        }
        taskMap.put(id, task);
    }

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

    public void updateEpic(Epic epic) {
        final long id = epic.getId();
        final Epic savedEpic = epicMap.get(id);
        if (savedEpic == null) {
            return;
        }
        epicMap.put(id, epic);
    }

    public void deleteTask(long id) {
        if (!taskMap.containsKey(id)) {
            System.out.println("Задачи с таким ID нет");
            return;
        }
        taskMap.remove(id);
    }

    public void deleteSubtask(long id) {
        Subtask subtask = subtaskMap.remove(id);

        if (subtask == null) {
            return;
        }
        Epic epic = epicMap.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

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

    public ArrayList<Long> getSubtasksByEpic(long id) {
        Epic epic = epicMap.get(id);
        return new ArrayList<>(epic.getSubtaskId());
    }

    private void updateEpicStatus(long epicId) {
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
