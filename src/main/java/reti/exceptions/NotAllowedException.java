package reti.exceptions;

import reti.*;

/**
 * Eccezione lanciata quando un client tenta di effettuare
 * un'operazione per cui non è autorizzato.
 */
public class NotAllowedException
    extends ServerErrorException
{
  static final long serialVersionUID = 1L;

  @Override
  public int getErrorCode() {
    return Message.ERROR_NOT_ALLOWED;
  }
}