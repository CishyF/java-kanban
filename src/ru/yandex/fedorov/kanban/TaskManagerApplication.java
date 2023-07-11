package ru.yandex.fedorov.kanban;

import ru.yandex.fedorov.kanban.service.FileBackedTaskManager;
import ru.yandex.fedorov.kanban.util.Managers;
import ru.yandex.fedorov.kanban.model.Epic;
import ru.yandex.fedorov.kanban.model.Subtask;
import ru.yandex.fedorov.kanban.model.Task;
import ru.yandex.fedorov.kanban.model.TaskStatus;
import ru.yandex.fedorov.kanban.service.TaskManager;

import java.io.File;

public class TaskManagerApplication {

    public static void main(String[] args) {
        new TaskManagerApplication().start();
    }

    public void start() {
        final TaskManager taskManager = Managers.getDefault();

        int idTask1 = taskManager.createTask(
                new Task("Название 1 задачи", " ", TaskStatus.NEW)
        );

        int idTask2 = taskManager.createTask(
                new Task("Название 2 задачи", " ", TaskStatus.IN_PROGRESS)
        );

        int idEpic1 = taskManager.createEpic(
                new Epic("Название 1 эпика", " ", TaskStatus.NEW)
        );
        int idSubtask1Epic1 = taskManager.createSubtask(
                new Subtask("Название позадачи 1 эпика 1", " ", TaskStatus.IN_PROGRESS, idEpic1)
        );

        int idEpic2 = taskManager.createEpic(
                new Epic("Название 2 эпика", " ", TaskStatus.IN_PROGRESS)
        );
        int idSubtask1Epic2 = taskManager.createSubtask(
                new Subtask("Название подзадачи 1 эпика 2", " ", TaskStatus.NEW, idEpic2)
        );
        int idSubtask2Epic2 = taskManager.createSubtask(
                new Subtask("Название подзадачи 2 эпика 2", " ", TaskStatus.NEW, idEpic2)
        );

        taskManager.getTask(idTask1);
        taskManager.getEpic(idEpic2);
        taskManager.getSubtask(idSubtask1Epic1);

        printInfo(taskManager);

        File file = new File("resources/data.csv");
        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);

        printInfo(newManager);

    }

    public void printInfo(TaskManager taskManager) {
        System.out.printf("Задачи:%n%s%nЭпики:%n%s%nПодзадачи:%n%s%nИстория:%n%s%n%s%n",
                taskManager.getTasks(),
                taskManager.getEpics(),
                taskManager.getSubtasks(),
                taskManager.getHistory(),
                "-".repeat(200)
        );
    }

}
