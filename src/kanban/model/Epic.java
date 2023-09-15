package kanban.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {
    private final List<Long> subtaskId = new ArrayList<>();
    protected LocalDateTime endTime;

    @Override
    public TasksTypes getTaskType() {
        return TasksTypes.EPIC;
    }

    public Epic(String name, String description, TaskStatuses status) {
        super(name, description, status);
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
                this.endTime = end;
                this.duration = duration;
            }
        }
    }

    public LocalDateTime getEndTime() {
        return endTime;
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
                ", endDate=" + endTime +
                '}';
    }
}
