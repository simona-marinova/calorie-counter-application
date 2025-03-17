package app.exception;

public class FoodAlreadyExistsException extends RuntimeException{

    public FoodAlreadyExistsException() {
    }

    public FoodAlreadyExistsException(String message) {
        super(message);
    }
}
