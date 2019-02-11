package reti.server;

import java.io.IOException;
import java.util.*;
import reti.exceptions.*;

/**
 * Rappresenta un account registrato sul server
 */
public class Account implements java.io.Serializable {
  static final long serialVersionUID = 1L;

  String username;
  String password;

  ArrayList<Document> availableDocuments = new ArrayList<>();
  HashMap<String, Document> ownedDocuments = new HashMap<>();
  Queue<Document> pendingInvites = new LinkedList<>();
  DocumentSection editingSection;

  public Account(String username, String password)
      throws InvalidUsernameException, InvalidPasswordException {
    String trimmedName = username.trim();
    if (trimmedName.length() == 0)
      throw new InvalidUsernameException();
    if (password.length() == 0)
      throw new InvalidPasswordException();

    this.username = trimmedName;
    this.password = password;
  }

  /**
   * Crea un nuovo documento
   * @param name Nome del documento da creare
   * @param numSections Numero di sezioni del documento
   * @return Il documento creato
   * @throws IOException Non è stato possibile creare i file delle sezioni.
   * @throws DuplicateDocumentException L'account possiede già un documento con
   *     lo stesso nome.
   * @throws InvalidDocumentException Il nome del documento non è valido.
   */
  public Document createDocument(String name, int numSections)
      throws IOException, DuplicateDocumentException, InvalidDocumentException {
    String trimmedName = name.trim();
    if (trimmedName.length() == 0)
      throw new InvalidDocumentException();

    if (ownedDocuments.containsKey(trimmedName))
      throw new DuplicateDocumentException();
    Document doc = new Document(trimmedName, this, numSections);
    availableDocuments.add(doc);
    ownedDocuments.put(trimmedName, doc);

    return doc;
  }

  /**
   * Restituisce un documento dell'account
   * @param a L'account che richiede il documento
   * @param name Il nome del documento da ottenere
   * @return Il documento con il nome specificato
   * @throws NotAllowedException L'account non è autorizzato ad accedere al
   *     documento richiesto.
   * @throws NonexistantDocumentException Il nome specificato non corrisponde ad
   *     alcun documento dell'account.
   */
  public Document getDocument(Account a, String name)
      throws NonexistantDocumentException, NotAllowedException {
    String trimmedName = name.trim();

    if (!ownedDocuments.containsKey(trimmedName))
      throw new NonexistantDocumentException();

    Document doc = ownedDocuments.get(trimmedName);
    if (!doc.getAllowedAccounts().contains(a))
      throw new NotAllowedException();

    return doc;
  }

  /**
   * @return Una collezione di tutti i documenti disponibili per questo account
   */
  public Collection<Document> getAvailableDocuments() {
    return Collections.unmodifiableCollection(availableDocuments);
  }

  /**
   * @return L'username dell'account
   */
  public String getUsername() { return username; }

  /**
   * @return La password dell'account
   */
  public String getPassword() { return password; }

  /**
   * Aggiunge un invito di modificare un documento a questo account
   * @param doc Il documento a cui si riferisce l'invito
   */
  public void addInvite(Document doc) {
    this.availableDocuments.add(doc);
    this.pendingInvites.add(doc);
  }

  /**
   * @return La lista di tutti gli inviti non ancora visualizzati
   */
  public Collection<Document> getInvites() {
    Collection<Document> docs =
        Collections.unmodifiableCollection(pendingInvites);
    return docs;
  }

  /**
   * Segna tutti gli inviti come visualizzati
   */
  public void clearInvites() { pendingInvites.clear(); }

  /**
   * Imposta la sezione che l'utente sta attualmente modificando
   */
  public void setEditingSection(DocumentSection section) {
    this.editingSection = section;
  }

  /**
   * Comandi eseguiti a seguito della disconnessione dell'account
   */
  public void onDisconnect() {
    if (editingSection != null) {
      editingSection.getMainDocument().endEditingSection(this, editingSection);
    }
    editingSection = null;
  }
}