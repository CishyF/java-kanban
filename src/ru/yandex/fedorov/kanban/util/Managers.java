package ru.yandex.fedorov.kanban.util;

import ru.yandex.fedorov.kanban.service.*;

import java.io.File;

public class Managers {

    private Managers() {}

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("resources/data.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
