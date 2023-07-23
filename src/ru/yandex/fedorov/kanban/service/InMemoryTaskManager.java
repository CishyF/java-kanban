package ru.yandex.fedorov.kanban.service;

import ru.yandex.fedorov.kanban.exception.TaskValidationException;
import ru.yandex.fedorov.kanban.model.Epic;
import ru.yandex.fedorov.kanban.model.Subtask;
import ru.yandex.fedorov.kanban.model.Task;
import ru.yandex.fedorov.kanban.model.TaskStatus;
import ru.yandex.fedorov.kanban.util.Managers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected int IdCounter = 0;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final HistoryManager history;
    private final Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        history = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>((t1, t2) -> {
            if (t1.equals(t2)) {
                return 0;
            }
            LocalDateTime startTime1 = t1.getStartTime();
            LocalDateTime startTime2 = t2.getStartTime();
            if (startTime1 == null) {
                return 1;
            }
            return startTime1.compareTo(startTime2);
        });
    }

    @Override
    public int createTask(Task task) {
        Objects.requireNonNull(task);
        int taskId = ++IdCounter;

        task.setId(taskId);
        tasks.put(taskId, task);

        addToPrioritized(task);

        return taskId;
    }

    @Override
    public int createEpic(Epic epic) {
        Objects.requireNonNull(epic);
        int epicId = ++IdCounter;

        epic.setId(epicId);
        epics.put(epicId, epic);

        updateEpicStatus(epicId);
        updateEpicDuration(epicId);

        return epicId;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        Objects.requireNonNull(subtask);
        if (!epics.containsKey(subtask.getEpicId()))
            throw new RuntimeException("Эпик, для которого вы пытаетесь добавить подзадачу, еще не существует.");

        int subtaskId = ++IdCounter;

        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);

        addToPrioritized(subtask);

        updateEpicStatus(subtask.getEpicId());
        updateEpicDuration(subtask.getEpicId());

        return subtaskId;
    }

    @Override
    public void updateTask(Task task) {
        Objects.requireNonNull(task);
        if (!tasks.containsKey(task.getId()))
            return;

        tasks.put(task.getId(), task);

        addToPrioritized(task);
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
        if (!subtasks.containsKey(subtask.getId()) || !epics.containsKey(subtask.getEpicId())) {
            return;
        }

        subtasks.put(subtask.getId(), subtask);

        addToPrioritized(subtask);

        updateEpicStatus(subtask.getEpicId());
        updateEpicDuration(subtask.getEpicId());
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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

        if (task != null) {
            history.add(task);
        }

        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);

        if (epic != null) {
            history.add(epic);
        }

        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);

        if (subtask != null) {
            history.add(subtask);
        }

        return subtask;
    }

    @Override
    public void removeTasks() {
        tasks.keySet().forEach(history::remove);
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epics.values()) {
            List<Integer> subtasksId = epic.getSubtasksId();

            subtasksId.forEach(history::remove);
            subtasksId.forEach(this::removeSubtask);
        }

        epics.keySet().forEach(history::remove);
        epics.clear();
    }

    @Override
    public void removeSubtasks() {
        epics.values().stream().mapToInt(Epic::getId).peek(this::updateEpicStatus).forEach(this::updateEpicDuration);

        subtasks.keySet().forEach(history::remove);
        subtasks.clear();
    }

    @Override
    public void removeTask(int id) {
        if (tasks.get(id) == null) {
            return;
        }

        history.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        if (epics.get(id) == null) {
            return;
        }

        List<Integer> subtasksId = epics.get(id).getSubtasksId();
        subtasksId.forEach(history::remove);
        subtasksId.forEach(this::removeSubtask);

        history.remove(id);
        epics.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasks.get(id) == null) {
            return;
        }

        int epicId = subtasks.get(id).getEpicId();

        history.remove(id);
        subtasks.remove(id);

        updateEpicStatus(epicId);
        updateEpicDuration(epicId);
    }

    private void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }

        List<Subtask> subtasks = getSubtasksByEpicId(id);
        TaskStatus updatedStatus = getStatusOfEpic(subtasks);

        epic.setStatus(updatedStatus);
        epic.setSubtasksId(subtasks.stream().map(Subtask::getId).collect(Collectors.toList()));
    }

    private TaskStatus getStatusOfEpic(List<Subtask> subtasks) {
        if (subtasks.isEmpty() || subtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.NEW)) {
            return TaskStatus.NEW;
        } else if (subtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.DONE)) {
            return TaskStatus.DONE;
        }
        return TaskStatus.IN_PROGRESS;
    }

    private void updateEpicDuration(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }

        List<Subtask> subtasks = getSubtasksByEpicId(id);
        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(0);
            epic.setEndTime(null);
            return;
        }

        LocalDateTime startTime = LocalDateTime.MAX;
        LocalDateTime endTime = LocalDateTime.MIN;
        long duration = 0;

        for (Subtask subtask : subtasks) {
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();
            long subtaskDuration = subtask.getDuration();

            if (subtaskStartTime != null && subtaskStartTime.isBefore(startTime)) {
                startTime = subtaskStartTime;
            }
            if (subtaskEndTime != null && subtaskEndTime.isAfter(endTime)) {
                endTime = subtaskEndTime;
            }

            duration += subtaskDuration;
        }

        epic.setStartTime(startTime == LocalDateTime.MAX ? null : startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime == LocalDateTime.MIN ? null : endTime);
    }

    protected void addToPrioritized(Task task) {
        final LocalDateTime startTime = task.getStartTime();
        final LocalDateTime endTime = task.getEndTime();

        for (Task t : prioritizedTasks) {
            final LocalDateTime existStart = t.getStartTime();
            final LocalDateTime existEnd = t.getEndTime();
            if (task.equals(t) || existStart == null || startTime == null) {
                continue;
            }
            if (!endTime.isAfter(existStart)) {
                continue;
            }
            if (!existEnd.isAfter(startTime)) {
                continue;
            }

            throw new TaskValidationException("Задача " + task.getId() + " пересекается по времени с " + t.getId());
        }

        prioritizedTasks.add(task);
    }
}
