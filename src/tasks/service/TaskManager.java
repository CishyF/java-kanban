package tasks.service;

import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public Task getTaskById(int id) {
        return tasksById.get(id);
    }

    public Epic getEpicById(int id) {
        return epicsById.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasksById.get(id);
    }



    public void removeAllTasks() {
        tasksById.clear();
    }

    public void removeAllEpics() {
        for (Epic epic : epicsById.values()) {

            List<Integer> subtasksId = epic.getSubtasksId();

            subtasksId.forEach(this::removeSubtaskById);
        }

        epicsById.clear();
    }

    public void removeAllSubtasks() {
        subtasksById.clear();
    }

    public void removeTaskById(int id) {
        tasksById.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = Objects.requireNonNull(getEpicById(id));

        List<Integer> subtasksId = epic.getSubtasksId();
        subtasksId.forEach(this::removeSubtaskById);

        epicsById.remove(id);
    }

    public void removeSubtaskById(int id) {
        subtasksById.remove(id);
    }
}
