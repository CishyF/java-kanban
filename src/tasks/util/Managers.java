package tasks.util;

import tasks.service.InMemoryTaskManager;
import tasks.service.TaskManager;

public class Managers {

    private static TaskManager taskManager;

    private Managers() {}

    public static TaskManager getDefault() {
        if (taskManager == null)
            taskManager = new InMemoryTaskManager();
        return taskManager;
    }

}
