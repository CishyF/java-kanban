package tasks;

import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Task;
import tasks.model.TaskStatus;
import tasks.service.TaskManager;

public class TaskManagerApplication {

    private final TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        new TaskManagerApplication().start();
    }

    public void start() {
        int idTask1 = taskManager.createTask(
                new Task("Название 1 задачи", "", TaskStatus.NEW)
        );

        int idTask2 = taskManager.createTask(
                new Task("Название 2 задачи", "", TaskStatus.IN_PROGRESS)
        );

        int idEpic1 = taskManager.createEpic(
                new Epic("Название 1 эпика", "", TaskStatus.NEW)
        );
        int idSubtask1Epic1 = taskManager.createSubtask(
                new Subtask("Название позадачи 1 эпика 1", "", TaskStatus.IN_PROGRESS, idEpic1)
        );

        int idEpic2 = taskManager.createEpic(
                new Epic("Название 2 эпика", "", TaskStatus.IN_PROGRESS)
        );
        int idSubtask1Epic2 = taskManager.createSubtask(
                new Subtask("Название подзадачи 1 эпика 2", "", TaskStatus.NEW, idEpic2)
        );
        int idSubtask2Epic2 = taskManager.createSubtask(
                new Subtask("Название подзадачи 2 эпика 2", "", TaskStatus.NEW, idEpic2)
        );

        System.out.printf("Задачи:%n%s%nЭпики:%n%s%nПодзадачи:%n%s%n%s%n",
                taskManager.getAllTasks(),
                taskManager.getAllEpics(),
                taskManager.getAllSubtasks(),
                "-".repeat(200)
        );
    }

}
