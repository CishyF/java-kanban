package ru.yandex.fedorov.kanban.web.service;

import com.google.gson.*;
import ru.yandex.fedorov.kanban.model.Epic;
import ru.yandex.fedorov.kanban.model.Subtask;
import ru.yandex.fedorov.kanban.model.Task;
import ru.yandex.fedorov.kanban.service.FileBackedTaskManager;
import ru.yandex.fedorov.kanban.util.LocalDateTimeAdapter;
import ru.yandex.fedorov.kanban.web.client.KVTaskClient;
import ru.yandex.fedorov.kanban.web.exception.ClientLoadException;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {

    private static KVTaskClient client;
    private static final Gson gson = new GsonBuilder().serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private static final String[] KEYS = {"TASKS", "EPICS", "SUBTASKS", "HISTORY"};

    public HttpTaskManager(String URL) {
        super(new File("resources/data.csv"));
        client = new KVTaskClient(URL);
    }

    @Override
    protected void save() {
        List<Task> taskList = getTasks();
        List<Subtask> subtaskList = getSubtasks();
        List<Epic> epicList = getEpics();
        List<Task> history = getHistory();

        String tasksJson = gson.toJson(taskList);
        String subtasksJson = gson.toJson(subtaskList);
        String epicsJson = gson.toJson(epicList);
        String historyJson = gson.toJson(history);

        List<String> toSave = List.of(tasksJson, subtasksJson, epicsJson, historyJson);
        for (int i = 0; i < KEYS.length; i++) {
            client.put(KEYS[i], toSave.get(i));
        }
    }

    public static HttpTaskManager loadFromServer(String URL) {
        client = new KVTaskClient(URL);
        HttpTaskManager manager = new HttpTaskManager(URL);

        for (String key : KEYS) {
            String json = client.load(key);
            if (json == null || json.equals("")) {
                continue;
            }

            JsonElement element = JsonParser.parseString(json);
            if (!element.isJsonArray()) {
                throw new ClientLoadException("Ошибка при загрузке данных из клиента");
            }

            JsonArray array = element.getAsJsonArray();
            if (array.isEmpty()) {
                continue;
            }

            for (JsonElement e : array) {
                if (!e.isJsonObject()) {
                    throw new ClientLoadException("Ошибка при загрузке данных из клиента");
                }

                switch (key) {
                    case "TASKS":
                        Task task = gson.fromJson(e, Task.class);
                        manager.IdCounter = Math.max(task.getId(), manager.IdCounter);
                        manager.addAnyTask(task);
                        break;
                    case "EPICS":
                        Epic epic = gson.fromJson(e, Epic.class);
                        manager.IdCounter = Math.max(epic.getId(), manager.IdCounter);
                        manager.addAnyTask(epic);
                        break;
                    case "SUBTASKS":
                        Subtask subtask = gson.fromJson(e, Subtask.class);
                        manager.IdCounter = Math.max(subtask.getId(), manager.IdCounter);
                        manager.addAnyTask(subtask);
                        break;
                    case "HISTORY":
                        int id = e.getAsJsonObject().get("id").getAsInt();
                        Task t = manager.findAnyTask(id);
                        if (t != null) {
                            manager.history.add(t);
                        }
                }
            }
        }

        return manager;
    }

}
