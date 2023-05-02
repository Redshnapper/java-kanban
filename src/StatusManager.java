import java.util.ArrayList;
import java.util.HashMap;

public class StatusManager {
    private static final String newStatus = "NEW";
    private static final String inProgressStatus = "IN_PROGRESS";
    private static final String doneStatus = "DONE";

    public void changeStatusNew(HashMap<Integer,Subtask> subtasks, HashMap<Integer,Epic> epics) {
        if (subtasks.isEmpty()) {
            for (Epic value : epics.values()) {
                value.status = newStatus;
            }
        } else {
            System.out.println("Список подзадач не пуст!");
        }
    }

    public void changeStatus(HashMap<Integer,Subtask> subtasks, HashMap<Integer,Epic> epics, int epicID) {
        ArrayList<String> statuses = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicID() == epicID) {
                statuses.add(subtask.status);
            }
        }
        if (!statuses.contains(doneStatus) && !statuses.contains(inProgressStatus) || epics.isEmpty()){
            epics.get(epicID).status = newStatus;
        } else if (!statuses.contains(newStatus) && !statuses.contains(inProgressStatus)) {
            epics.get(epicID).status = doneStatus;
        } else {
            epics.get(epicID).status = inProgressStatus;
        }
    }
}


