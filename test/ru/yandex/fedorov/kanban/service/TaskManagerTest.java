package ru.yandex.fedorov.kanban.service;

import org.junit.jupiter.api.Test;
import ru.yandex.fedorov.kanban.model.Epic;
import ru.yandex.fedorov.kanban.model.Subtask;
import ru.yandex.fedorov.kanban.model.Task;
import ru.yandex.fedorov.kanban.model.TaskStatus;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;

    /*
    Тесты для рассчета статуса Epic
    ----------------------------------------------------------------------------------------------------------------
    */

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

    /*
    Закончились тесты для рассчета статуса Epic
    ----------------------------------------------------------------------------------------------------------------
    */

    @Test
    public void subtaskShouldHasEpic() {
        final int idEpic = taskManager.createEpic(
            new Epic("Название эпика", " ", TaskStatus.NEW)
        );
        final int idSubtaskEpic = taskManager.createSubtask(
            new Subtask("Название позадачи 1 эпика", " ", TaskStatus.IN_PROGRESS, idEpic)
        );

        final Subtask subtask = taskManager.getSubtask(idSubtaskEpic);

        assertEquals(subtask.getEpicId(), idEpic);
    }

    @Test
    public void shouldCreateTask() {
        final Task task = new Task("Название задачи", " ", TaskStatus.IN_PROGRESS);

        final int idTask = taskManager.createTask(task);

        assertEquals(taskManager.getTasks().size(), 1);
        assertEquals(taskManager.getTask(idTask), task);
    }

    @Test
    public void shouldThrowExceptionWithNullAtTaskCreate() {
        final NullPointerException nlp = assertThrows(
            NullPointerException.class,
            () -> taskManager.createTask(null)
        );

        assertNull(nlp.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfEpicDoesNotExistAtSubtaskCreate() {
        final Subtask subtask = new Subtask("Название позадачи", " ", TaskStatus.IN_PROGRESS, 100);
        final RuntimeException e = assertThrows(
            RuntimeException.class,
            () -> taskManager.createSubtask(subtask)
        );

        assertEquals(e.getMessage(), "Эпик, для которого вы пытаетесь добавить подзадачу, еще не существует.");
    }

    @Test
    public void shouldNotUpdateIfTaskDoesNotExist() {
        final Task task = new Task("Название задачи", " ", TaskStatus.IN_PROGRESS);

        assertEquals(Collections.emptyList(), taskManager.getTasks());

        taskManager.updateTask(task);
        assertEquals(Collections.emptyList(), taskManager.getTasks());
    }

    @Test
    public void shouldThrowExceptionWithNullAtTaskUpdate() {
        final NullPointerException nlp = assertThrows(
            NullPointerException.class,
            () -> taskManager.updateTask(null)
        );

        assertNull(nlp.getMessage());
    }

    @Test
    public void shouldUpdateTask() {
        taskManager.createTask(
            new Task("Название задачи", " ", TaskStatus.IN_PROGRESS)
        );

        final Task task = new Task(1, "Название новой задачи", " ", TaskStatus.NEW);
        taskManager.updateTask(task);

        assertEquals(taskManager.getTasks().size(), 1);
        assertEquals(taskManager.getTask(1), task);
    }

    @Test
    public void shouldReturnSubtasksByEpicId() {
        final int idEpic = taskManager.createEpic(
            new Epic("Название эпика", " ", TaskStatus.NEW)
        );
        final int idSubtask1Epic = taskManager.createSubtask(
            new Subtask("Название позадачи 1 эпика", " ", TaskStatus.IN_PROGRESS, idEpic)
        );
        final int idSubtask2Epic = taskManager.createSubtask(
            new Subtask("Название позадачи 2 эпика", " ", TaskStatus.IN_PROGRESS, idEpic)
        );

        final Subtask subtask1 = taskManager.getSubtask(idSubtask1Epic);
        final Subtask subtask2 = taskManager.getSubtask(idSubtask2Epic);

        assertEquals(taskManager.getSubtasksByEpicId(idEpic), List.of(subtask1, subtask2));
    }

    @Test
    public void shouldReturnEmptyListIfEpicIdIsIncorrect() {
        final int idEpic = taskManager.createEpic(
                new Epic("Название эпика", " ", TaskStatus.NEW)
        );
        taskManager.createSubtask(
                new Subtask("Название позадачи 1 эпика", " ", TaskStatus.IN_PROGRESS, idEpic)
        );
        taskManager.createSubtask(
                new Subtask("Название позадачи 2 эпика", " ", TaskStatus.IN_PROGRESS, idEpic)
        );

        assertEquals(Collections.emptyList(), taskManager.getSubtasksByEpicId(101));
    }

    @Test
    public void shouldReturnTaskIfIdIsCorrect() {
        final Task task = new Task("Название задачи", " ", TaskStatus.IN_PROGRESS);
        final int id = taskManager.createTask(task);

        assertEquals(task, taskManager.getTask(id));
    }

    @Test
    public void shouldReturnNullIfTaskIdIsIncorrect() {
        final Task task = new Task("Название задачи", " ", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task);

        assertNull(taskManager.getTask(100));
    }

    @Test
    public void shouldReturnNullIfTasksAreEmpty() {
        assertNull(taskManager.getTask(1));
    }

    @Test
    public void shouldReturnEpicIfIdIsCorrect() {
        final Epic epic = new Epic("Название эпика", " ", TaskStatus.NEW);
        final int idEpic = taskManager.createEpic(epic);

        assertEquals(epic, taskManager.getEpic(idEpic));
    }

    @Test
    public void shouldReturnNullIfEpicIdIsIncorrect() {
        final Epic epic = new Epic("Название эпика", " ", TaskStatus.NEW);
        taskManager.createEpic(epic);

        assertNull(taskManager.getEpic(100));
    }

    @Test
    public void shouldReturnNullIfEpicsAreEmpty() {
        assertNull(taskManager.getEpic(1));
    }

    @Test
    public void shouldReturnSubtaskIfIdIsCorrect() {
        final int idEpic = taskManager.createEpic(
            new Epic("Название эпика", " ", TaskStatus.NEW)
        );
        final Subtask subtask = new Subtask("Название позадачи эпика", " ", TaskStatus.IN_PROGRESS, idEpic);
        final int subtaskId = taskManager.createSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtask(subtaskId));
    }

    @Test
    public void shouldReturnNullIfSubtaskIdIsIncorrect() {
        final Epic epic = new Epic("Название эпика", " ", TaskStatus.NEW);
        final int idEpic = taskManager.createEpic(epic);
        final Subtask subtask = new Subtask("Название позадачи эпика", " ", TaskStatus.IN_PROGRESS, idEpic);
        taskManager.createSubtask(subtask);

        assertNull(taskManager.getSubtask(100));
    }

    @Test
    public void shouldReturnNullIfSubtasksAreEmpty() {
        assertNull(taskManager.getSubtask(1));
    }

    @Test
    public void shouldRemoveTaskFromTasks() {
        final Task task = new Task("Название задачи", " ", TaskStatus.IN_PROGRESS);
        final int id = taskManager.createTask(task);

        assertEquals(Collections.singletonList(task), taskManager.getTasks());
        taskManager.removeTask(id);

        assertEquals(Collections.emptyList(), taskManager.getTasks());
    }

    @Test
    public void shouldRemoveEpicAndItsSubtasksFromEpicsAndSubtasks() {
        final Epic epic = new Epic("Название эпика", " ", TaskStatus.NEW);
        final int idEpic = taskManager.createEpic(epic);
        taskManager.createSubtask(
            new Subtask("Название позадачи эпика", " ", TaskStatus.IN_PROGRESS, idEpic)
        );

        assertEquals(Collections.singletonList(epic), taskManager.getEpics());
        taskManager.removeEpic(idEpic);

        assertEquals(Collections.emptyList(), taskManager.getEpics());
        assertEquals(Collections.emptyList(), taskManager.getSubtasks());
    }

    @Test
    public void shouldRemoveSubtaskFromSubtasksAndClearSubtasksIdInItsEpic() {
        final int idEpic = taskManager.createEpic(
            new Epic("Название эпика", " ", TaskStatus.NEW)
        );
        final Subtask subtask = new Subtask("Название позадачи эпика", " ", TaskStatus.IN_PROGRESS, idEpic);
        final int subtasksId = taskManager.createSubtask(subtask);

        assertEquals(Collections.singletonList(subtask), taskManager.getSubtasks());
        taskManager.removeSubtask(subtasksId);

        assertEquals(Collections.emptyList(), taskManager.getSubtasks());
        assertEquals(Collections.emptyList(), taskManager.getEpic(idEpic).getSubtasksId());
    }

}
