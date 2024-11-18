package edu.co.arsw.gridmaster.model.exceptions;

public class PlayerSaveException extends GridMasterException {
    public PlayerSaveException() {
        super("Unable to save player.");
    }
}
