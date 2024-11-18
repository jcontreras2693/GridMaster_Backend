package edu.co.arsw.gridmaster.model.exceptions;

public class GameNotFoundException extends GamePersistanceException {
    public GameNotFoundException() {
        super("Game not found.");
    }
}
