
class Epic extends Task {
    public Epic(String name, String description, String status, int id) {
        super(name, description, status, id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
