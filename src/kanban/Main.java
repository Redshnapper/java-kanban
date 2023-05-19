package kanban;

import kanban.manager.*;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;


public class Main {
    public static void main(String[] args) {
        TasksManager tasksManager = Managers.getDefault();
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
        final Long subtaskId1 = tasksManager.addNewSubtask(subtask1);
        final Long subtaskId2 = tasksManager.addNewSubtask(subtask2);
        final Long subtaskId3 = tasksManager.addNewSubtask(subtask3);
        printAllTasks(tasksManager);

        System.out.println("--------");

        task1.setStatus(TaskStatuses.DONE);
        subtask3.setStatus(TaskStatuses.NEW);
        subtask3.setName("Subtask #3-2.1");

        tasksManager.updateTask(task1);
        tasksManager.updateSubtask(subtask3);
        tasksManager.updateEpic(epic1);
        printAllTasks(tasksManager);

        System.out.println("--------");

        tasksManager.deleteSubtask(subtaskId3);
        tasksManager.deleteEpic(epicId2);
        tasksManager.deleteTask(taskId1);
        System.out.println(tasksManager.getSubtasksByEpic(epicId1));
        System.out.println(tasksManager.getTasks());
        System.out.println(tasksManager.getSubtasks());
        System.out.println(tasksManager.getEpics());
        printAllTasks(tasksManager);

        System.out.println("--------");

        tasksManager.getTaskById(taskId2);
        tasksManager.getTaskById(taskId2);
        tasksManager.getEpicById(epicId1);
        tasksManager.getEpicById(epicId1);
        tasksManager.getTaskById(taskId2);
        
        tasksManager.getTaskById(taskId2);
        tasksManager.getTaskById(taskId2);
        tasksManager.getTaskById(taskId2);
        tasksManager.getTaskById(taskId2);
        tasksManager.getSubtaskById(subtaskId1);

        tasksManager.getSubtaskById(subtaskId1);
        tasksManager.getSubtaskById(subtaskId2);

        tasksManager.getHistory();

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
    }
}

