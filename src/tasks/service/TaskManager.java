package tasks.service;

import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Task;
import tasks.model.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {

    private static int IdCounter = 1;
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

    public void updateTask(int id, Task task) {
        if (!tasksById.containsKey(id))
            return;

        task.setId(id);
        tasksById.put(id, task);
    }

    public void updateEpic(int id, Epic epic) {
        if (!epicsById.containsKey(id))
            return;

        List<Integer> subtasksOfCurrentEpicAtId = epicsById.get(id).getSubtasksId();
        subtasksOfCurrentEpicAtId.forEach(this::removeSubtaskById);

        epic.setId(id);
        epicsById.put(id, epic);
    }

    public void updateSubtask(int id, Subtask subtask) {
        if (!subtasksById.containsKey(id))
            return;

        subtask.setId(id);
        subtasksById.put(id, subtask);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasksById.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicsById.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasksById.values());
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

    private void updateEpicStatusById(int id) {
        Epic epic = getEpicById(id);
        if (epic == null)
            return;

        List<Subtask> subtasks = epic.getSubtasksId()
                                     .stream()
                                     .map(this::getSubtaskById)
                                     .collect(Collectors.toList());

        Epic updatedEpic = new Epic(
            epic.getName(), epic.getDescription(), getStatusOfEpicBySubtasks(subtasks)
        );

        updateEpic(epic.getId(), updatedEpic);
    }

    private TaskStatus getStatusOfEpicBySubtasks(List<Subtask> subtasks) {
        if (subtasks.isEmpty() || subtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.NEW))
            return TaskStatus.NEW;
        else if (subtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.DONE))
            return TaskStatus.DONE;
        return TaskStatus.IN_PROGRESS;
    }

}
