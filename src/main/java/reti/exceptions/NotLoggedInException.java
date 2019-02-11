package reti.exceptions;

import reti.*;

/**
 * Eccezione lanciata quando un client tenta operazioni diverse
 * dal login prima di aver effettuato l'accesso
 */
public class NotLoggedInException
    extends ServerErrorException
{
  static final long serialVersionUID = 1L;

  @Override
  public int getErrorCode() {
    return Message.ERROR_NOT_LOGGED;
  }
}