package lk.thilina.pos_stationary.exception;

public class FailedOperationException extends Exception{
    public FailedOperationException() {
        super();
    }

    public FailedOperationException(String message) {
        super(message);
    }

    public FailedOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedOperationException(Throwable cause) {
        super(cause);
    }

}
