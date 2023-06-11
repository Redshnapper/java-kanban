package kanban.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Long> subtaskId = new ArrayList<>();

    public Epic(String name, String description, TaskStatuses status) {
        super(name, description, status);
    }

    public List<Long> getSubtaskId() {
        return subtaskId;
    }

    public void removeSubtask(long id){
        if (!subtaskId.contains(id)) {
            System.out.println("Такого айди тут нет");
        } else {
            subtaskId.remove(id);
        }
    }

    public void clearSubsId() {
        subtaskId.clear();
    }

    public void setSubtaskId(long subId) {
        subtaskId.add(subId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return subtaskId == epic.subtaskId;
    }

    @Override
    public String toString() {
        return "kanban.model.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
