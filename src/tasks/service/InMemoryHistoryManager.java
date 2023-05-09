package tasks.service;

import tasks.model.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {

    private Set<Task> history;

    public InMemoryHistoryManager() {
        history = new LinkedHashSet<>();
    }

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > 10)
            history = new LinkedHashSet<>(new ArrayList<>(history).subList(1, 10));
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
