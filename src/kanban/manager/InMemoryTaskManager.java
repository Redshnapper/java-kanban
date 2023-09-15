package kanban.manager;

import kanban.manager.exception.ValidateTaskTimeException;
import kanban.manager.file.StartDateComparator;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskStatuses;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TasksManager {
    private static long id = 0;
    private final Map<Long, Task> taskMap = new HashMap<>();
    private final Map<Long, Subtask> subtaskMap = new HashMap<>();
    private final Map<Long, Epic> epicMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final StartDateComparator comparator = new StartDateComparator();
    private final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private boolean validate(Task task) {
        LocalDateTime startDate = task.getStartDate();
        if (startDate == null) return false;
        LocalDateTime endDate = task.getEndDate();
        if (prioritizedTasks.isEmpty()) return false;

        boolean check = prioritizedTasks.stream()
                .anyMatch(t -> t.getStartDate().isBefore(endDate) && t.getEndDate().isAfter(startDate)
                        || t.getStartDate().isEqual(endDate) || t.getEndDate().isEqual(startDate));
        if (check) {
            throw new ValidateTaskTimeException("Валидация не пройдена, задачи пересекаются по времени!");
        }
        return false;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Subtask> getEpicSubtasks(long epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        if (epicMap.containsKey(epicId)) {
            for (Subtask subtask : subtaskMap.values()) {
                if (subtask.getEpicId() == epicId) {
                    epicSubtasks.add(subtask);
                }
            }
            return new ArrayList<>(epicSubtasks);
        } else return null;

    }

    @Override
    public long addNewTask(Task task) {
        id++;
        task.setId(id);
        createTask(task);
        validate(task);
        if (task.getStartDate() == null) {
            task.setStartDate(LocalDateTime.of(3000, 1, 1, 0, 0, 0));
        }
        prioritizedTasks.add(task);
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
        validate(subtask);
        if (subtask.getStartDate() == null) {
            subtask.setStartDate(LocalDateTime.of(3000, 1, 1, 0, 0, 0));
        }
        epic.calculateTime(subtaskMap);
        prioritizedTasks.add(subtask);
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
        taskMap.put(task.getId(), task);
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

    public List<Long> getTaskIdList() {
        return new ArrayList<>(taskMap.keySet());
    }

    public List<Long> getSubtaskIdList() {
        return new ArrayList<>(subtaskMap.keySet());
    }

    public List<Long> getEpicIdList() {
        return new ArrayList<>(epicMap.keySet());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicMap.values());
    }


    @Override
    public void removeTasks() {
        for (Long id : taskMap.keySet()) {
            historyManager.remove(id);
        }
        taskMap.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Long id : subtaskMap.keySet()) {
            historyManager.remove(id);
        }
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.clearSubsId();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void removeEpics() {
        for (Long id : subtaskMap.keySet()) {
            historyManager.remove(id);
        }
        for (Long id : epicMap.keySet()) {
            historyManager.remove(id);
        }
        epicMap.clear();
        subtaskMap.clear();
    }

    @Override
    public Task getTaskById(long id) {
        Task task = taskMap.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;

    }

    @Override
    public Subtask getSubtaskById(long id) {
        Subtask subtask = subtaskMap.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = epicMap.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
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
        Task task = taskMap.remove(id);
        if (task == null) {
            return;
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(long id) {
        Subtask subtask = subtaskMap.remove(id);
        if (subtask == null) {
            return;
        }
        historyManager.remove(id);
        Epic epic = epicMap.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void deleteEpic(long id) {
        List<Long> subtasksIdToRemove = new ArrayList<>();

        if (epicMap.containsKey(id)) {
            for (Subtask value : subtaskMap.values()) {
                if (value.getEpicId() == id) {
                    subtasksIdToRemove.add(value.getId());
                }
            }
            for (Long idToRemove : subtasksIdToRemove) {
                subtaskMap.remove(idToRemove);
                historyManager.remove(idToRemove);
            }
            epicMap.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Long> getSubtasksByEpic(long id) {
        Epic epic = epicMap.get(id);
        return new ArrayList<>(epic.getSubtaskId());
    }

    @Override
    public void updateEpicStatus(long epicId) {
        Epic epic = epicMap.get(epicId);
        List<Long> subs = epic.getSubtaskId();
        if (subs.isEmpty()) {
            epic.setStatus(TaskStatuses.NEW);
            return;
        }
        TaskStatuses status = null;
        for (long id : subs) {
            final Subtask subtask = subtaskMap.get(id);
            if (status == null && !subtaskMap.isEmpty() && subtask != null) {
                status = subtask.getStatus();
                continue;
            }
            if (!subtaskMap.isEmpty() && status.equals(subtask.getStatus())
                    && !status.equals(TaskStatuses.IN_PROGRESS)) {
                continue;
            }
            epic.setStatus(TaskStatuses.IN_PROGRESS);
            return;
        }
        epic.setStatus(status);
    }

    public static void setId(long id) {
        InMemoryTaskManager.id = id;
    }

    @Override
    public long getId() {
        return id;
    }
}
