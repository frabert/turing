package reti.exceptions;

import reti.*;

/**
 * Eccezione lanciata quando un client tenta di creare un documento
 * con nome uguale ad un altro già esistente
 */
public class DuplicateDocumentException
    extends ServerErrorException
{
  static final long serialVersionUID = 1L;

  @Override
  public int getErrorCode() {
    return Message.ERROR_INVALID_DOCUMENT;
  }
}