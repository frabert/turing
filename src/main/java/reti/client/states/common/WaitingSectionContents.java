package reti.client.states.common;

import java.io.IOException;
import java.nio.file.*;

import reti.*;
import reti.client.*;

/**
 * Stato del client che attende i contenuti di una sezione
 * di cui è stata effettuata la richiesta di visualizzazione
 */
public class WaitingSectionContents extends ClientState {
  Path filePath;
  ClientState previous;

  public WaitingSectionContents(String outputFile, ClientState previous) {
    this.filePath = Paths.get(outputFile);
    this.previous = previous;
  }

  @Override
  public ClientState handleMessage(Message msg) {
    switch(msg.getType()) {
      case Message.TYPE_SHOW_SECTION_OK: {
        int isBeingEdited = msg.getInt();
 
        if(isBeingEdited != 0) {
          DialogUtilities.showInfoDialog("La sezione è in corso di modifiche da parte di altri utenti");
        }

        return this;
      }
      case Message.TYPE_SHOW_SECTION_CONTENTS: {
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
        DialogUtilities.showErrorDialog("Impossibile visualizzare la sezione");
        return previous;
      }
      default: return this;
    }
  }

}