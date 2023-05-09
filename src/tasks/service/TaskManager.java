package tasks.service;

import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Task;

import java.util.List;

public interface TaskManager {

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getSubtasksByEpicId(int epicId);

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

}
