package edu.co.arsw.gridmaster.model.exceptions;

public class PlayerException extends GridMasterException {
  public PlayerException() {
    super();
  }

  public PlayerException(String message) {
    super(message);
  }

  public PlayerException(String message, Throwable cause) {
    super(message, cause);
  }
}