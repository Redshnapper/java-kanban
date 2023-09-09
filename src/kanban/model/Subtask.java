package kanban.model;

import java.time.LocalDateTime;

public class Subtask extends Task {
    protected long epicId;

    @Override
    public TasksTypes getTaskType() {
        return TasksTypes.SUBTASK;
    }

    public Subtask(String name, String description, TaskStatuses status, long epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatuses status, LocalDateTime startDate, Integer duration, long epicId) {
        super(name, description, status, startDate, duration);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", duration=" + duration +
                '}';
    }
}
