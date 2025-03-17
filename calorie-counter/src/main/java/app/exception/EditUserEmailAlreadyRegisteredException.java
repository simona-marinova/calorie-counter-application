package app.exception;

public class EditUserEmailAlreadyRegisteredException extends RuntimeException{

    public EditUserEmailAlreadyRegisteredException() {
    }

    public EditUserEmailAlreadyRegisteredException(String message) {
        super(message);
    }
}
