package ru.yandex.fedorov.kanban.util;

import ru.yandex.fedorov.kanban.exception.ManagerSaveException;
import ru.yandex.fedorov.kanban.model.*;
import ru.yandex.fedorov.kanban.service.HistoryManager;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CSVTaskUtils {

    private CSVTaskUtils() {}

    public static String getHeader() {
        return "id,type,name,status,description,start_time,duration,end_time,epic";
    }

    public static String historyToString(HistoryManager manager) {
        return manager.getHistory()
                .stream()
                .map(t -> String.valueOf(t.getId()))
                .collect(Collectors.joining(","));
    }

    public static Task taskFromString(String taskInString) {
        String[] taskFields = taskInString.split(",");

        int id = Integer.parseInt(taskFields[0]);
        TaskType type = TaskType.valueOf(taskFields[1]);
        String name = taskFields[2];
        TaskStatus status = TaskStatus.valueOf(taskFields[3]);
        String description = taskFields[4];
        LocalDateTime startTime = taskFields[5].equals("null") ? null : LocalDateTime.parse(taskFields[5]);
        long duration = Long.parseLong(taskFields[6]);
        LocalDateTime endTime = taskFields[5].equals("null") ? null : LocalDateTime.parse(taskFields[5]);

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, startTime, duration);
            case EPIC:
                Epic epic = new Epic(id, name, description, status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                epic.setEndTime(endTime);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(taskFields[8]);
                return new Subtask(id, name, description, status, startTime, duration, epicId);
        }

        throw new ManagerSaveException("Can't read task from string");
    }

    public static String taskToString(Task task) {
        Objects.requireNonNull(task);
        return String.format(
                "%d,%s,%s,%s,%s,%s,%d,%s,%s",
                task.getId(), task.getType().toString(), task.getName(),
                task.getStatus().toString(), task.getDescription(),
                task.getStartTime(), task.getDuration(), task.getEndTime(),
                task.getType().equals(TaskType.SUBTASK) ? String.valueOf(((Subtask) task).getEpicId()) : ""
        );
    }

    public static List<Integer> historyFromString(String historyInString) {
        return Arrays.stream(historyInString.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

}
