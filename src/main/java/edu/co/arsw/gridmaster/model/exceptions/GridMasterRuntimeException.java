package edu.co.arsw.gridmaster.model.exceptions;

public class GridMasterRuntimeException extends RuntimeException {
  public GridMasterRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public GridMasterRuntimeException(String message) {
    super(message);
  }

  public GridMasterRuntimeException(Throwable cause) {
    super(cause);
  }
}
