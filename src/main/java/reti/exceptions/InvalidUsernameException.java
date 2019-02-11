package reti.exceptions;

import reti.*;

/**
 * Eccezione lanciata quando un utente tenta di registrarsi o di
 * accedere con un nome utente non valido
 */
public class InvalidUsernameException
    extends ServerErrorException
{
  static final long serialVersionUID = 1L;

  @Override
  public int getErrorCode() {
    return Message.ERROR_INVALID_USER;
  }
}