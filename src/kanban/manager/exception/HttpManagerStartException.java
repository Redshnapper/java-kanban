package kanban.manager.exception;

public class HttpManagerStartException extends RuntimeException {
    public HttpManagerStartException(String message) {
        System.out.println(message);
    }

}
