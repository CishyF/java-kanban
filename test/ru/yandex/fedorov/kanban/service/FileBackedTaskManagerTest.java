package ru.yandex.fedorov.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.fedorov.kanban.model.Epic;
import ru.yandex.fedorov.kanban.model.TaskStatus;

import java.io.File;
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
}
