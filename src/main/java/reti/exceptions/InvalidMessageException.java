package reti.exceptions;

import reti.*;

/**
 * Eccezione lanciata quando un client invia un messaggio non valido
 */
public class InvalidMessageException
    extends ServerErrorException
{
  static final long serialVersionUID = 1L;

  @Override
  public int getErrorCode() {
    return Message.ERROR_INVALID_MESSAGE;
  }
}