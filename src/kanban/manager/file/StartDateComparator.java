package kanban.manager.file;

import kanban.model.Task;

import java.util.Comparator;

public class StartDateComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if (o1.getStartDate().isBefore(o2.getStartDate())) {
            return -1;
        } else if (o1.getStartDate().isAfter(o2.getStartDate())) {
            return 1;
        } else {
            return 1;
        }
    }

}
