package ru.yandex.fedorov.kanban.service;

import ru.yandex.fedorov.kanban.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);
    void remove(int id);
    List<Task> getHistory();
}
