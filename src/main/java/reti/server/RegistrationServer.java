package reti.server;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import reti.*;
import reti.exceptions.*;

/**
 * Endpoint RMI per la registrazione
 */
public class RegistrationServer
    extends UnicastRemoteObject
    implements RegistrationService
{
  static final long serialVersionUID = 1L;
  ServerState state;

  public RegistrationServer(ServerState state)
      throws RemoteException
  {
    super();

    if(state == null) throw new NullPointerException();
    this.state = state;
  }

  @Override
  public void registerAccount(String name, String password)
      throws InvalidPasswordException,
            InvalidUsernameException,
            DuplicateAccountException
  {
    if(name == null || password == null) throw new NullPointerException();
    state.persistent.addAccount(new Account(name, password));
  }
}