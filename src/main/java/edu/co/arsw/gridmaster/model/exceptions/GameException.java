package edu.co.arsw.gridmaster.model.exceptions;

public class GameException extends GridMasterException {
    public GameException() {
        super();
    }

    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}