package edu.co.arsw.gridmaster.model.exceptions;

public class GameSaveException extends GamePersistanceException {
    public GameSaveException() {
        super("Unable to save the game.");
    }
}

