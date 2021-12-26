package lk.grocery.pos.exception;

/* Me class eka bawitha karanna puluwan customer kenekta order ekakata onama dekata ekama de DB eke thibboth */
public class DuplicateIdentifierException extends Exception {
public DuplicateIdentifierException(String message){
    super(message);
}

    public DuplicateIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }
}
