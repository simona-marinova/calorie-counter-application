package app.exception;

public class MyRecipeAlreadyExistsException extends RuntimeException{

    public MyRecipeAlreadyExistsException() {
    }

    public MyRecipeAlreadyExistsException(String message) {
        super(message);
    }
}
