package kanban.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    protected long id;
    protected String name;
    protected String description;
    protected TaskStatuses status;
    protected LocalDateTime startDate = null;
    protected Integer duration = 0;

    public TasksTypes getTaskType() {
        return TasksTypes.TASK;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, TaskStatuses status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, TaskStatuses status, LocalDateTime startDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.duration = duration;
    }
    public Task(String name, String description, LocalDateTime startDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.duration = duration;
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }

    public TaskStatuses getStatus() {
        return status;
    }


    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setId(long id) {
        this.id = id;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatuses status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        if (startDate == null) return null;
        return startDate.plusMinutes(duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", duration=" + duration +
                ", endDate=" + getEndDate() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status && getTaskType() == task.getTaskType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id, getTaskType());
    }
}
