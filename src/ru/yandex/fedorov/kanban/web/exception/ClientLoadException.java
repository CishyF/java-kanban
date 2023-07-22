package ru.yandex.fedorov.kanban.web.exception;

public class ClientLoadException extends RuntimeException {

    public ClientLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
