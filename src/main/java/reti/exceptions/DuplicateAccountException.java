package reti.exceptions;

import reti.*;

/**
 * Eccezione lanciata quando un client tenta di registrare un
 * account già esistente
 */
public class DuplicateAccountException
    extends ServerErrorException
{
  static final long serialVersionUID = 1L;

  @Override
  public int getErrorCode() {
    return Message.ERROR_INVALID_USER;
  }
}