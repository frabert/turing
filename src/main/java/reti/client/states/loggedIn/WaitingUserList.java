package reti.client.states.loggedIn;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import reti.*;
import reti.client.*;
import reti.client.states.*;

/**
 * Stato del client che attende la lista degli utenti
 * registrati dal server
 */
public class WaitingUserList extends ClientState {
  int count = -1;
  ArrayList<String> users = new ArrayList<>();
  String documentName;

  public WaitingUserList(String documentName) {
    if(documentName == null) throw new NullPointerException();
    this.documentName = documentName;
  }

  @Override
  public ClientState handleMessage(Message msg) {
    switch(msg.getType()) {
      case Message.TYPE_USER_LIST_COUNT: {
        count = msg.getInt();
        return this;
      }
      case Message.TYPE_USER_LIST_NAME: {
        users.add(msg.getString());
        if(users.size() == count) {
          try {
            final ChooseUserDialog dialog = new ChooseUserDialog();
            final Object[] possibilities = users.toArray();
            SwingUtilities.invokeAndWait(new Runnable() {
              @Override
              public void run() {
                dialog.showDialog(possibilities);
              }
            });
            if(dialog.chosenUser != null) {
              Client.connection.writer.addMessage(Message.TYPE_INVITE_USER_NAME, (String)dialog.chosenUser);
              Client.connection.writer.addMessage(Message.TYPE_INVITE_USER_DOCUMENT, documentName);

              return new WaitingInviteConfirm();
            }
          } catch(Exception ex) { return new LoggedIn(); }
          
          return new LoggedIn();
        } else {
          return this;
        }
      }
      default: return this;
    }
  }
}