package ru.yandex.fedorov.kanban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.fedorov.kanban.service.InMemoryTaskManager;
import ru.yandex.fedorov.kanban.service.TaskManager;

import java.util.Collections;
import java.util.List;

public class EpicStatusTest {

    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldBeNewIfSubtasksListIsEmpty() {
        final int epicId = taskManager.createEpic(
            new Epic("Название эпика", " ", TaskStatus.IN_PROGRESS)
        );

        final Epic epic = taskManager.getEpic(epicId);

        assertEquals(Collections.emptyList(), epic.getSubtasksId());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeNewIfSubtasksAreNew() {
        final int epicId = taskManager.createEpic(
            new Epic("Название эпика", " ", TaskStatus.IN_PROGRESS)
        );
        final int subtaskId1 = taskManager.createSubtask(
                new Subtask("Название позадачи 1 эпика", " ", TaskStatus.NEW, epicId)
        );
        final int subtaskId2 = taskManager.createSubtask(
                new Subtask("Название позадачи 2 эпика", " ", TaskStatus.NEW, epicId)
        );

        final Epic epic = taskManager.getEpic(epicId);

        assertEquals(List.of(subtaskId1, subtaskId2), epic.getSubtasksId());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeDoneIfSubtasksAreDone() {
        final int epicId = taskManager.createEpic(
                new Epic("Название эпика", " ", TaskStatus.IN_PROGRESS)
        );
        final int subtaskId1 = taskManager.createSubtask(
                new Subtask("Название позадачи 1 эпика", " ", TaskStatus.DONE, epicId)
        );
        final int subtaskId2 = taskManager.createSubtask(
                new Subtask("Название позадачи 2 эпика", " ", TaskStatus.DONE, epicId)
        );

        final Epic epic = taskManager.getEpic(epicId);

        assertEquals(List.of(subtaskId1, subtaskId2), epic.getSubtasksId());
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void shouldBeInProgressIfSubtasksAreNewAndDone() {
        final int epicId = taskManager.createEpic(
                new Epic("Название эпика", " ", TaskStatus.NEW)
        );
        final int subtaskId1 = taskManager.createSubtask(
                new Subtask("Название позадачи 1 эпика", " ", TaskStatus.NEW, epicId)
        );
        final int subtaskId2 = taskManager.createSubtask(
                new Subtask("Название позадачи 2 эпика", " ", TaskStatus.DONE, epicId)
        );

        final Epic epic = taskManager.getEpic(epicId);

        assertEquals(List.of(subtaskId1, subtaskId2), epic.getSubtasksId());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldBeInProgressIfSubtasksAreInProgress() {
        final int epicId = taskManager.createEpic(
                new Epic("Название эпика", " ", TaskStatus.NEW)
        );
        final int subtaskId1 = taskManager.createSubtask(
                new Subtask("Название позадачи 1 эпика", " ", TaskStatus.IN_PROGRESS, epicId)
        );
        final int subtaskId2 = taskManager.createSubtask(
                new Subtask("Название позадачи 2 эпика", " ", TaskStatus.IN_PROGRESS, epicId)
        );

        final Epic epic = taskManager.getEpic(epicId);

        assertEquals(List.of(subtaskId1, subtaskId2), epic.getSubtasksId());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}
