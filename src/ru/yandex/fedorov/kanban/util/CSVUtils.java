package ru.yandex.fedorov.kanban.util;

import ru.yandex.fedorov.kanban.service.HistoryManager;
import ru.yandex.fedorov.kanban.service.TaskManager;

import java.util.stream.Collectors;

public final class CSVUtils {

    private CSVUtils() {}

    public static String historyToString(HistoryManager history) {
        return history.getHistory()
                        .stream()
                        .map(t -> String.valueOf(t.getId()))
                        .collect(Collectors.joining(","));
    }

    public static void addHistoryInManagerFromString(String history, TaskManager taskManager) {
        for (String tempId : history.split(",")) {
            int id = Integer.parseInt(tempId);

            if (taskManager.getTask(id) == null) {
                if (taskManager.getEpic(id) == null) {
                    taskManager.getSubtask(id);
                }
            }
        }
    }
}
