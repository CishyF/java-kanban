package tasks.model;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;

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

    public void setStatus(TaskStatus status) {
        this.status = status;
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
}
