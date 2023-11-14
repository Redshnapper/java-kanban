package kanban.manager.history;

import kanban.manager.history.HistoryManager;
import kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task item;
        Node next;
        Node prev;

        public Node(Node prev, Task item, Node next) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }

    HashMap<Long, Node> history = new HashMap<>();
    Node first;
    Node last;

    @Override
    public void add(Task task) {
        linkLast(task);
        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }
        history.put(task.getId(), last);

    }

    @Override
    public void remove(long id) {
        if (history.containsKey(id)) {
            removeById(id);
        }
    }

    private void removeById(long id) {
        Node node = history.get(id);
        removeNode(node);
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(last, task, null);
        final Node prevLast = last;
        last = newNode;
        if (prevLast == null) first = newNode;
        else prevLast.next = newNode;
    }

    private void removeNode(Node node) {
        history.remove(node.item.getId());
        if (first == node) {
            first = node.next;
            if (first == null) return;
            first.prev = null;
            return;
        }
        if (last == node) {
            last = node.prev;
            if (last == null) return;
            last.next = null;
            return;
        }

        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> list = new ArrayList<>();
        Node current = first;
        while (current != null) {
            list.add(current.item);
            current = current.next;
        }

        return new ArrayList<>(list);
    }
}
