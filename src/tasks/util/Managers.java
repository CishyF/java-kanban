package tasks.util;

import tasks.service.HistoryManager;
import tasks.service.InMemoryHistoryManager;
import tasks.service.InMemoryTaskManager;
import tasks.service.TaskManager;

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
