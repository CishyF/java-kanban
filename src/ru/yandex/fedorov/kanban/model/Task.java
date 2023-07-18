package ru.yandex.fedorov.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected LocalDateTime startTime;
    protected long duration;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, long durationInMinutes) {
        if (durationInMinutes < 0) {
            throw new RuntimeException("Продолжительность задачи не может быть меньше 0");
        }
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = durationInMinutes;
    }

    public Task(
            int id, String name, String description,
            TaskStatus status, LocalDateTime startTime, long durationInMinutes
    ) {
        if (durationInMinutes < 0) {
            throw new RuntimeException("Продолжительность задачи не может быть меньше 0");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = durationInMinutes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(Duration.ofMinutes(duration));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Task))
            return false;

        Task t = (Task) o;
        return id == t.id;
    }

    @Override
    public int hashCode() {

        return 31 * Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

}
