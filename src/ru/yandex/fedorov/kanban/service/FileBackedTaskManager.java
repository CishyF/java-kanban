package ru.yandex.fedorov.kanban.service;

import ru.yandex.fedorov.kanban.exception.ManagerReadException;
import ru.yandex.fedorov.kanban.exception.ManagerSaveException;
import ru.yandex.fedorov.kanban.model.Epic;
import ru.yandex.fedorov.kanban.model.Subtask;
import ru.yandex.fedorov.kanban.model.Task;
import ru.yandex.fedorov.kanban.model.TaskStatus;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = Objects.requireNonNull(file);
        if (file.length() != 0) {
            loadFromFile();
        }
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.write("\n");

            // Соединение всех типов задач в один поток, сортировка по id, преобразование задач в строки
            List<String> allTypesOfTasksInString = Stream.concat(
                    Stream.concat(getTasks().stream(), getEpics().stream()), getSubtasks().stream()
            ).sorted(Comparator.comparingInt(Task::getId))
             .map(Object::toString)
             .collect(Collectors.toList());

            for (String task : allTypesOfTasksInString) {
                writer.write(task);
                writer.write("\n");
            }

            writer.write("\n");
            writer.write(historyToString(getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Чтение названий столбцов
            reader.readLine();

            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals(""))
                    break;
                loadTaskFromString(line);
            }

            String historyInString = reader.readLine();
            if (historyInString != null) {
                addHistoryInManagerFromString(historyInString);
            }
        } catch (IOException e) {
            throw new ManagerReadException();
        }
    }

    private void loadTaskFromString(String taskInString) {
        String[] taskFields = taskInString.split(",");

        int id = Integer.parseInt(taskFields[0]);
        if (id != IdCounter) {
            IdCounter = id;
        }

        TaskStatus status = TaskStatus.valueOf(taskFields[3]);
        switch (taskFields[1]) {
            case "TASK":
                Task task = new Task(taskFields[2], taskFields[4], status);
                task.setId(id);
                super.createTask(task);
                break;
            case "EPIC":
                Epic epic = new Epic(taskFields[2], taskFields[4], status);
                epic.setId(id);
                super.createEpic(epic);
                break;
            case "SUBTASK":
                Subtask subtask = new Subtask(taskFields[2], taskFields[4], status, Integer.parseInt(taskFields[5]));
                subtask.setId(id);
                super.createSubtask(subtask);
        }
    }

    private static String historyToString(List<Task> history) {
        return history.stream()
                .map(t -> String.valueOf(t.getId()))
                .collect(Collectors.joining(","));
    }

    private void addHistoryInManagerFromString(String history) {
        for (String tempId : history.split(",")) {
            int id = Integer.parseInt(tempId);

            if (super.getTask(id) == null) {
                if (super.getEpic(id) == null) {
                    super.getSubtask(id);
                }
            }
        }
    }

}
