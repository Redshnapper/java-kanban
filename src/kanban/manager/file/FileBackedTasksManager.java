package kanban.manager.file;

import kanban.manager.InMemoryTaskManager;
import kanban.manager.Managers;
import kanban.manager.TasksManager;
import kanban.manager.exception.ManagerSaveException;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskStatuses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final File file = CSVFormatHandler.getFile();

    public static void main(String[] args) {
        TasksManager manager = Managers.getDefaultFile();
        Task task1 = new Task("Task #1", "Task1 description", TaskStatuses.NEW);
        Task task2 = new Task("Task #2", "Task2 description", TaskStatuses.IN_PROGRESS);
        final long taskId1 = manager.addNewTask(task1);
        final long taskId2 = manager.addNewTask(task2);
        Epic epic1 = new Epic("Epic #1", "Epic1 description", TaskStatuses.NEW);
        Epic epic2 = new Epic("Epic #2", "Epic2 description", TaskStatuses.NEW);
        final long epicId1 = manager.addNewEpic(epic1);
        final long epicId2 = manager.addNewEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask #1-1", "Subtask1 description", TaskStatuses.NEW, epicId1);
        Subtask subtask2 = new Subtask("Subtask #2-1", "Subtask2 description", TaskStatuses.DONE, epicId1);
        Subtask subtask3 = new Subtask("Subtask #3-2", "Subtask3 description", TaskStatuses.DONE, epicId2);
        final long subtaskId1 = manager.addNewSubtask(subtask1);
        final long subtaskId2 = manager.addNewSubtask(subtask2);
        final long subtaskId3 = manager.addNewSubtask(subtask3);

        manager.getSubtaskById(subtaskId1); // 5
        manager.getSubtaskById(subtaskId2); // 6
        manager.getSubtaskById(subtaskId3); // 7
        manager.getTaskById(taskId2); // 2
        manager.getTaskById(taskId1); // 1
        manager.getEpicById(epicId1); // 3
        manager.getEpicById(epicId2); // 4


        FileBackedTasksManager fromFileManager = loadFromFile(file);

        System.out.printf("%-1.30s", "история из файла -------> ");
        for (Task task : fromFileManager.getHistory()) {
            System.out.printf("%s", task.getId());
        }

        System.out.printf("%n%-1.30s", "история из менеджера ---> ");
        for (Task task : manager.getHistory()) {
            System.out.printf("%s", task.getId());
        }

    }

    private static void printAllTasks(TasksManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);
            System.out.println("--> Подзадачи эпика:");
            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("--------");
    }

    static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fromFileManager = new FileBackedTasksManager();
        String content;
        try {
            content = Files.readString(Path.of(file.getPath()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }
        String[] lines = content.split("\r?\n");

        List<Long> history = historyFromString(lines[lines.length - 1]);

        if (lines.length > 1) {
            long generatorId = 0;
            for (int i = 1; i < lines.length - 2; i++) {
                Task task = fromString(lines[i]);
                final long id = task.getId();
                if (id > generatorId) {
                    generatorId = id;
                }
                switch (task.getTaskType()) {
                    case TASK:
                        fromFileManager.createTask(task);
                        continue;
                    case SUBTASK:
                        fromFileManager.createSubtask((Subtask) task);
                        continue;
                    case EPIC:
                        fromFileManager.createEpic((Epic) task);
                        continue;
                    default:
                        System.out.println("Что-то пошло не так(");
                }
            }
            for (Map.Entry<Long, Subtask> e : fromFileManager.getSubtaskMap().entrySet()) {
                final Subtask subtask = e.getValue();
                Epic epic = fromFileManager.getEpicMap().get(subtask.getEpicId());
                epic.setSubtaskId(subtask.getId());
            }
            setId(generatorId);
        } else {
            return null;
        }

        for (Long taskId : history) {
            if (fromFileManager.getTaskMap().containsKey(taskId)) {
                fromFileManager.getTaskById(taskId);
            }
            if (fromFileManager.getSubtaskMap().containsKey(taskId)) {
                fromFileManager.getSubtaskById(taskId);
            }
            if (fromFileManager.getEpicMap().containsKey(taskId)) {
                fromFileManager.getEpicById(taskId);
            }
        }
        return fromFileManager;
    }

    static List<Long> historyFromString(String value) {
        List<Long> history = new ArrayList<>();
        for (String taskId : value.split(",")) {
            history.add(Long.parseLong(taskId));
        }
        return history;
    }

    static Task fromString(String value) {
        String[] parts = value.split(",");
        TaskStatuses status = convertStatusType(parts[3]);
        Task task;
        switch (parts[1]) {
            case "TASK":
                task = new Task(parts[2], parts[4], status);
                task.setId(Integer.parseInt(parts[0]));
                return task;
            case "SUBTASK":
                task = new Subtask(parts[2], parts[4], status, Integer.parseInt(parts[5]));
                task.setId(Integer.parseInt(parts[0]));
                return task;
            case "EPIC":
                task = new Epic(parts[2], parts[4], status);
                task.setId(Integer.parseInt(parts[0]));
                return task;
            default:
                System.out.println("Что-то пошло не так!");
                return null;
        }
    }

    static TaskStatuses convertStatusType(String status) {
        switch (status) {
            case "NEW":
                return TaskStatuses.NEW;
            case "IN_PROGRESS":
                return TaskStatuses.IN_PROGRESS;
            case "DONE":
                return TaskStatuses.DONE;
            default:
                System.out.println("Что-то пошло не так..");
                return null;
        }
    }

    void save() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(CSVFormatHandler.getFile()))) {
            fileWriter.write(CSVFormatHandler.getCsv());
            for (Task task : getTasks()) {
                fileWriter.write(CSVFormatHandler.toString(task) + '\n');
            }
            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(CSVFormatHandler.toString(subtask) + '\n');
            }
            for (Epic epic : getEpics()) {
                fileWriter.write(CSVFormatHandler.toString(epic) + '\n');
            }
            fileWriter.write('\n');
            fileWriter.write(CSVFormatHandler.historyToString(super.getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка во время сохранения файла");
        }
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public List<Subtask> getEpicSubtasks(long epicId) {
        return super.getEpicSubtasks(epicId);
    }

    @Override
    public long addNewTask(Task task) {
        long taskId = super.addNewTask(task);
        save();
        return taskId;
    }

    @Override
    public long addNewSubtask(Subtask subtask) {
        long subtaskId = super.addNewSubtask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public long addNewEpic(Epic epic) {
        long epicId = super.addNewEpic(epic);
        save();
        return epicId;
    }

    @Override
    public Task createTask(Task task) {
        return super.createTask(task);
    }

    @Override
    public Task createSubtask(Subtask subtask) {
        return super.createSubtask(subtask);
    }

    @Override
    public Epic createEpic(Epic epic) {
        return super.createEpic(epic);
    }

    @Override
    public List<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public List<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public Task getTaskById(long id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(long id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
    }

    @Override
    public void deleteTask(long id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(long id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(long id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public List<Long> getSubtasksByEpic(long id) {
        return super.getSubtasksByEpic(id);
    }

    @Override
    public void updateEpicStatus(long epicId) {
        super.updateEpicStatus(epicId);
    }

}

