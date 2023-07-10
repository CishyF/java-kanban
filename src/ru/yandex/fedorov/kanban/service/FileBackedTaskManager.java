package ru.yandex.fedorov.kanban.service;

import ru.yandex.fedorov.kanban.model.Task;

import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private static String historyToString(List<Task> history) {
        return history.stream()
                .map(t -> String.valueOf(t.getId()))
                .collect(Collectors.joining(","));
    }

    private static void addHistoryInManagerFromString(String history, TaskManager taskManager) {
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
