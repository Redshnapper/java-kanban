package kanban;

import kanban.manager.Managers;
import kanban.manager.TasksManager;
import kanban.manager.file.FileBackedTasksManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskStatuses;

import java.util.List;


public class Main {
    public static void main(String[] args) {
        TasksManager tasksManager = Managers.getDefaultFile();
        Task task1 = new Task("Task #1", "Task1 description", TaskStatuses.NEW);
        Task task2 = new Task("Task #2", "Task2 description", TaskStatuses.IN_PROGRESS);
        final long taskId1 = tasksManager.addNewTask(task1);
        final long taskId2 = tasksManager.addNewTask(task2);
        Epic epic1 = new Epic("Epic #1", "Epic1 description", TaskStatuses.NEW);
        Epic epic2 = new Epic("Epic #2", "Epic2 description", TaskStatuses.NEW);
        final long epicId1 = tasksManager.addNewEpic(epic1);
        final long epicId2 = tasksManager.addNewEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask #1-1", "Subtask1 description", TaskStatuses.NEW, epicId1);
        Subtask subtask2 = new Subtask("Subtask #2-1", "Subtask2 description", TaskStatuses.DONE, epicId1);
        Subtask subtask3 = new Subtask("Subtask #3-2", "Subtask3 description", TaskStatuses.DONE, epicId2);
        final long subtaskId1 = tasksManager.addNewSubtask(subtask1);
        final long subtaskId2 = tasksManager.addNewSubtask(subtask2);
        final long subtaskId3 = tasksManager.addNewSubtask(subtask3);

        printAllTasks(tasksManager);

        tasksManager.getSubtaskById(subtaskId1); // 5
        tasksManager.getSubtaskById(subtaskId2); // 6
        tasksManager.getSubtaskById(subtaskId3); // 7
        tasksManager.getTaskById(taskId2); // 2
        tasksManager.getTaskById(taskId1); // 1
        printHistory(tasksManager.getHistory());

    }

    private static void printAllTasks(TasksManager tasksManager) {
        System.out.println("Задачи:");
        for (Task task : tasksManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : tasksManager.getEpics()) {
            System.out.println(epic);
            System.out.println("--> Подзадачи эпика:");
            for (Task task : tasksManager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : tasksManager.getSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("--------");
    }

    public static void printHistory(List<Task> history) {
        for (Task task : history) {
            System.out.println(task.getId());
        }
    }
}

