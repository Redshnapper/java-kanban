package kanban.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {
    private final List<Long> subtaskId = new ArrayList<>();
    protected LocalDateTime endDate;

    @Override
    public TasksTypes getTaskType() {
        return TasksTypes.EPIC;
    }

    public Epic(String name, String description) {
        super(name, description);
    }
    public Epic(String name, String description, LocalDateTime startTime, int duration) {
        super(name, description, startTime, duration);
        this.endDate = super.getEndDate();
    }

    public void calculateTime(Map<Long, Subtask> subs) {
        LocalDateTime start = LocalDateTime.MAX;
        Integer duration = 0;
        LocalDateTime end = LocalDateTime.MIN;

        if (!subs.isEmpty()) {
            for (Long id : subtaskId) {
                Subtask subtask = subs.get(id);
                duration += subtask.getDuration();
                if (subtask.getStartDate().isBefore(start)) {
                    start = subtask.getStartDate();
                }
                if (subtask.getEndDate().isAfter(end)) {
                    end = subtask.getEndDate();
                }
                this.startDate = start;
                this.endDate = end;
                this.duration = duration;
            }
        }
    }

    @Override
    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void removeSubtask(long id) {
        if (subtaskId.contains(id)) {
            subtaskId.remove(id);
        }
    }

    public List<Long> getSubtaskId() {
        return subtaskId;
    }

    public void clearSubsId() {
        subtaskId.clear();
    }

    public void setSubtaskId(long subId) {
        subtaskId.add(subId);
    }

    public void addSubtaskId(Long id) {
        subtaskId.add(id);
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
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", duration=" + duration +
                ", endDate=" + endDate +
                '}';
    }
}
