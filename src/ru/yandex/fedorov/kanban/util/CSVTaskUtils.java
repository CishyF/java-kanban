package ru.yandex.fedorov.kanban.util;

import ru.yandex.fedorov.kanban.exception.ManagerSaveException;
import ru.yandex.fedorov.kanban.model.*;
import ru.yandex.fedorov.kanban.service.HistoryManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVTaskUtils {

    private CSVTaskUtils() {}

    public static String getHeader() {
        return "id,type,name,status,description,epic";
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

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description, status);
            case SUBTASK:
                int epicId = Integer.parseInt(taskFields[5]);
                return new Subtask(id, name, description, status, epicId);
        }

        throw new ManagerSaveException("Can't read task from string");
    }

    public static List<Integer> historyFromString(String historyInString) {
        return Arrays.stream(historyInString.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

}
