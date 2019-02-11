package reti.exceptions;

import reti.*;

/**
 * Eccezione lanciata quando un client tenta di fare login
 * con un nome utente che ha già effettuato l'accessso
 */
public class AlreadyLoggedException
    extends ServerErrorException
{
  static final long serialVersionUID = 1L;

  @Override
  public int getErrorCode() {
    return Message.ERROR_ALREADY_LOGGED;
  }
};