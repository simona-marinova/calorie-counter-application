package app.exception;

public class FoodItemAlreadyExistsException extends RuntimeException {

    public FoodItemAlreadyExistsException() {
    }

    public FoodItemAlreadyExistsException(String message) {
        super(message);
    }
}
