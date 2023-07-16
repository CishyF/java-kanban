package ru.yandex.fedorov.kanban.service;

import org.junit.jupiter.api.BeforeEach;

import java.io.File;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTaskManager(new File("resources/data.csv"));
    }
}
