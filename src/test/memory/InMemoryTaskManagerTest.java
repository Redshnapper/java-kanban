package test.memory;

import kanban.manager.InMemoryTaskManager;
import kanban.manager.exception.ValidateTaskTimeException;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskStatuses;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.TasksManagerTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static kanban.model.TaskStatuses.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TasksManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        init();
    }

    @AfterEach
    void cleanIds() {
        taskManager.setId(0);
    }

    @Test
    void addTaskWithTimeShouldReturnEndDateEquals01_01_2023_12_00_00() {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 11, 1);
        int duration = 60;
        LocalDateTime endTime = time.plusMinutes(duration);
        Task withTimeTask = new Task("Task 1", "Task 1", NEW, time, duration);
        taskManager.addNewTask(withTimeTask);

        assertEquals(time, withTimeTask.getStartDate(), "Дата начала не совпадает");
        assertEquals(duration, withTimeTask.getDuration(), "Длительность не совпадает");
        assertEquals(endTime, withTimeTask.getEndDate(), "Дата окончания не совпадает");
    }

    @Test
    void addSubtaskWithTimeShouldReturnEndDateEquals01_01_2023_12_00_00() {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 11, 1);
        int duration = 60;
        LocalDateTime endTime = time.plusMinutes(duration);
        long epicId = taskManager.addNewEpic(epic);
        Subtask subtaskWithTime = new Subtask("Subtask 1", "Sub 1", NEW, time, duration, epicId);
        taskManager.addNewSubtask(subtaskWithTime);

        assertEquals(time, subtaskWithTime.getStartDate(), "Дата начала не совпадает");
        assertEquals(duration, subtaskWithTime.getDuration(), "Длительность не совпадает");
        assertEquals(endTime, subtaskWithTime.getEndDate(), "Дата окончания не совпадает");
    }

    @Test
    void addEpicWithSubtaskShouldReturnEndDateEquals01_01_2023_12_00_00() {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 11, 0);
        int duration = 60;
        LocalDateTime endTime = time.plusMinutes(duration);
        long epicId = taskManager.addNewEpic(epic);
        Subtask subtaskWithTime = new Subtask("Subtask 1", "Sub 1", NEW, time, duration, epicId);
        taskManager.addNewSubtask(subtaskWithTime);

        assertEquals(time, epic.getStartDate(), "Дата начала не совпадает");
        assertEquals(duration, epic.getDuration(), "Длительность не совпадает");
        assertEquals(endTime, epic.getEndTime(), "Дата окончания не совпадает");
    }

    @Test
    void validateShouldThrowsExceptionWithTasksIntersection() {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 1, 1);
        Task withTimeTask = new Task("Task 1", "Task 1", NEW, time, 300);
        Task withTimeSecondTask = new Task("Task 2", "Task 2", NEW, time, 300);
        taskManager.addNewTask(withTimeTask);
        final ValidateTaskTimeException exception = assertThrows(
                ValidateTaskTimeException.class,
                () -> taskManager.addNewTask(withTimeSecondTask));

        assertEquals("Валидация не пройдена, задачи пересекаются по времени!", exception.getMessage());
    }

    @Test
    void validateShouldThrowsExceptionWithSubtasksIntersection() {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 1, 1);
        Task withTimeSubtask = new Subtask("Subtask 1", "Sub 1", NEW, time, 300, 1);
        Task withTimeSecondSubtask = new Subtask("Subtask 2", "Sub 2", NEW, time, 300, 1);
        taskManager.addNewTask(withTimeSubtask);

        final ValidateTaskTimeException exception = assertThrows(
                ValidateTaskTimeException.class,
                () -> taskManager.addNewTask(withTimeSecondSubtask));
        assertEquals("Валидация не пройдена, задачи пересекаются по времени!", exception.getMessage());
    }

    @Test
    void shouldReturnNewStatusWith0Subtasks() {
        taskManager.addNewEpic(epic);
        TaskStatuses status = epic.getStatus();

        assertEquals(NEW, status, "Статус не NEW");
    }

    @Test
    void shouldReturnNewStatusWith2NewSubtasks() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        Subtask secondSubtask = new Subtask("Subtask 2", "Subtask 2 description", NEW, epicId);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(secondSubtask);
        TaskStatuses status = epic.getStatus();

        assertEquals(NEW, status, "Статус не DONE");
    }

    @Test
    void shouldReturnDoneStatusWith2CompletedSubtasks() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", DONE, epicId);
        Subtask secondSubtask = new Subtask("Subtask 2", "Subtask 2 description", DONE, epicId);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(secondSubtask);
        TaskStatuses status = epic.getStatus();

        assertEquals(DONE, status, "Статус не DONE");
    }

    @Test
    void shouldReturnInProgressStatusWithNewAndDoneSubtasks() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        Subtask secondSubtask = new Subtask("Subtask 2", "Subtask 2 description", DONE, epicId);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(secondSubtask);
        TaskStatuses status = epic.getStatus();

        assertEquals(IN_PROGRESS, status, "Статус не DONE");
    }

    @Test
    void shouldReturnInProgressStatusWith2InProgressSubtasks() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", IN_PROGRESS, epicId);
        Subtask secondSubtask = new Subtask("Subtask 2", "Subtask 2 description", IN_PROGRESS, epicId);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(secondSubtask);
        TaskStatuses status = epic.getStatus();

        assertEquals(IN_PROGRESS, status, "Статус не DONE");
    }

    @Test
    void shouldDeleteTaskById() {
        long id = taskManager.addNewTask(task);
        taskManager.deleteTask(id);

        assertNull(taskManager.getTaskById(id), "Задача не удалена");
    }

    @Test
    void shouldDeleteSubtaskById() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        Subtask secondSubtask = new Subtask("Subtask 2", "Subtask 2 description", NEW, epicId);
        long id = taskManager.addNewSubtask(subtask);
        taskManager.deleteSubtask(id);

        assertEquals(NEW, epic.getStatus(), "Статус не NEW");
        assertTrue(epic.getSubtaskId().isEmpty(), "Подзадача не удалена из списка подзадач в" +
                " соответствующем эпике");
        assertNull(taskManager.getSubtaskById(id), "Подзадача не удалена");
    }

    @Test
    void shouldDeleteEpicByIdAndCorrespondingSubtasks() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        Subtask secondSubtask = new Subtask("Subtask 2", "Subtask 2 description", NEW, epicId);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(secondSubtask);
        taskManager.deleteEpic(epicId);

        assertTrue(taskManager.getSubtasks().isEmpty(), "Подзадачи не удалены");
        assertNull(taskManager.getEpicById(epicId), "Эпик не удален");
    }

    @Test
    void shouldReturnUpdatedTaskFromTasksMap() {
        long id = taskManager.addNewTask(task);
        Task updatedTask = new Task("Task 1", "Task 1 description", IN_PROGRESS);
        updatedTask.setId(id);
        taskManager.updateTask(updatedTask);

        assertEquals(updatedTask, taskManager.getTaskById(id), "Задача не обновилась");
    }

    @Test
    void updateTaskShouldReturnNullFromEmptyTaskMap() {
        task.setId(1);
        Task updatedTask = new Task("Task 1", "Task 1 description", IN_PROGRESS);
        updatedTask.setId(1);
        taskManager.updateTask(updatedTask);

        assertEquals(0, taskManager.getTasks().size());
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    void shouldReturnUpdatedSubtaskFromSubtaskMap() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        long id = taskManager.addNewSubtask(subtask);
        Subtask updatedSubtask = new Subtask("Subtask", "Subtask description", IN_PROGRESS, epicId);
        updatedSubtask.setId(id);
        taskManager.updateSubtask(updatedSubtask);

        assertEquals(IN_PROGRESS, epic.getStatus(), "Статус не IN_PROGRESS");
        assertEquals(updatedSubtask, taskManager.getSubtaskById(id), "Подзадача не обновилась");
    }

    @Test
    void updateSubtaskShouldReturnNullFromEmptySubtaskMap() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        subtask.setId(1);
        Subtask updatedSubtask = new Subtask("Subtask", "Subtask description", IN_PROGRESS, epicId);
        updatedSubtask.setId(1);
        taskManager.updateSubtask(updatedSubtask);

        assertEquals(NEW, epic.getStatus(), "Статус не NEW");
        assertEquals(0, taskManager.getSubtasks().size());
        assertNull(taskManager.getSubtaskById(1));
    }

    @Test
    void shouldReturnUpdatedEpicFromEpicsMap() {
        long id = taskManager.addNewEpic(epic);
        Epic updatedEpic = new Epic("Epic 1", "Epic 1 description", IN_PROGRESS);
        updatedEpic.setId(id);
        taskManager.updateEpic(updatedEpic);

        assertEquals(updatedEpic, taskManager.getEpicById(id), "Эпик не обновился");
    }

    @Test
    void updateEpicShouldReturnNullFromEmptyEpicMap() {
        epic.setId(1);
        Epic updatedEpic = new Epic("Epic 1", "Epic 1 description", IN_PROGRESS);
        updatedEpic.setId(1);
        taskManager.updateEpic(updatedEpic);

        assertEquals(0, taskManager.getEpics().size());
        assertNull(taskManager.getEpicById(1));
    }

    @Test
    void getTaskByIdShouldReturnNullWithEmptyTasksList() {
        Task task = taskManager.getTaskById(1);

        assertEquals(0, taskManager.getTasks().size());
        assertNull(task);
    }

    @Test
    void shouldReturnTaskById() {
        long id = taskManager.addNewTask(task);
        Task savedTask = taskManager.getTaskById(id);

        assertEquals(1, id);
        assertNotNull(savedTask);
        assertEquals(task, savedTask);
    }

    @Test
    void getTaskByIdShouldReturnNullWithWrongTaskId() {
        taskManager.addNewTask(task);
        Task newTask = taskManager.getTaskById(2);

        assertNull(newTask);
    }

    @Test
    void getSubtaskByIdShouldReturnNullWithEmptySubtasksList() {
        Subtask subtask = taskManager.getSubtaskById(1);

        assertEquals(0, taskManager.getSubtasks().size());
        assertNull(subtask);
    }

    @Test
    void shouldReturnSubtaskById() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        long SubtaskId = taskManager.addNewSubtask(subtask);
        Subtask savedSubtask = taskManager.getSubtaskById(SubtaskId);

        assertEquals(NEW, epic.getStatus(), "Статус не NEW");
        assertEquals(2, SubtaskId, "Id подзадач не совпадают");
        assertNotNull(savedSubtask, "Подзадача пустая");
        assertEquals(subtask, savedSubtask, "Подзадачи не равны");
    }

    @Test
    void getSubtaskByIdShouldReturnNullWithWrongSubtaskId() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        Subtask newSubtask = taskManager.getSubtaskById(2);

        assertNull(newSubtask);
    }

    @Test
    void getEpicByIdShouldReturnNullWithEmptyEpicsList() {
        Epic epic = taskManager.getEpicById(1);

        assertEquals(0, taskManager.getEpics().size());
        assertNull(epic);
    }

    @Test
    void shouldReturnEpicById() {
        long epicId = taskManager.addNewEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epicId);

        assertEquals(1, epicId, "Id подзадач не совпадают");
        assertNotNull(savedEpic, "Подзадача пустая");
        assertEquals(epic, savedEpic, "Подзадачи не равны");
    }

    @Test
    void getEpicByIdShouldReturnNullWithWrongEpicId() {
        taskManager.addNewEpic(epic);
        Epic newEpic = taskManager.getEpicById(2);

        assertNull(newEpic);
    }

    @Test
    void checkRemoveTasksShouldReturnEmptyList() {
        taskManager.addNewTask(task);
        taskManager.removeTasks();

        assertEquals(0, taskManager.getTasks().size(), "Список задач не пуст");
    }

    @Test
    void checkRemoveSubtasksShouldReturnEmptyList() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", DONE, epicId);
        taskManager.addNewSubtask(subtask);
        taskManager.removeSubtasks();

        assertEquals(NEW, epic.getStatus(), "Статус не NEW");
        assertEquals(0, taskManager.getSubtasks().size(), "Список задач не пуст");
        assertEquals(0, epic.getSubtaskId().size(), "У эпика остался не пустой список сабтасок");
    }

    @Test
    void checkRemoveEpicsShouldReturnEmptyList() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        taskManager.addNewSubtask(subtask);
        taskManager.removeEpics();

        assertEquals(0, taskManager.getEpics().size(), "Список эпиков не пуст");
        assertEquals(0, taskManager.getSubtasks().size(), "Список подзадач не пуст");
    }


    @Test
    void shouldReturnEmptyListOfTasks() {
        List<Task> tasks = new ArrayList<>(taskManager.getTasks());

        assertEquals(0, tasks.size(), "Список задач не пустой");
    }

    @Test
    void shouldReturnEmptyListOfSubtasks() {
        List<Task> subs = new ArrayList<>(taskManager.getSubtasks());

        assertEquals(0, subs.size(), "Список подзадач не пустой");
    }

    @Test
    void shouldReturnEmptyListOfEpics() {
        List<Task> epics = new ArrayList<>(taskManager.getEpics());

        assertEquals(0, epics.size(), "Список эпиков не пустой");
    }

    @Test
    void shouldReturnListOfTwoTasks() {
        List<Task> tasks = new ArrayList<>();
        Task secondTask = new Task("Task 2", "Task 2 description", NEW);
        taskManager.addNewTask(task);
        taskManager.addNewTask(secondTask);
        tasks.add(task);
        tasks.add(secondTask);

        assertNotNull(taskManager.getTasks(), "Список задач пуст");
        assertEquals(tasks, taskManager.getTasks(), "Списки задач не совпадают");
    }

    @Test
    void shouldReturnListOfTwoSubtasks() {
        List<Subtask> subs = new ArrayList<>();
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", DONE, epicId);
        Subtask secondSubtask = new Subtask("Subtask 2", "Subtask 2 description", DONE, epicId);
        subs.add(subtask);
        subs.add(secondSubtask);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(secondSubtask);

        assertEquals(DONE, epic.getStatus(), "Статус не DONE");
        assertNotNull(taskManager.getSubtasks(), "Список подзадач пуст");
        assertEquals(subs, taskManager.getSubtasks(), "Списки подзадач не совпадают");

    }

    @Test
    void shouldReturnListOfTwoEpics() {
        List<Epic> epics = new ArrayList<>();
        Epic secondEpic = new Epic("Epic 2", "Epic 2 description", NEW);
        taskManager.addNewEpic(epic);
        taskManager.addNewEpic(secondEpic);
        epics.add(epic);
        epics.add(secondEpic);

        assertNotNull(taskManager.getEpics(), "Список эпиков пуст");
        assertEquals(epics, taskManager.getEpics(), "Списки задач не совпадают");

    }

    @Test
    void shouldReturnListOfTwoEpicSubtasks() {
        List<Subtask> subs = new ArrayList<>();
        List<Subtask> savedSubs;
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        Subtask secondSubtask = new Subtask("Subtask 2", "Subtask 2 description", NEW, epicId);

        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(secondSubtask);
        savedSubs = taskManager.getEpicSubtasks(epicId);
        subs.add(subtask);
        subs.add(secondSubtask);

        assertEquals(NEW, epic.getStatus(), "Статус не NEW");
        assertNotNull(savedSubs, "Список задач пуст");
        assertEquals(subs, savedSubs, "Списки задач не равны");
    }

    @Test
    void shouldReturnNullWithIncorrectEpicId() {
        List<Subtask> subs = taskManager.getEpicSubtasks(100000000);

        assertNull(subs, "Список задач не пуст");
    }

    @Test
    void shouldSetId() {
        taskManager.setId(10);
        long id = taskManager.getId();
        assertEquals(10, id, "Id не совпадают");
    }

    @Test
    void shouldReturnEmptyTaskList() {
        List<Long> savedList = taskManager.getTaskIdList();

        assertEquals(0, savedList.size());
        assertTrue(savedList.isEmpty());
    }

    @Test
    void shouldReturnEmptySubtaskList() {
        List<Long> savedList = taskManager.getSubtaskIdList();

        assertEquals(0, savedList.size());
        assertTrue(savedList.isEmpty());
    }

    @Test
    void shouldReturnEmptyEpicList() {
        List<Long> savedList = taskManager.getEpicIdList();

        assertEquals(0, savedList.size());
        assertTrue(savedList.isEmpty());
    }
    @Test
    void shouldReturnTaskListWithTaskIdEqualZero() {
        List<Long> correctList = new ArrayList<>();
        correctList.add(0L);
        taskManager.createTask(task);
        List<Long> savedList = taskManager.getTaskIdList();

        assertEquals(1, savedList.size());
        assertEquals(correctList, savedList);
    }
    @Test
    void shouldReturnEpicListWithEpicIdEqualZero() {
        List<Long> correctList = new ArrayList<>();
        correctList.add(0L);
        taskManager.createEpic(epic);
        List<Long> savedList = taskManager.getEpicIdList();

        assertEquals(1, savedList.size());
        assertEquals(correctList, savedList);
    }

    @Test
    void shouldReturnSubtaskListWithSubtaskIdEqualZero() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        List<Long> correctList = new ArrayList<>();
        correctList.add(0L);
        taskManager.createSubtask(subtask);
        List<Long> savedList = taskManager.getSubtaskIdList();

        assertEquals(1, savedList.size());
        assertEquals(correctList, savedList);
    }

    @Test
    void addNewTaskEqualsOriginTaskAndNotNull() {
        long id = taskManager.addNewTask(task);
        Task savedTask = taskManager.getTaskById(id);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    @Test
    void addTwoTasksCheckNotEqualsAndIncreaseIds() {
        long id = taskManager.addNewTask(task);
        long id1 = taskManager.addNewTask(new Task("1", "1", NEW));
        Task savedTask = taskManager.getTaskById(id);
        Task newTask = taskManager.getTaskById(id1);

        assertNotEquals(savedTask, newTask);
        assertNotEquals(id, id1, "Id совпадают");
        assertEquals(1, id);
        assertEquals(2, id1);
    }

    @Test
    void addTwoEpicsCheckEqualsAndIncreaseIds() {
        long id = taskManager.addNewEpic(epic);
        long id1 = taskManager.addNewEpic(epic);
        Epic savedEpic = taskManager.getEpicById(id);
        Epic newEpic = taskManager.getEpicById(id1);

        assertNotNull(savedEpic, "Эпик не создан");
        assertNotNull(newEpic, "Эпик не создан");
        assertEquals(savedEpic, newEpic, "Эпики не совпадают");
        assertNotEquals(id, id1, "Id совпадают");
        assertEquals(1, id);
        assertEquals(2, id1);
    }

    @Test
    void addNewSubtaskCheckEqualsEpicIds() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", IN_PROGRESS, epicId);
        long id = taskManager.addNewSubtask(subtask);
        Subtask savedSubtask = taskManager.getSubtaskById(id);
        long subtaskEpicId = subtask.getEpicId();

        assertEquals(IN_PROGRESS, epic.getStatus(), "Статус не IN_PROGRESS");
        assertEquals(epicId, subtaskEpicId, "Id эпиков не равны");
        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают");

    }

    @Test
    void addTwoSubtasksCheckEqualsAndIncreaseIds() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        Subtask secondSubtask = new Subtask("Subtask 2", "Subtask 2 description", DONE, epicId);
        long id = taskManager.addNewSubtask(subtask);
        long id2 = taskManager.addNewSubtask(secondSubtask);
        Task savedSubtask = taskManager.getSubtaskById(id);
        Task secondSavedSubtask = taskManager.getSubtaskById(id2);

        assertEquals(IN_PROGRESS, epic.getStatus(), "Статус не IN_PROGRESS");
        assertNotEquals(secondSavedSubtask, savedSubtask, "Подзадачи совпадают");
        assertNotEquals(id, id2, "Id совпадают");
        assertEquals(2, id);
        assertEquals(3, id2);
    }

    @Test
    void addNewEpicEqualsOriginEpicAndNotNull() {
        long id = taskManager.addNewEpic(epic);
        Epic savedEpic = taskManager.getEpicById(id);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");
    }

    @Test
    void createTaskEqualsOriginTaskAndNotNull() {
        Task savedTask = taskManager.createTask(task);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    @Test
    void createSubtaskEqualsOriginSubtaskAndNotNull() {
        long epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask", "Subtask description", NEW, epicId);
        Task savedSubtask = taskManager.createSubtask(subtask);

        assertEquals(epicId, subtask.getEpicId(), "Id эпиков не совпадают");
        assertNotNull(savedSubtask, "Задача не найдена");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают");
    }

    @Test
    void createEpicEqualsOriginEpicAndNotNull() {
        Epic savedEpic = taskManager.createEpic(epic);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");
    }

}