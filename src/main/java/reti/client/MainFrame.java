package reti.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Finestra principale
 */
public class MainFrame extends JFrame implements ActionListener {
  private static final long serialVersionUID = 1L;

  DefaultListModel<Document> availableDocumentsModel;
  JList<Document> availableDocuments;
  DefaultListModel<String> ownedDocumentsModel;
  JList<String> ownedDocuments;

  JPanel defaultPanel;

  /** Creazione di un nuovo documento */
  public static final String ACTION_NEW_DOCUMENT  = "MAIN_NEW_DOCUMENT";
  
  /** Invito di un utente */
  public static final String ACTION_INVITE_USER   = "MAIN_INVITE_USER";
  
  /** Modifica di una sezione */
  public static final String ACTION_EDIT_SECTION  = "MAIN_EDIT_SECTION";
  
  /** Visualizzazione di una sezione */
  public static final String ACTION_VIEW_SECTION  = "MAIN_VIEW_SECTION";
  
  /** Visualizzazione di un documento */
  public static final String ACTION_VIEW_DOCUMENT = "MAIN_VIEW_DOCUMENT";
  
  /** Logout */
  public static final String ACTION_LOGOUT        = "MAIN_LOGOUT";
  
  /** Termine delle modifiche */
  public static final String ACTION_END_EDIT      = "MAIN_END_EDIT";
  
  /** Annullamento delle modifiche */
  public static final String ACTION_ABORT_EDIT    = "MAIN_ABORT_EDIT";

  JButton newDocument, inviteUser, viewDocument, viewSection, editSection, logout;
  JButton endEdit, abortEdit;

  public MainFrame() {
    super("TURING");
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setVisible(true);
    this.setLocationRelativeTo(null);

    defaultPanel = new JPanel(new BorderLayout());
    this.setContentPane(defaultPanel);

    availableDocumentsModel = new DefaultListModel<>();
    availableDocuments = new JList<>();

    ownedDocumentsModel = new DefaultListModel<>();
    ownedDocuments = new JList<>();

    createLeftPanel();
    createRightPanel();

    pack();
  }

  void createLeftPanel() {
    ownedDocuments.setPreferredSize(new Dimension(250, 300));

    newDocument = new JButton("Nuovo documento");
    newDocument.setActionCommand(ACTION_NEW_DOCUMENT);
    newDocument.addActionListener(this);

    inviteUser = new JButton("Invita utente");
    inviteUser.setActionCommand(ACTION_INVITE_USER);
    inviteUser.addActionListener(this);

    logout = new JButton("Logout");
    logout.setActionCommand(ACTION_LOGOUT);
    logout.addActionListener(this);

    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
    leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
    leftPanel.add(new JLabel("Documenti personali"));
    leftPanel.add(ownedDocuments);
    leftPanel.add(newDocument);
    leftPanel.add(inviteUser);
    leftPanel.add(logout);

    defaultPanel.add(leftPanel, BorderLayout.LINE_START);
  }

  void createRightPanel() {
    availableDocuments.setPreferredSize(new Dimension(250, 300));

    viewDocument = new JButton("Visualizza documento");
    viewDocument.setActionCommand(ACTION_VIEW_DOCUMENT);
    viewDocument.addActionListener(this);

    viewSection = new JButton("Visualizza sezione");
    viewSection.setActionCommand(ACTION_VIEW_SECTION);
    viewSection.addActionListener(this);

    editSection = new JButton("Modifica sezione");
    editSection.setActionCommand(ACTION_EDIT_SECTION);
    editSection.addActionListener(this);

    endEdit = new JButton("Termina modifiche");
    endEdit.setActionCommand(ACTION_END_EDIT);
    endEdit.addActionListener(this);
    endEdit.setEnabled(false);

    abortEdit = new JButton("Annulla modifiche");
    abortEdit.setActionCommand(ACTION_ABORT_EDIT);
    abortEdit.addActionListener(this);
    abortEdit.setEnabled(false);

    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
    rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
    rightPanel.add(new JLabel("Documenti condivisi"));
    rightPanel.add(availableDocuments);
    rightPanel.add(viewDocument);
    rightPanel.add(viewSection);
    rightPanel.add(editSection);
    rightPanel.add(endEdit);
    rightPanel.add(abortEdit);

    rightPanel.setPreferredSize(new Dimension(300, 500));
    defaultPanel.add(rightPanel, BorderLayout.LINE_END);
  }

  /**
   * Svuota la lista dei documenti personali
   */
  public void clearOwnedDocuments() {
    ownedDocumentsModel.clear();
    ownedDocuments.setModel(ownedDocumentsModel);
  }

  /**
   * Aggiunge un documento personale alla lista
   */
  public void addOwnedDocument(String doc) {
    ownedDocumentsModel.addElement(doc);
    ownedDocuments.setModel(ownedDocumentsModel);
  }

  /**
   * Svuota la lista dei documenti disponibili
   */
  public void clearAvailableDocuments() {
    availableDocumentsModel.clear();
    availableDocuments.setModel(availableDocumentsModel);
  }

  /**
   * Aggiunge un documento disponibile alla lista
   */
  public void addAvailableDocument(Document doc) {
    availableDocumentsModel.addElement(doc);
    availableDocuments.setModel(availableDocumentsModel);
  }

  /**
   * Cambia la modalità da "modifiche in corso" a quella normale,
   * disabilitando i pulsanti delle azioni non consentite e
   * attivando quelli delle azioni disponibili
   */
  public void setEditingMode(boolean editing) {
    newDocument.setEnabled(!editing);
    inviteUser.setEnabled(!editing);
    editSection.setEnabled(!editing);
    logout.setEnabled(!editing);
    endEdit.setEnabled(editing);
    abortEdit.setEnabled(editing);
  }

  /**
   * Restituisce il documento personale selezionato, o null
   */
  public String getSelectedOwnedDocument() {
    int idx = ownedDocuments.getSelectedIndex();
    if(idx < 0) return null;
    else return ownedDocumentsModel.get(idx);
  }

  /**
   * Restituisce il documento disponibile selezionato, o null
   */
  public Document getSelectedAvailableDocument() {
    int idx = availableDocuments.getSelectedIndex();
    if(idx < 0) return null;
    else return availableDocumentsModel.get(idx);
  }

  /**
   * Invia il messaggio di annulla modifiche
   */
  public void abortEdits() {
    Client.connection.handleAction(ACTION_ABORT_EDIT);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    synchronized(Client.connection.writer) {
      Client.connection.handleAction(e.getActionCommand());
    }
  }
}