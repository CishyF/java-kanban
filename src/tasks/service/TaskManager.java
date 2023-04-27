package tasks.service;

import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Task;

import java.util.HashMap;
import java.util.Map;

public class TaskManager {

    private static int IdCounter = 0;
    private final Map<Integer, Task> tasksById;
    private final Map<Integer, Epic> epicsById;
    private final Map<Integer, Subtask> subtasksById;

    public TaskManager() {
        tasksById = new HashMap<>();
        epicsById = new HashMap<>();
        subtasksById = new HashMap<>();
    }

    public int createTask(Task task) {
        int taskId = IdCounter++;

        task.setId(taskId);
        tasksById.put(taskId, task);

        return taskId;
    }

    public int createEpic(Epic epic) {
        int epicId = IdCounter++;

        epic.setId(epicId);
        epicsById.put(epicId, epic);

        return epicId;
    }

    public int createSubtask(Subtask subtask) {
        int subtaskId = IdCounter++;

        subtask.setId(subtaskId);
        subtasksById.put(subtaskId, subtask);

        return subtaskId;
    }


}
