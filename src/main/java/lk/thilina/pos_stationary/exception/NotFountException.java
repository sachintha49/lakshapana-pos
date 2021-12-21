package lk.thilina.pos_stationary.exception;

public class NotFountException extends Exception {
    public NotFountException() {
        super();
    }

    public NotFountException(String message) {
        super(message);
    }

    public NotFountException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFountException(Throwable cause) {
        super(cause);
    }
}
