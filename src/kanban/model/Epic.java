package kanban.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Long> subtaskId = new ArrayList<>();

    @Override
    public TasksTypes getTaskType() {
        return TasksTypes.EPIC;
    }

    public Epic(String name, String description, TaskStatuses status) {
        super(name, description, status);
    }

    public List<Long> getSubtaskId() {
        return subtaskId;
    }

    public void removeSubtask(long id) {
        if (subtaskId.contains(id)) {
            subtaskId.remove(id);
        }
    }

    public void clearSubsId() {
        subtaskId.clear();
    }

    public void setSubtaskId(long subId) {
        subtaskId.add(subId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return id == epic.id && Objects.equals(name, epic.name) && Objects.equals(description, epic.description)
                && status == epic.status && getTaskType() == epic.getTaskType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId, getTaskType());
    }

    @Override
    public String toString() {
        return "kanban.model.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
