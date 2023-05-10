package ru.yandex.fedorov.kanban.service;

import ru.yandex.fedorov.kanban.model.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {

    private Set<Task> history;

    public InMemoryHistoryManager() {
        history = new LinkedHashSet<>();
    }

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > 10) {
            history = new LinkedHashSet<>(new ArrayList<>(history).subList(1, 11));
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
