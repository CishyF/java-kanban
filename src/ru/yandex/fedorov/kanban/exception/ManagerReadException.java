package ru.yandex.fedorov.kanban.exception;

public class ManagerReadException extends RuntimeException {

    public ManagerReadException(String message, Throwable cause) {
        super(message, cause);
    }

}
