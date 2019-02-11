package reti.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import reti.*;
import reti.exceptions.*;

/**
 * Rappresenta un documento formato da più sezioni
 */
public class Document implements java.io.Serializable {
  static final long serialVersionUID = 1L;

  Account owner;
  ArrayList<Account> allowedAccounts;
  HashSet<Account> editingAccounts;
  DocumentSection[] sections;
  String name;
  InetAddress chatAddress;

  public Document(String name, Account owner, int numSections)
      throws IOException
  {
    if(owner == null || name == null) throw new NullPointerException();
    
    this.name = name.trim();
    this.owner = owner;

    this.sections = new DocumentSection[numSections];

    for(int i = 0; i < numSections; i++) {
      this.sections[i] = new DocumentSection(this);
    }

    this.editingAccounts = new HashSet<>();
    this.allowedAccounts = new ArrayList<>();

    this.allowedAccounts.add(owner);
  }

  /**
   * @return L'account proprietario del documento
   */
  public Account getOwner() { return owner; }

  /**
   * @return Il numero di sezioni del documento
   */
  public int getSectionCount() { return sections.length; }

  /**
   * Restituisce la sezione specificata del documento
   * @param acc L'account che richiede la sezione
   * @param i L'indice della sezione richiesta
   * @return La sezione richiesta del documento
   * @throws NotAllowedException L'account a non è autorizzato ad accedere al documento
   * @throws InvalidSectionException La sezione specificata non è valida
   */
  public DocumentSection getSection(Account acc, int i)
      throws NotAllowedException, InvalidSectionException
  {
    if(!allowedAccounts.contains(acc))
      throw new NotAllowedException();

    if(i >= sections.length)
      throw new InvalidSectionException();
    
    return sections[i];
  }

  /**
   * @return Il nome del documento
   */
  public String getName() { return name; }

  /**
   * @return La lista di tutti gli account che hanno il permesso di accedere al documento
   */
  public Collection<Account> getAllowedAccounts() {
    return Collections.unmodifiableCollection(allowedAccounts);
  }

  /**
   * @return L'indirizzo multicast della lobby chat del documento, o null se la chat non è attiva
   */
  public InetAddress getChatAddress() { return chatAddress; }

  /**
   * Inizia l'editing di una sezione del documento
   * @param a L'account che inizia le modifiche
   * @param i L'indice della sezione da modificare
   * @return La sezione da modificare
   * @throws InvalidSectionException La sezione specificata non è valida
   * @throws NotAllowedException L'account a non è autorizzato a modificare questo documento
   * @throws SectionLockedException La sezione i è già bloccata da un altro account
   */
  public DocumentSection startEditSection(Account a, int i)
      throws NotAllowedException, InvalidSectionException, SectionLockedException
  {
    if(!allowedAccounts.contains(a)) throw new NotAllowedException();

    DocumentSection sect = getSection(a, i);
    sect.Lock(a);
    editingAccounts.add(a);
    a.setEditingSection(sect);

    if(chatAddress == null) chatAddress = Server.state.getEphemeral().getFreeChatAddress();
    return sect;
  }

  /**
   * Finisce l'editing di una sezione del documento
   * @param acc L'account che termina l'editing
   * @param sect La sezione che è stata modificata
   */
  public void endEditingSection(Account acc, DocumentSection sect)
  {
    sect.Unlock();
    editingAccounts.remove(acc);
    acc.setEditingSection(null);
    if(editingAccounts.isEmpty()) {
      Server.state.getEphemeral().unusedAddresses.push(chatAddress);
      chatAddress = null;
    }
  }

  /**
   * Abilita l'account a ad accedere a questo documento
   * @param a L'account da abilitare all'accesso
   * @throws InvalidUsernameException L'account è già autorizzato a modificare il documento
   */
  public void addAllowedAccount(Account a)
    throws InvalidUsernameException
  {
    if(allowedAccounts.contains(a))
      throw new InvalidUsernameException();
    allowedAccounts.add(a);
  }

  /**
   * @return Il contenuto dell'intero documento e un flag che indica se altri utenti lo stanno modificando
   * @throws IOException Si è verificato un errore durante la lettura di uno dei file del documento
   */
  public Pair<byte[], Boolean> getContents()
      throws IOException
  {
    boolean beingEdited = false;
    ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
    for(DocumentSection section : sections) {
      bytestream.write(section.readContents());
    }

    return new Pair<>(bytestream.toByteArray(), beingEdited);
  }
}