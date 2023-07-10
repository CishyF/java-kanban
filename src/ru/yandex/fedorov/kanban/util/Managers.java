package ru.yandex.fedorov.kanban.util;

import ru.yandex.fedorov.kanban.model.Task;
import ru.yandex.fedorov.kanban.service.*;

import java.io.File;

public class Managers {

    private Managers() {}

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBacked(File file) {
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
