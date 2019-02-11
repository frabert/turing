package reti.exceptions;

import reti.*;

/**
 * Eccezione lanciata quando un utente tenta di accedere ad una
 * sezione non valida di un documento
 */
public class InvalidSectionException
    extends ServerErrorException
{
  static final long serialVersionUID = 1L;

  @Override
  public int getErrorCode() {
    return Message.ERROR_INVALID_SECTION;
  }
}