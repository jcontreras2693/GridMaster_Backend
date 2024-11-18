package edu.co.arsw.gridmaster.model.exceptions;

public class GamePersistanceException extends GridMasterException {
    public GamePersistanceException() {
        super();
    }

    public GamePersistanceException(String message) {
        super(message);
    }

    public GamePersistanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
