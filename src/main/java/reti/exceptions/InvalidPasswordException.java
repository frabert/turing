package reti.exceptions;

import reti.*;

/**
 * Eccezione lanciata quando un client tenta di registrarsi o di
 * accedere con una password non valida
 */
public class InvalidPasswordException
    extends ServerErrorException
{
  static final long serialVersionUID = 1L;

  @Override
  public int getErrorCode() {
    return Message.ERROR_INVALID_PASSWORD;
  }
}