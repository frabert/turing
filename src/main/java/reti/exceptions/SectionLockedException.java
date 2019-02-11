package reti.exceptions;

import reti.*;

/**
 * Eccezione lanciata quando un client tenta di modificare una
 * sezione già in corso di modifiche da parte di altri client
 */
public class SectionLockedException
    extends ServerErrorException
{
  static final long serialVersionUID = 1L;

  @Override
  public int getErrorCode() {
    return Message.ERROR_SECTION_LOCKED;
  }
};