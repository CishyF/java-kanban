package ru.yandex.fedorov.kanban.util;

import ru.yandex.fedorov.kanban.service.InMemoryHistoryManager;
import ru.yandex.fedorov.kanban.service.InMemoryTaskManager;
import ru.yandex.fedorov.kanban.service.HistoryManager;
import ru.yandex.fedorov.kanban.service.TaskManager;

public class Managers {

    private Managers() {}

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
