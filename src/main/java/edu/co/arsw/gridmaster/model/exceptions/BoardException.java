package edu.co.arsw.gridmaster.model.exceptions;

public class BoardException extends GridMasterException {
    public BoardException() {
        super();
    }

    public BoardException(String message) {
        super(message);
    }

    public BoardException(String message, Throwable cause) {
        super(message, cause);
    }
}
