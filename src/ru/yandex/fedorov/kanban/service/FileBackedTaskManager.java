package ru.yandex.fedorov.kanban.service;

import ru.yandex.fedorov.kanban.exception.ManagerReadException;
import ru.yandex.fedorov.kanban.exception.ManagerSaveException;
import ru.yandex.fedorov.kanban.model.Epic;
import ru.yandex.fedorov.kanban.model.Subtask;
import ru.yandex.fedorov.kanban.model.Task;
import ru.yandex.fedorov.kanban.util.CSVTaskUtils;

import java.io.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = Objects.requireNonNull(file);
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
            writer.write(CSVTaskUtils.getHeader());
            writer.newLine();

            // Соединение всех типов задач в один поток, сортировка по id, преобразование задач в строки
            List<String> allTypesOfTasksInString = Stream.of(
                    tasks.values(), epics.values(), subtasks.values()
            ).flatMap(Collection::stream)
             .sorted(Comparator.comparingInt(Task::getId))
             .map(CSVTaskUtils::taskToString)
             .collect(Collectors.toList());

            for (String task : allTypesOfTasksInString) {
                writer.write(task);
                writer.newLine();
            }

            writer.newLine();
            writer.write(CSVTaskUtils.historyToString(history));
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: " + file.getName(), e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Чтение названий столбцов
            reader.readLine();

            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals("")) {
                    break;
                }

                Task task = CSVTaskUtils.taskFromString(line);
                manager.IdCounter = task.getId();

                manager.addAnyTask(task);
            }

            String historyInString = reader.readLine();
            if (historyInString != null) {
                for (int id : CSVTaskUtils.historyFromString(historyInString)) {
                    Task task = manager.findAnyTask(id);
                    if (task != null) {
                        manager.history.add(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerReadException("Can't read from file: " + file.getName(), e);
        }

        return manager;
    }

    private void addAnyTask(Task task) {
        switch (task.getType()) {
            case TASK:
                tasks.put(task.getId(), task);
                addToPrioritized(task);
                break;
            case EPIC:
                epics.put(task.getId(), (Epic) task);
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
                subtasks.put(task.getId(), (Subtask) task);
                addToPrioritized(subtask);
        }
    }

    private Task findAnyTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        } else if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }

        return null;
    }

}
