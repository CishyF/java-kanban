package ru.yandex.fedorov.kanban.service;

import ru.yandex.fedorov.kanban.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList history;
    private final HashMap<Integer, CustomLinkedList.Node> nodeById;

    public InMemoryHistoryManager() {
        history = new CustomLinkedList();
        nodeById = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (!nodeById.containsKey(task.getId())) {
            nodeById.put(task.getId(), history.addLast(task));
        }
    }

    @Override
    public void remove(int id) {
        if (nodeById.containsKey(id)) {
            history.removeNode(nodeById.get(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.asTasksList();
    }

    private static class CustomLinkedList {

        private Node first = null;
        private Node last = null;

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

        Node addLast(Task task) {
            Node tempNode;

            if (first == null) {
                tempNode = new Node(null, task, null);
                first = tempNode;
                last = tempNode;
            }
            tempNode = new Node(last, task, null);
            last.next = tempNode;
            last = tempNode;

            return tempNode;
        }

        List<Task> asTasksList() {
            List<Task> tasks = new ArrayList<>();

            for (Node elem = first; elem != null; elem = elem.next) {
                tasks.add(elem.data);
            }

            return tasks;
        }

        void removeNode(Node node) {
            if (node == first) {
                first = node.next;
                first.prev = null;
            } else if (node == last) {
                last = node.prev;
                last.next = null;
            } else {
                node.next.prev = node.prev;
                node.prev.next = node.next;
            }
        }

    }
}
