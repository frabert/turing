package reti.client.states.loggedIn;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.*;

import javax.swing.SwingUtilities;

import reti.*;
import reti.client.*;
import reti.client.states.*;

/**
 * Stato del client che attende la conferma di autorizzazione
 * di modifica di una sezione dal server
 */
public class WaitingEditSectionConfirm extends ClientState {
  Path filePath;
  String owner, name;
  int index;

  public WaitingEditSectionConfirm(String outputFile, String owner, String name,
                                   int index) {
    this.filePath = Paths.get(outputFile);
    this.owner = owner;
    this.name = name;
    this.index = index;
  }

  @Override
  public ClientState handleMessage(Message msg) {
    switch (msg.getType()) {
    case Message.TYPE_EDIT_SECTION_OK: {
      byte[] contents = msg.getData();
      try {
        Files.write(filePath, contents, StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE);
        DialogUtilities.showInfoDialog("File salvato");
      } catch (IOException ex) {
        DialogUtilities.showErrorDialog("Impossibile scrivere il file");
      }
      return this;
    }
    case Message.TYPE_DOCUMENT_CHAT_ADDR: {
      try {
        final InetAddress addr = InetAddress.getByAddress(msg.getData());
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            try {
              Client.chatFrame = new ChatFrame(addr);
            } catch (Exception e) { }
          }
        });
      } catch (Exception e) { }
      Client.frame.setEditingMode(true);
      return new Editing(owner, name, index, filePath);
    }
    case Message.TYPE_ERROR: {
      String error;
      switch (msg.getInt()) {
      case Message.ERROR_SECTION_LOCKED: {
        error =
            "La sezione è già in corso di modifiche da parte di un altro utente";
        break;
      }
      default:
        error = "Impossibile modificare la sezione specificata";
      }
      DialogUtilities.showErrorDialog(error);
      return new LoggedIn();
    }
    default:
      return this;
    }
  }
}