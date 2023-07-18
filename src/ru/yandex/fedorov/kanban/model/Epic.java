package ru.yandex.fedorov.kanban.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private LocalDateTime endTime;

    private final List<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public void addSubtaskId(int id) {
        subtasksId.add(id);
    }

    public List<Integer> getSubtasksId() {
        return List.copyOf(subtasksId);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void setStartTime(LocalDateTime startTime) {
        if (endTime != null && startTime != null && startTime.isAfter(endTime)) {
            throw new RuntimeException("Время начала выполнения эпика не может быть позже времени завершения");
        }
        this.startTime = startTime;
    }

    public void setDuration(long durationInMinutes) {
        if (durationInMinutes < 0) {
            throw new RuntimeException("Продолжительность задачи не может быть меньше 0");
        }
        this.duration = durationInMinutes;
    }

    public void setEndTime(LocalDateTime endTime) {
        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            throw new RuntimeException("Время завершения не может быть раньше времени начала выполнения эпика");
        }
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasksId=" + subtasksId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

}
