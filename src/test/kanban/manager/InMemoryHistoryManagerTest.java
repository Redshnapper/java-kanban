package kanban.manager;

import kanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void SetUp() {
        historyManager = Managers.getDefaultHistory();
    }
    @Test
    void getHistory() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История это null");
        assertTrue(history.isEmpty(), "История не пустая");
    }
}