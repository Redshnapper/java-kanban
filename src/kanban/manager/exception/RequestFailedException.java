package kanban.manager.exception;

public class RequestFailedException extends RuntimeException {

    public RequestFailedException() {
    }
    public RequestFailedException(String message) {
        System.out.println(message);
    }

    public RequestFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
