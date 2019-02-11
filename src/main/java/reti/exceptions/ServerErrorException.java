package reti.exceptions;

/**
 * Generica eccezione causata da un client
 */
public abstract class ServerErrorException
    extends Exception
{
  static final long serialVersionUID = 1L;

  public abstract int getErrorCode();
}