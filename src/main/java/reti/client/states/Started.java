package reti.client.states;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import reti.*;
import reti.client.*;

/**
 * Stato del client prima di aver effettuato l'accesso / dopo
 * essersi disconnesso
 */
public class Started extends ClientState {
  @Override
  public ClientState handleMessage(Message msg) {
    switch (msg.getType()) {
    case Message.TYPE_LOGIN_OK: {
      DialogUtilities.showInfoDialog("Accesso effettuato");

      synchronized (Client.connection.writer) {
        Client.connection.writer.addMessage(Message.TYPE_LIST_DOCUMENTS, 0);
      }

      Client.frame.setTitle("TURING - " + Client.connection.username);
      return new LoggedIn();
    }
    case Message.TYPE_ERROR: {
      final String message;
      switch (msg.getInt()) {
      case Message.ERROR_ALREADY_LOGGED:
        message = "L'utente è già connesso";
        break;
      case Message.ERROR_INVALID_PASSWORD:
        message = "Password non corretta";
        break;
      case Message.ERROR_INVALID_USER:
        message = "Utente inesistente";
        break;
      default:
        message = "Errore sconosciuto";
      }
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          JOptionPane.showMessageDialog(Client.frame, message, "Errore",
                                        JOptionPane.ERROR_MESSAGE);
          Client.AttemptLogin();
        }
      });
      break;
    }
    }
    return new Started();
  }
}