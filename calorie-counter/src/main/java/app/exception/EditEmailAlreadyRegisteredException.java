package app.exception;

public class EditEmailAlreadyRegisteredException extends RuntimeException{

    public EditEmailAlreadyRegisteredException() {
    }

    public EditEmailAlreadyRegisteredException(String message) {
        super(message);
    }
}
