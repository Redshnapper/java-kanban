package kanban;

import kanban.manager.TasksManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;


public class Main {
    public static void main(String[] args) {
        TasksManager tasksManager = new TasksManager();
        Task task1 = new Task("Task #1", "Task1 description", "NEW");
        Task task2 = new Task("Task #2", "Task2 description", "IN_PROGRESS");
        final long taskId1 = tasksManager.addNewTask(task1);
        final long taskId2 = tasksManager.addNewTask(task2);
        Epic epic1 = new Epic("Epic #1", "Epic1 description", "NEW");
        Epic epic2 = new Epic("Epic #2", "Epic2 description", "NEW");
        final long epicId1 = tasksManager.addNewEpic(epic1);
        final long epicId2 = tasksManager.addNewEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask #1-1", "Subtask1 description", "NEW", epicId1);
        Subtask subtask2 = new Subtask("Subtask #2-1", "Subtask2 description", "DONE", epicId1);
        Subtask subtask3 = new Subtask("Subtask #3-2", "Subtask3 description", "DONE", epicId2);
        final Integer subtaskId1 = tasksManager.addNewSubtask(subtask1);
        final Integer subtaskId2 = tasksManager.addNewSubtask(subtask2);
        final Integer subtaskId3 = tasksManager.addNewSubtask(subtask3);
        printAllTasks(tasksManager);

        task1.setStatus("DONE");
        subtask3.setStatus("NEW");
        subtask3.setName("Subtask #3-2.1");
        epic1.setStatus("Epic #1.1");
        tasksManager.updateTask(task1);
        tasksManager.updateSubtask(subtask3);
        tasksManager.updateEpic(epic1);
        printAllTasks(tasksManager);

        tasksManager.deleteSubtask(subtaskId1);
        tasksManager.deleteEpic(epicId2);
        tasksManager.deleteTask(taskId1);
        System.out.println(tasksManager.getSubtasksByEpic(epicId1));
        System.out.println(tasksManager.getTasks());
        System.out.println(tasksManager.getSubtasks());
        System.out.println(tasksManager.getEpics());
        printAllTasks(tasksManager);


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

