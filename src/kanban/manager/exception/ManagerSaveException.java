package kanban.manager.exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        System.out.println(message);
    }

    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}

