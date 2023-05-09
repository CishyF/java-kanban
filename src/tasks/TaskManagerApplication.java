package tasks;

import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Task;
import tasks.model.TaskStatus;
import tasks.service.TaskManager;
import tasks.util.Managers;

public class TaskManagerApplication {

    private final TaskManager taskManager = Managers.getDefault();

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

        printAllTypesOfTasks();

        Task updatedTask = new Task("Обновленная задача 1", "", TaskStatus.IN_PROGRESS);
        updatedTask.setId(idTask1);
        taskManager.updateTask(updatedTask);

        Subtask updatedSubtask1Epic1 =
                new Subtask("Обновленная подзадача 1 эпика 1", "", TaskStatus.DONE, idEpic1);
        updatedSubtask1Epic1.setId(idSubtask1Epic1);
        taskManager.updateSubtask(updatedSubtask1Epic1);

        Subtask updatedSubtask2Epic2 =
                new Subtask("Обновленная задача 2 эпика 2", "", TaskStatus.IN_PROGRESS, idEpic2);
        updatedSubtask2Epic2.setId(idSubtask2Epic2);
        taskManager.updateSubtask(updatedSubtask2Epic2);

        printAllTypesOfTasks();

        taskManager.removeTask(idTask1);
        taskManager.removeEpic(idEpic2);

        printAllTypesOfTasks();

    }

    public void printAllTypesOfTasks() {
        System.out.printf("Задачи:%n%s%nЭпики:%n%s%nПодзадачи:%n%s%n%s%n",
                taskManager.getTasks(),
                taskManager.getEpics(),
                taskManager.getSubtasks(),
                "-".repeat(200)
        );
    }

}
