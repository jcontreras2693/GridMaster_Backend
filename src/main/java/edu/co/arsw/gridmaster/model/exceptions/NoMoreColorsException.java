package edu.co.arsw.gridmaster.model.exceptions;

public class NoMoreColorsException extends RuntimeException {
    public NoMoreColorsException() {
        super("There are no more available colors");
    }
}
