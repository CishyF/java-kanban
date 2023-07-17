package ru.yandex.fedorov.kanban.service;

import ru.yandex.fedorov.kanban.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;


public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> nodeById;
    private Node first = null;
    private Node last = null;

    public InMemoryHistoryManager() {
        nodeById = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        Objects.requireNonNull(task);

        removeNodeById(task.getId());
        addLast(task);

        nodeById.put(task.getId(), last);
    }

    @Override
    public void remove(int id) {
        removeNodeById(id);
    }

    @Override
    public List<Task> getHistory() {
        return asTasksList();
    }

    private static class Node {

        Node prev;
        Task data;
        Node next;

        Node(Node prev, Task data, Node next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }

    private void addLast(Task task) {
        Node tempNode;

        if (first == null) {
            tempNode = new Node(null, task, null);
            first = tempNode;
        } else {
            tempNode = new Node(last, task, null);
            last.next = tempNode;
        }
        last = tempNode;
    }

    private List<Task> asTasksList() {
        List<Task> tasks = new ArrayList<>();

        for (Node elem = first; elem != null; elem = elem.next) {
            tasks.add(elem.data);
        }

        return tasks;
    }

    private void removeNodeById(int id) {
        final Node tempNode = nodeById.remove(id);

        if (tempNode == null) {
            return;
        } else if (tempNode == first && tempNode == last) {
            first = null;
            last = null;
        } else if (tempNode == first) {
            first = tempNode.next;
            first.prev = null;
        } else if (tempNode == last) {
            last = tempNode.prev;
            last.next = null;
        } else {
            tempNode.next.prev = tempNode.prev;
            tempNode.prev.next = tempNode.next;
        }
    }

}
