package reti.client.states.loggedIn;

import reti.*;
import reti.client.*;
import reti.client.states.*;

/**
 * Stato del client che attende la conferma di invio di un invito
 * dal server
 */
public class WaitingInviteConfirm extends ClientState {
  @Override
  public ClientState handleMessage(Message msg) {
    switch(msg.getType()) {
      case Message.TYPE_INVITE_USER_OK: {
        DialogUtilities.showInfoDialog("Invito inviato");
        break;
      }
      case Message.TYPE_ERROR: {
        DialogUtilities.showErrorDialog("Impossibile invitare l'utente selezionato");
        break;
      }
      default: {}
    }
    return new LoggedIn();
  }

}