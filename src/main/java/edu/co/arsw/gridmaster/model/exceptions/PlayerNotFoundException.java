package edu.co.arsw.gridmaster.model.exceptions;

public class PlayerNotFoundException extends GridMasterException {
    public PlayerNotFoundException() {
        super("Player not found.");
    }
}
