package reti.server;

import java.util.Enumeration;
import java.util.Hashtable;
import java.io.Serializable;
import reti.exceptions.*;

/**
 * Stato del server che viene mantenuto attraverso le sessioni
 */
public class PersistentServerState implements Serializable {
  static final long serialVersionUID = 1L;
  Hashtable<String, Account> accounts;

  public PersistentServerState() {
    accounts = new Hashtable<>();
  }

  /**
   * @param username L'username che deve essere cercato
   * @return True se l'username corrisponde ad un account esistente, false altrimenti
   */
  public synchronized Boolean accountExists(String username) {
    if(username == null) throw new NullPointerException();

    String trimmedName = username.trim();
    return accounts.containsKey(trimmedName);
  }

  /**
   * Aggiunge un account
   * @param account L'account da aggiungere
   * @throws DuplicateAccountException L'account è già presente
   */
  public synchronized void addAccount(Account account)
      throws DuplicateAccountException
  {
    if(account == null) throw new NullPointerException();

    if(accountExists(account.getUsername()))
      throw new DuplicateAccountException();

    accounts.put(account.getUsername(), account);
  }

  /**
   * Restituisce l'account relativo all'username specificato
   * @param username L'username dell'account
   * @return L'account corrispondente all'username specificato
   * @throws NonexistantAccountException L'username non corrisponde ad alcun account
   */
  public synchronized Account getAccount(String username)
      throws NonexistantAccountException
  {
    if(username == null) throw new NullPointerException();

    String trimmedUsername = username.trim();

    if(!accountExists(trimmedUsername))
      throw new NonexistantAccountException();

    return accounts.get(trimmedUsername);
  }

  public synchronized Enumeration<String> getAccounts() {
    return accounts.keys();
  }
}