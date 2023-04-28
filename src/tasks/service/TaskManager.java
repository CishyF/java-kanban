package tasks.service;

import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Task;
import tasks.model.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {

    private int IdCounter = 1;
    private final Map<Integer, Task> tasksById;
    private final Map<Integer, Epic> epicsById;
    private final Map<Integer, Subtask> subtasksById;

    public TaskManager() {
        tasksById = new HashMap<>();
        epicsById = new HashMap<>();
        subtasksById = new HashMap<>();
    }

    public int createTask(Task task) {
        Objects.requireNonNull(task);
        int taskId = IdCounter++;

        task.setId(taskId);
        tasksById.put(taskId, task);

        return taskId;
    }

    public int createEpic(Epic epic) {
        Objects.requireNonNull(epic);
        int epicId = IdCounter++;

        epic.setId(epicId);
        epicsById.put(epicId, epic);

        updateEpicStatusById(epicId);

        return epicId;
    }

    public int createSubtask(Subtask subtask) {
        Objects.requireNonNull(subtask);
        int subtaskId = IdCounter++;

        subtask.setId(subtaskId);
        subtasksById.put(subtaskId, subtask);

        updateEpicStatusById(subtask.getEpicId());

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

        epic.setId(id);
        epicsById.put(id, epic);
    }

    public void updateSubtask(int id, Subtask subtask) {
        if (!subtasksById.containsKey(id))
            return;

        subtask.setId(id);
        subtasksById.put(id, subtask);

        updateEpicStatusById(subtask.getEpicId());
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
        epicsById.values().stream().mapToInt(Epic::getId).forEach(this::updateEpicStatusById);

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
        if (subtasksById.get(id) == null)
            return;

        int epicId = subtasksById.get(id).getEpicId();

        subtasksById.remove(id);

        updateEpicStatusById(epicId);
    }

    private void updateEpicStatusById(int id) {
        Epic epic = getEpicById(id);
        if (epic == null)
            return;

        List<Subtask> subtasksOfCurrentEpic = getAllSubtasks().stream()
                                                              .filter(subtask -> subtask.getEpicId() == id)
                                                              .collect(Collectors.toList());
        TaskStatus updatedStatus = getStatusOfEpicBySubtasks(subtasksOfCurrentEpic);

        Epic updatedEpic = new Epic(
            epic.getName(), epic.getDescription(), updatedStatus
        );
        subtasksOfCurrentEpic.forEach(subtask -> updatedEpic.addSubtaskId(subtask.getId()));

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
