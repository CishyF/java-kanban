package ru.yandex.fedorov.kanban.util;

import ru.yandex.fedorov.kanban.service.*;
import ru.yandex.fedorov.kanban.web.service.HttpTaskManager;

public class Managers {

    private Managers() {}

    public static HttpTaskManager getDefault() {
        return HttpTaskManager.loadFromServer("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
