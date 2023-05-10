package ru.yandex.fedorov.kanban.util;

import ru.yandex.fedorov.kanban.service.InMemoryHistoryManager;
import ru.yandex.fedorov.kanban.service.InMemoryTaskManager;
import ru.yandex.fedorov.kanban.service.HistoryManager;
import ru.yandex.fedorov.kanban.service.TaskManager;

public class Managers {

    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    private Managers() {}

    public static TaskManager getDefault() {
        if (taskManager == null)
            taskManager = new InMemoryTaskManager();
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null)
            historyManager = new InMemoryHistoryManager();
        return historyManager;
    }

}
