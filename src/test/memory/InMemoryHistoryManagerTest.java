package test.memory;

import kanban.manager.HistoryManager;
import kanban.manager.Managers;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static kanban.model.TaskStatuses.DONE;
import static kanban.model.TaskStatuses.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    Task task;
    Subtask subtask;
    Epic epic;
    List<Task> history;

    @BeforeEach
    void SetUp() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Task", "Task description", NEW);
        epic = new Epic("Epic", "Epic description", NEW);
        long id = epic.getId();
        subtask = new Subtask("Subtask", "Subtask description", DONE, id);
        epic.setSubtaskId(id);
    }

    @Test
    void addTaskTwiceShouldReturnTask() {
        historyManager.add(task);
        historyManager.add(task);
        Task savedTask = historyManager.getHistory().get(0);
        int historyLength = historyManager.getHistory().size();

        assertEquals(1, historyLength, "В истории больше 1 элемента");
        assertEquals(task, savedTask, "Сохранные задачи не совпадают");
    }

    @Test
    void removeFromEmptyHistoryShouldReturnVoid() {
        int historyLength = historyManager.getHistory().size(); // 0
        assertEquals(0, historyLength, "История не пустая");
        historyManager.remove(1);
        assertEquals(0, historyLength, "История изменилась");
    }

    @Test
    void shouldReturnVoidAfterRemoveTaskTwice() {
        long id = task.getId();
        history = new ArrayList<>();
        List<Task> savedHistory;
        historyManager.add(task);
        historyManager.remove(id);
        historyManager.remove(id);
        savedHistory = historyManager.getHistory();

        assertNotNull(savedHistory, "История это null");
        assertEquals(history, savedHistory, "История не пуста");
    }


    @Test
    void getHistoryReturnNull() {
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История это null");
        assertTrue(history.isEmpty(), "История не пустая");
    }

    @Test
    void getHistoryReturnHistoryOfOneTaskWithRepeats() {
        final List<Task> correctHistory = new ArrayList<>();
        correctHistory.add(task);
        historyManager.add(task);
        historyManager.add(task);
        final List<Task> savedHistory = historyManager.getHistory();

        assertEquals(1, savedHistory.size());
        assertEquals(correctHistory, savedHistory);
    }

    @Test
    void shouldReturnHistoryOf1TaskAfterDeleteEpicFromStartOfHistory() {
        epic.setId(1);
        task.setId(2);
        historyManager.add(epic);
        historyManager.add(task);
        historyManager.remove(epic.getId());
        Task savedTask = historyManager.getHistory().get(0);

        assertEquals(1, historyManager.getHistory().size(), "История содержит больше 1 элемента");
        assertEquals(task, savedTask, "Последние элементы истории не совпадают");
    }

    @Test
    void shouldReturnHistoryOfEpicAndTaskAfterDeleteSubtaskFromHistory() {
        history = new ArrayList<>();
        epic.setId(1);
        task.setId(2);
        subtask.setId(3);
        historyManager.add(epic);
        history.add(epic);
        historyManager.add(subtask);
        historyManager.add(task);
        history.add(task);
        historyManager.remove(subtask.getId());
        List<Task> savedHistory = historyManager.getHistory();

        assertEquals(2, historyManager.getHistory().size(), "История содержит больше 2 элементов");
        assertEquals(history, savedHistory, "Истории не совпадают");
    }

    @Test
    void shouldReturnHistoryOf1EpicAfterDeleteTaskFromEndOfHistory() {
        epic.setId(1);
        task.setId(2);
        historyManager.add(epic);
        historyManager.add(task);
        historyManager.remove(task.getId());
        Task savedEpic = historyManager.getHistory().get(0);

        assertEquals(1, historyManager.getHistory().size(), "История содержит больше 1 элемента");
        assertEquals(epic, savedEpic, "Последние элементы истории не совпадают");
    }
}