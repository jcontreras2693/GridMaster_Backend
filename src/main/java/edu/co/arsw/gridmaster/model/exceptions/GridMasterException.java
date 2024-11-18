package edu.co.arsw.gridmaster.model.exceptions;

public class GridMasterException extends Exception {
    public GridMasterException() {
        super();
    }

    public GridMasterException(String message) {
        super(message);
    }

    public GridMasterException(String message, Throwable cause) {
        super(message, cause);
    }
}