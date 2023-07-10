package ru.yandex.fedorov.kanban.model;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Task))
            return false;

        Task t = (Task) o;
        return id == t.id;
    }

    @Override
    public int hashCode() {

        return 31 * Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("%d,TASK,%s,%s,%s,", id, name, status.toString(), description);
    }
}
