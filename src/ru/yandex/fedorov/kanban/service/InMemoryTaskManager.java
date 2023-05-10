package ru.yandex.fedorov.kanban.service;

import ru.yandex.fedorov.kanban.model.Epic;
import ru.yandex.fedorov.kanban.model.Subtask;
import ru.yandex.fedorov.kanban.model.Task;
import ru.yandex.fedorov.kanban.model.TaskStatus;
import ru.yandex.fedorov.kanban.util.Managers;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private int IdCounter = 1;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final HistoryManager history = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public int createTask(Task task) {
        Objects.requireNonNull(task);
        int taskId = IdCounter++;

        task.setId(taskId);
        tasks.put(taskId, task);

        return taskId;
    }

    @Override
    public int createEpic(Epic epic) {
        Objects.requireNonNull(epic);
        int epicId = IdCounter++;

        epic.setId(epicId);
        epics.put(epicId, epic);

        updateEpicStatus(epicId);

        return epicId;
    }

    @Override
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

    @Override
    public void updateTask(Task task) {
        Objects.requireNonNull(task);
        if (!tasks.containsKey(task.getId()))
            return;

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Objects.requireNonNull(epic);
        if (!epics.containsKey(epic.getId()))
            return;

        Epic savedEpic = epics.get(epic.getId());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Objects.requireNonNull(subtask);
        if (!subtasks.containsKey(subtask.getId()) || !epics.containsKey(subtask.getEpicId()))
            return;

        subtasks.put(subtask.getId(), subtask);

        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        return subtasks.values().stream().filter(subtask -> subtask.getEpicId() == epicId).collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);

        if (task != null)
            history.add(task);

        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);

        if (epic != null)
            history.add(epic);

        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);

        if (subtask != null)
            history.add(subtask);

        return subtask;
    }

    @Override
    public void removeTasks() {
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epics.values()) {
            List<Integer> subtasksId = epic.getSubtasksId();

            subtasksId.forEach(this::removeSubtask);
        }

        epics.clear();
    }

    @Override
    public void removeSubtasks() {
        epics.values().stream().mapToInt(Epic::getId).forEach(this::updateEpicStatus);

        subtasks.clear();
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = Objects.requireNonNull(epics.get(id));

        List<Integer> subtasksId = epic.getSubtasksId();
        subtasksId.forEach(this::removeSubtask);

        epics.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasks.get(id) == null)
            return;

        int epicId = subtasks.get(id).getEpicId();

        subtasks.remove(id);

        updateEpicStatus(epicId);
    }

    private void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
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

        epics.put(updatedEpic.getId(), updatedEpic);
    }

    private TaskStatus getStatusOfEpic(List<Subtask> subtasks) {
        if (subtasks.isEmpty() || subtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.NEW))
            return TaskStatus.NEW;
        else if (subtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.DONE))
            return TaskStatus.DONE;
        return TaskStatus.IN_PROGRESS;
    }
}
