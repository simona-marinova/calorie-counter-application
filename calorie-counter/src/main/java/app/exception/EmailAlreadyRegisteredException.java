package app.exception;

public class EmailAlreadyRegisteredException extends  RuntimeException{

    public EmailAlreadyRegisteredException() {
    }

    public EmailAlreadyRegisteredException(String message) {
        super(message);
    }
}
