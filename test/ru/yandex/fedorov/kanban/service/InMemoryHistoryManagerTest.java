package ru.yandex.fedorov.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.fedorov.kanban.model.Task;
import ru.yandex.fedorov.kanban.model.TaskStatus;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager history;

    @BeforeEach
    public void beforeEach() {
        history = new InMemoryHistoryManager();
    }

    @Test
    public void shouldThrowsExceptionIfAddNull() {
        NullPointerException nlp = assertThrows(
            NullPointerException.class,
            () -> history.add(null)
        );
        assertNull(nlp.getMessage());
    }

    @Test
    public void shouldNotChangeAnythingIfRemoveIdThatDoesNotExist() {
        history.remove(101);
        assertEquals(Collections.emptyList(), history.getHistory());
    }

    @Test
    public void shouldDoesNotAddDuplicatedTask() {
        Task task = new Task(1, "Название задачи", " ", TaskStatus.NEW);
        history.add(task);
        assertEquals(Collections.singletonList(task), history.getHistory());
        history.add(task);
        assertEquals(Collections.singletonList(task), history.getHistory());
    }

    @Test
    public void shouldRemoveFirstElement() {
        Task task1 = new Task(1, "Название задачи 1", " ", TaskStatus.NEW);
        Task task2 = new Task(2, "Название задачи 2", " ", TaskStatus.NEW);
        Task task3 = new Task(3, "Название задачи 3", " ", TaskStatus.NEW);
        history.add(task1);
        history.add(task2);
        history.add(task3);

        history.remove(task1.getId());

        assertEquals(List.of(task2, task3), history.getHistory());
    }

    @Test
    public void shouldRemoveMidElement() {
        Task task1 = new Task(1, "Название задачи 1", " ", TaskStatus.NEW);
        Task task2 = new Task(2, "Название задачи 2", " ", TaskStatus.NEW);
        Task task3 = new Task(3, "Название задачи 3", " ", TaskStatus.NEW);
        history.add(task1);
        history.add(task2);
        history.add(task3);

        history.remove(task2.getId());

        assertEquals(List.of(task1, task3), history.getHistory());
    }

    @Test
    public void shouldRemoveLastElement() {
        Task task1 = new Task(1, "Название задачи 1", " ", TaskStatus.NEW);
        Task task2 = new Task(2, "Название задачи 2", " ", TaskStatus.NEW);
        Task task3 = new Task(3, "Название задачи 3", " ", TaskStatus.NEW);
        history.add(task1);
        history.add(task2);
        history.add(task3);

        history.remove(task3.getId());

        assertEquals(List.of(task1, task2), history.getHistory());
    }
}
