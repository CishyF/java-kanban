package ru.yandex.fedorov.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(
        String name, String description, TaskStatus status, LocalDateTime startTime, long durationInMinutes, int epicId
    ) {
        super(name, description, status, startTime, durationInMinutes);
        this.epicId = epicId;
    }

    public Subtask(
        int id, String name, String description, TaskStatus status,
        LocalDateTime startTime, long durationInMinutes, int epicId
    ) {
        super(id, name, description, status, startTime, durationInMinutes);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + Duration.ofMinutes(duration) +
                '}';
    }

}
