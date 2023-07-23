package ru.yandex.fedorov.kanban;

import ru.yandex.fedorov.kanban.web.server.HttpTaskServer;
import ru.yandex.fedorov.kanban.web.server.KVServer;

import java.io.IOException;

public class TaskManagerApplication {

    public static void main(String[] args) {
        try {
            new KVServer().start();
            new HttpTaskServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
