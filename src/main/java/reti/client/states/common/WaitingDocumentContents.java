package reti.client.states.common;

import java.io.IOException;
import java.nio.file.*;

import reti.*;
import reti.client.*;

/**
 * Stato del client che attende i contenuti di un documento
 * di cui è stata effettuata la richiesta di visualizzazione
 */
public class WaitingDocumentContents extends ClientState {
  Path filePath;
  ClientState previous;
  boolean receivedEditingStatus = false;

  public WaitingDocumentContents(String outputFile, ClientState previous) {
    this.filePath = Paths.get(outputFile);
    this.previous = previous;
  }

  @Override
  public ClientState handleMessage(Message msg) {
    switch(msg.getType()) {
      case Message.TYPE_SHOW_DOCUMENT_OK: {
        return this;
      }
      case Message.TYPE_DOCUMENT_CONTENTS: {
        if(!receivedEditingStatus) {
          int isBeingEdited = msg.getInt();
          
          if(isBeingEdited != 0) {
            DialogUtilities.showInfoDialog("Il documento è in corso di modifiche da parte di altri utenti");
          }
          receivedEditingStatus = true;
          return this;
        }

        byte[] contents = msg.getData();
        try {
          Files.write(
            filePath,
            contents,
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.CREATE);
          DialogUtilities.showInfoDialog("File salvato");
        } catch(IOException ex) {
          DialogUtilities.showErrorDialog("Impossibile scrivere il file");
        }
        return previous;
      }
      case Message.TYPE_ERROR: {
        DialogUtilities.showErrorDialog("Impossibile visualizzare il documento");
        return previous;
      }
      default: return this;
    }
  }

}