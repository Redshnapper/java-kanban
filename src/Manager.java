import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private static int id = 0;
    StatusManager statusManager = new StatusManager();
    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    HashMap<Integer, Epic> epicMap = new HashMap<>();

    public Task createTask(String name, String description, String status) {
        id++;
        Task task = new Task(name, description, status, id);
        taskMap.put(id,task);
        return task;
    }

    public Task createSubtask(String name, String description, String status, Epic epic) {
        id++;
        Subtask subtask = new Subtask(name, description, status, epic.id, id);
        subtaskMap.put(id,subtask);
        statusManager.changeStatus(subtaskMap,epicMap,epic.id);
        return subtask;
    }

    public Epic createEpic(String name, String description, String status) {
        id++;
        Epic epic = new Epic(name, description, status, id);
        epicMap.put(id,epic);
        statusManager.changeStatus(subtaskMap,epicMap,id);
        return epic;
    }

    public ArrayList<String> returnTasksList() {
        ArrayList<String> tasksNames = new ArrayList<>();

        for (Task value : taskMap.values()) {
            tasksNames.add(value.name);
        }
        return tasksNames;
    }

    public ArrayList<String> returnEpicsList() {
        ArrayList<String> epicsNames = new ArrayList<>();

        for (Task value : epicMap.values()) {
            epicsNames.add(value.name);
        }
        return epicsNames;
    }

    public ArrayList<String> returnSubtasksList() {
        ArrayList<String> subtasksNames = new ArrayList<>();

        for (Task value : subtaskMap.values()) {
            subtasksNames.add(value.name);
        }
        return subtasksNames;
    }

    public void removeTasks() {
        taskMap.clear();
    }

    public void removeSubtasks() {
        subtaskMap.clear();
        statusManager.changeStatusNew(subtaskMap,epicMap);
    }

    public void removeEpics() {
        epicMap.clear();
        subtaskMap.clear();
    }

    public Task getTaskById(int id) {
        if (taskMap.containsKey(id)) {
            Task task = taskMap.get(id);
            System.out.println(task);
            return task;
        } else {
            System.out.println("Задачи с таким ID нет");
            return null;
        }
    }

    public Subtask getSubtaskById(int id) {
        if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.get(id);
            System.out.println(subtask);
            return subtask;
        } else {
            System.out.println("Подзадачи с таким ID нет");
            return null;
        }
    }

    public Epic getEpicById(int id) {
        if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            System.out.println(epic);
            return epic;
        } else {
            System.out.println("Эпика с таким ID нет");
            return null;
        }
    }

    public void updateTask(String name, String description, String status, int id) {
        if (taskMap.containsKey(id)) {
            Task updatedTask = new Task(name,description,status,id);
            taskMap.put(id, updatedTask);
        } else {
            System.out.println("Задачи с таким ID нет");
        }
    }

    public void updateSubtask(String name, String description, String status, int id) {
        if (subtaskMap.containsKey(id)) {
            Subtask oldSubtask = subtaskMap.get(id);
            Subtask updatedSubtask = new Subtask(name,description,status,oldSubtask.getEpicID(),id);
            subtaskMap.put(id, updatedSubtask);
            statusManager.changeStatus(subtaskMap,epicMap,updatedSubtask.getEpicID());
        } else {
            System.out.println("Подзадачи с таким ID нет");
        }
    }

    public void updateEpic(String name, String description, String status, int id) {
        if (epicMap.containsKey(id)) {
            Epic updatedEpic = new Epic(name,description,status,id);
            epicMap.put(id, updatedEpic);
            statusManager.changeStatus(subtaskMap,epicMap,id);
        } else {
            System.out.println("Эпика с таким ID нет");
        }
    }

    public void removeTaskByID(int id) {
        if (taskMap.containsKey(id)){
            taskMap.remove(id);
        } else {
            System.out.println("Задачи с таким ID нет");
        }
    }

    public void removeSubtaskByID(int id) {
        if (subtaskMap.containsKey(id)){
            int epicID = subtaskMap.get(id).epicID;
            subtaskMap.remove(id);
            statusManager.changeStatus(subtaskMap,epicMap,epicID);
        } else {
            System.out.println("Подзадачи с таким ID нет");
        }
    }

    public void removeEpicByID(int id) {
        ArrayList<Integer> subtasksIdToRemove = new ArrayList<>();

        if (epicMap.containsKey(id)){
            for (Subtask value : subtaskMap.values()) {
                if (value.getEpicID() == id) {
                    subtasksIdToRemove.add(value.getId());
                }
            }
            for (Integer idToRemove : subtasksIdToRemove) {
                subtaskMap.remove(idToRemove);
            }
            epicMap.remove(id);
        } else {
            System.out.println("Эпика с таким ID нет");
        }
    }

    public ArrayList<Subtask> getSubtasksByEpic(int id) {
        ArrayList<Subtask> subtasks = new ArrayList<>();

        for (Subtask subtask : subtaskMap.values()) {
            if (subtask.getEpicID() == id) {
                subtasks.add(subtask);
            }
        }
        return subtasks;
    }

}
