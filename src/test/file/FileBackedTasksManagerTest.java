package test.file;

import kanban.manager.HistoryManager;
import kanban.manager.InMemoryTaskManager;
import kanban.manager.Managers;
import kanban.manager.exception.ManagerSaveException;
import kanban.manager.file.CSVFormatHandler;
import kanban.manager.file.FileBackedTasksManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskStatuses;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.TasksManagerTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static kanban.model.TaskStatuses.NEW;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TasksManagerTest<FileBackedTasksManager> {

    File file = new File("src\\test\\resources\\allTasks.csv");
    FileBackedTasksManager fileBackedTasksManager;

    @BeforeEach
    void setUp() {
        init();
        fileBackedTasksManager = Managers.getDefaultFile();
        CSVFormatHandler.setFile(file);

    }

    @AfterEach
    void cleanIds() {
        InMemoryTaskManager.setId(0);
    }

    @AfterEach
    void cleanData() {
        fileBackedTasksManager = null;
        File writedFile = CSVFormatHandler.getFile();
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(writedFile))) {
            fileWriter.write("");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка во время сохранения файла " + e.getMessage());
        }
    }


    @Test
    void loadFromFileShouldReturnTaskEndDateEquals01_01_2023_12_00_00() {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 11, 1);
        int duration = 60;
        LocalDateTime endTime = time.plusMinutes(duration);
        Task withTimeTask = new Task("Task 1", "Task 1", NEW, time, duration);
        fileBackedTasksManager.addNewTask(withTimeTask);
        FileBackedTasksManager savedManager = fileBackedTasksManager.loadFromFile(file);
        LocalDateTime savedStartDate = savedManager.getTasks().get(0).getStartDate();
        LocalDateTime savedEndDate = savedManager.getTasks().get(0).getEndDate();
        int savedDuration = savedManager.getTasks().get(0).getDuration();

        assertEquals(time, savedStartDate, "Дата начала не совпадает");
        assertEquals(duration, savedDuration, "Длительность не совпадает");
        assertEquals(endTime, savedEndDate, "Дата окончания не совпадает");
    }

    @Test
    void loadFromFileShouldReturnSubtaskEndDateEquals01_01_2023_12_00_00() {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 11, 1);
        int duration = 60;
        LocalDateTime endTime = time.plusMinutes(duration);
        long epicId = fileBackedTasksManager.addNewEpic(epic);
        Task withTimeTask = new Subtask("Subtask 1", "Subtask 1", NEW, time, duration, epicId);
        fileBackedTasksManager.addNewTask(withTimeTask);
        FileBackedTasksManager savedManager = fileBackedTasksManager.loadFromFile(file);
        LocalDateTime savedStartDate = savedManager.getSubtasks().get(0).getStartDate();
        LocalDateTime savedEndDate = savedManager.getSubtasks().get(0).getEndDate();
        int savedDuration = savedManager.getSubtasks().get(0).getDuration();

        assertEquals(time, savedStartDate, "Дата начала не совпадает");
        assertEquals(duration, savedDuration, "Длительность не совпадает");
        assertEquals(endTime, savedEndDate, "Дата окончания не совпадает");
    }

    @Test
    void loadFromFileShouldReturnEpicEndDateEquals01_01_2023_12_00_00() {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 11, 1);
        int duration = 60;
        LocalDateTime endTime = time.plusMinutes(duration);
        long epicId = fileBackedTasksManager.addNewEpic(epic);
        Task withTimeTask = new Subtask("Subtask 1", "Subtask 1", NEW, time, duration, epicId);
        fileBackedTasksManager.addNewTask(withTimeTask);
        FileBackedTasksManager savedManager = fileBackedTasksManager.loadFromFile(file);
        LocalDateTime savedStartDate = savedManager.getEpics().get(0).getStartDate();
        LocalDateTime savedEndDate = savedManager.getEpics().get(0).getEndDate();
        int savedDuration = savedManager.getEpics().get(0).getDuration();

        assertEquals(time, savedStartDate, "Дата начала не совпадает");
        assertEquals(duration, savedDuration, "Длительность не совпадает");
        assertEquals(endTime, savedEndDate, "Дата окончания не совпадает");
    }


    @Test
    void loadFromEmptyFileShouldReturnNull() {
        fileBackedTasksManager = fileBackedTasksManager.loadFromFile(file);

        assertNull(fileBackedTasksManager, "Файл не пустой");
    }

    @Test
    void loadFromFileShouldReturnEpicNoSubtasks() {
        fileBackedTasksManager.addNewEpic(epic);
        fileBackedTasksManager.getEpicById(1);
        FileBackedTasksManager savedManager = fileBackedTasksManager.loadFromFile(file);
        Epic savedEpic = savedManager.getEpics().get(0);
        HistoryManager historyManager = savedManager.getHistoryManager();
        List<Task> savedHistory = historyManager.getHistory();

        assertNotNull(savedHistory, "История пустая");
        assertEquals(1, savedHistory.size(), "История больше или меньше 1");
        assertNotNull(savedManager, "Файл пустой");
        assertEquals(epic, savedEpic, "Эпики не совпадают");
    }

    @Test
    void loadFromFileShouldReturnHistoryOf2TasksWithoutRepeats() {
        Task newTask = new Task(task.getName() + "1", task.getDescription() + "1", TaskStatuses.NEW);
        Task secondNewTask = new Task(task.getName() + "2", task.getDescription() + "2", TaskStatuses.NEW);
        fileBackedTasksManager.addNewTask(task);
        fileBackedTasksManager.addNewTask(newTask);
        fileBackedTasksManager.getTaskById(task.getId());
        fileBackedTasksManager.getTaskById(newTask.getId());
        fileBackedTasksManager.getTaskById(newTask.getId());
        FileBackedTasksManager savedManager = fileBackedTasksManager.loadFromFile(file);
        HistoryManager historyManager = savedManager.getHistoryManager();
        List<Task> savedHistory = historyManager.getHistory();
        List<Task> history = List.of(task, newTask);
        savedManager.addNewTask(secondNewTask);

        assertNotNull(savedManager, "Файл пустой");
        assertEquals(history, savedHistory, "Истории не совпадают");
        assertEquals(3, secondNewTask.getId(), "После восстановления менеджера из файла" +
                "и добавления новой задачи Id не увеличился");

    }

    @Test
    void loadFromFileWithEmptyHistoryShouldReturn1Task() {
        fileBackedTasksManager.addNewTask(task);
        FileBackedTasksManager savedManager = fileBackedTasksManager.loadFromFile(file);
        savedManager.addNewTask(new Task("1", "2", TaskStatuses.NEW));
        HistoryManager historyManager = savedManager.getHistoryManager();
        List<Task> savedHistory = historyManager.getHistory();
        List<Task> tasks = savedManager.getTasks();

        assertEquals(task, tasks.get(0), "Задачи не совпадают");
        assertEquals(0, savedHistory.size(), "История больше 0");
    }
}