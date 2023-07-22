package ru.yandex.fedorov.kanban.web.exception;

public class ClientSaveException extends RuntimeException {

    public ClientSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
