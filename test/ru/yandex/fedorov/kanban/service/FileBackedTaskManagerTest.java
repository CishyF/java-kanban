package ru.yandex.fedorov.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.fedorov.kanban.model.Epic;
import ru.yandex.fedorov.kanban.model.Subtask;
import ru.yandex.fedorov.kanban.model.TaskStatus;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTaskManager(new File("resources/data.csv"));
    }

    @Test
    public void shouldBeSameAfterLoadingWithoutAnyTasks() {
        taskManager.getTask(1);

        final TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(new File("resources/data.csv"));
        assertEquals(Collections.emptyList(), newTaskManager.getTasks());
        assertEquals(Collections.emptyList(), newTaskManager.getEpics());
        assertEquals(Collections.emptyList(), newTaskManager.getSubtasks());
        // Проверка корректной загрузки пустой истории
        assertEquals(Collections.emptyList(), newTaskManager.getHistory());
    }

    @Test
    public void shouldBeSameAfterLoadingWithEpicWithoutSubtasks() {
        final Epic epic = new Epic("Название эпика", " ", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);
        taskManager.getEpic(epicId);

        final TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(new File("resources/data.csv"));
        final Epic epicFromFile = newTaskManager.getEpics().get(0);
        assertEquals(epicId, epicFromFile.getId());
        assertEquals(epic, epicFromFile);
        assertEquals(Collections.singletonList(epic), newTaskManager.getHistory());
    }

    @Test
    public void shouldBeSameEpicAfterDeserialization() {
        final int epicId = taskManager.createEpic(
            new Epic("Название эпика", " ", TaskStatus.NEW)
        );

        final LocalDateTime startTimeFirstSubtask = LocalDateTime.now();
        taskManager.createSubtask(
            new Subtask("Название позадачи 1 эпика", " ", TaskStatus.DONE, startTimeFirstSubtask, 10, epicId)
        );

        assertEquals(taskManager.getEpic(epicId).getStartTime(), startTimeFirstSubtask);
        assertEquals(taskManager.getEpic(epicId).getDuration(), 10);
        assertEquals(taskManager.getEpic(epicId).getEndTime(), startTimeFirstSubtask.plusMinutes(10));

        final LocalDateTime startTimeSecondSubtask = LocalDateTime.now().plusMinutes(20);
        taskManager.createSubtask(
            new Subtask("Название позадачи 2 эпика", " ", TaskStatus.DONE, startTimeSecondSubtask, 10, epicId)
        );

        assertEquals(taskManager.getEpic(epicId).getStartTime(), startTimeFirstSubtask);
        assertEquals(taskManager.getEpic(epicId).getDuration(), 20);
        assertEquals(taskManager.getEpic(epicId).getEndTime(), startTimeSecondSubtask.plusMinutes(10));

        final Epic epic = taskManager.getEpic(epicId);

        final TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(new File("resources/data.csv"));

        assertEquals(epic, newTaskManager.getEpic(epicId));
    }
}
