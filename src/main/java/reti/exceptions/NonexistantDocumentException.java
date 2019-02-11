package reti.exceptions;

import reti.*;

/**
 * Eccezione lanciata quando un client tenta di accedere ad un
 * documento non esistente
 */
public class NonexistantDocumentException
    extends ServerErrorException
{
  static final long serialVersionUID = 1L;

  @Override
  public int getErrorCode() {
    return Message.ERROR_INVALID_DOCUMENT;
  }
}