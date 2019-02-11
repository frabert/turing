package reti;

import java.rmi.*;

import reti.exceptions.*;

/** Interfaccia remota per la registrazione di nuovi utenti */
public interface RegistrationService extends Remote 
{
  /**
   * Richiede la registrazione di un nuovo utente
   * @param name Lo username dell'utente da registrare
   * @param password La password dell'utente da registrare
   * @throws InvalidUsernameException Il nome utente richiesto
   *    non è valido
   * @throws InvalidPasswordException La password richiesta non
   *    è valida
   * @throws DuplicateAccountException Un utente con nome uguale a
   *    quello richiesto è già presente
   */
  public void registerAccount(
      String name,
      String password)
    throws RemoteException,
          InvalidUsernameException,
          InvalidPasswordException,
          DuplicateAccountException;
}
