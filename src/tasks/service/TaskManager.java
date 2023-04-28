package tasks.service;

import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Task;
import tasks.model.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {

    private int IdCounter = 1;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public int createTask(Task task) {
        Objects.requireNonNull(task);
        int taskId = IdCounter++;

        task.setId(taskId);
        tasks.put(taskId, task);

        return taskId;
    }

    public int createEpic(Epic epic) {
        Objects.requireNonNull(epic);
        int epicId = IdCounter++;

        epic.setId(epicId);
        epics.put(epicId, epic);

        updateEpicStatus(epicId);

        return epicId;
    }

    public int createSubtask(Subtask subtask) {
        Objects.requireNonNull(subtask);
        if (!epics.containsKey(subtask.getEpicId()))
            throw new RuntimeException("Эпик, для которого вы пытаетесь добавить подзадачу, еще не существует.");

        int subtaskId = IdCounter++;

        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);

        updateEpicStatus(subtask.getEpicId());

        return subtaskId;
    }

    public void updateTask(Task task) {
        Objects.requireNonNull(task);
        if (!tasks.containsKey(task.getId()))
            return;

        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        Objects.requireNonNull(epic);
        if (!epics.containsKey(epic.getId()))
            return;

        Epic savedEpic = epics.get(epic.getId());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    public void updateSubtask(Subtask subtask) {
        Objects.requireNonNull(subtask);
        if (!subtasks.containsKey(subtask.getId()))
            return;
        if (!epics.containsKey(subtask.getEpicId()))
            return;

        subtasks.put(subtask.getId(), subtask);

        updateEpicStatus(subtask.getEpicId());
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }



    public void removeTasks() {
        tasks.clear();
    }

    public void removeEpics() {
        for (Epic epic : epics.values()) {
            List<Integer> subtasksId = epic.getSubtasksId();

            subtasksId.forEach(this::removeSubtask);
        }

        epics.clear();
    }

    public void removeSubtasks() {
        epics.values().stream().mapToInt(Epic::getId).forEach(this::updateEpicStatus);

        subtasks.clear();
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        Epic epic = Objects.requireNonNull(getEpic(id));

        List<Integer> subtasksId = epic.getSubtasksId();
        subtasksId.forEach(this::removeSubtask);

        epics.remove(id);
    }

    public void removeSubtask(int id) {
        if (subtasks.get(id) == null)
            return;

        int epicId = subtasks.get(id).getEpicId();

        subtasks.remove(id);

        updateEpicStatus(epicId);
    }

    private void updateEpicStatus(int id) {
        Epic epic = getEpic(id);
        if (epic == null)
            return;

        List<Subtask> subtasksOfCurrentEpic = getSubtasks().stream()
                                                              .filter(subtask -> subtask.getEpicId() == id)
                                                              .collect(Collectors.toList());
        TaskStatus updatedStatus = getStatusOfEpic(subtasksOfCurrentEpic);

        Epic updatedEpic = new Epic(
            epic.getName(), epic.getDescription(), updatedStatus
        );
        updatedEpic.setId(epic.getId());
        subtasksOfCurrentEpic.forEach(subtask -> updatedEpic.addSubtaskId(subtask.getId()));

        updateEpic(updatedEpic);
    }

    private TaskStatus getStatusOfEpic(List<Subtask> subtasks) {
        if (subtasks.isEmpty() || subtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.NEW))
            return TaskStatus.NEW;
        else if (subtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.DONE))
            return TaskStatus.DONE;
        return TaskStatus.IN_PROGRESS;
    }

}
