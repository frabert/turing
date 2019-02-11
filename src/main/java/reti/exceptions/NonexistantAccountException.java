package reti.exceptions;

import reti.*;

/**
 * Eccezione lanciata quando un client tenta di accedere con un
 * nome utente non esistente
 */
public class NonexistantAccountException
    extends ServerErrorException
{
  static final long serialVersionUID = 1L;

  @Override
  public int getErrorCode() {
    return Message.ERROR_INVALID_USER;
  }
}