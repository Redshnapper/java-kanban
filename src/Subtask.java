
class Subtask extends Task {
    protected int epicID;

    public Subtask(String name, String description, String status, int epicID, int id) {
        super(name, description, status);
        this.epicID = epicID;
        this.id = id;
    }
    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicID=" + epicID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
