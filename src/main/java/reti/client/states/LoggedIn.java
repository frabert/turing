package reti.client.states;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import reti.*;
import reti.client.*;
import reti.client.states.loggedIn.*;
import reti.client.states.common.*;

/**
 * Stato del client che ha effettuato l'acceesso
 */
public class LoggedIn extends ClientState {
  @Override
  public ClientState handleMessage(Message msg) {
    switch(msg.getType()) {
      case Message.TYPE_DOCUMENT_LIST_COUNT: {
        int count = msg.getInt();
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            Client.frame.clearAvailableDocuments();
            Client.frame.clearOwnedDocuments();
          }
        });
        if(count > 0) return new DocumentList(count);
        else return this;
      }
      case Message.TYPE_INVITE_COUNT: {
        int count = msg.getInt();
        return new WaitingDocumentInvites(count, this);
      }
      default: return this;
    }
  }

  ClientState NewDocument() {
    String name = JOptionPane.showInputDialog(Client.frame, "Nome del documento");
    if(name == null) return this;

    try {
      int sections = Integer.parseInt(JOptionPane.showInputDialog(Client.frame, "Numero di sezioni"));
  
      synchronized(Client.connection.writer) {
        Client.connection.writer.addMessage(Message.TYPE_NEW_DOCUMENT_NAME, name);
        Client.connection.writer.addMessage(Message.TYPE_NEW_DOCUMENT_SECTIONS, sections);
      }
      return new WaitingNewDocumentConfirm(name, sections);
    } catch(NumberFormatException ex) {
      return this;
    }
  }

  ClientState ViewDocument() {
    Document selectedDocument = Client.frame.getSelectedAvailableDocument();
    if(selectedDocument == null) return this;

    JFileChooser chooser = new JFileChooser();
    int res = chooser.showSaveDialog(Client.frame);
    if(res != JFileChooser.APPROVE_OPTION) return this;
    
    synchronized(Client.connection.writer) {
      Client.connection.writer.addMessage(Message.TYPE_SHOW_DOCUMENT_OWNER, selectedDocument.getOwner());
      Client.connection.writer.addMessage(Message.TYPE_SHOW_DOCUMENT_NAME, selectedDocument.getName());
    }
    return new WaitingDocumentContents(chooser.getSelectedFile().getPath(), this);
  }

  ClientState ViewSection() {
    Document selectedDocument = Client.frame.getSelectedAvailableDocument();
    if(selectedDocument == null) return this;

    JFileChooser chooser = new JFileChooser();
    int res = chooser.showSaveDialog(Client.frame);
    if(res != JFileChooser.APPROVE_OPTION) return this;

    Object[] options = new Object[selectedDocument.getSections()];
    for(int i = 0; i < options.length; i++) options[i] = i;

    Object section = JOptionPane.showInputDialog(
      Client.frame,
      "Selezionare la sezione da visualizzare",
      "Scelta sezione",
      JOptionPane.PLAIN_MESSAGE,
      null,
      options,
      options[0]);
    if(section == null) return this;

    int sectionNum = (Integer)section;
    synchronized(Client.connection.writer) {
      Client.connection.writer.addMessage(Message.TYPE_SHOW_SECTION_OWNER, selectedDocument.getOwner());
      Client.connection.writer.addMessage(Message.TYPE_SHOW_SECTION_NAME, selectedDocument.getName());
      Client.connection.writer.addMessage(Message.TYPE_SHOW_SECTION_IDX, sectionNum);
    }
    return new WaitingSectionContents(chooser.getSelectedFile().getPath(), this);
  }

  public ClientState EditSection() {
    Document selectedDocument = Client.frame.getSelectedAvailableDocument();
    if(selectedDocument == null) return this;

    JFileChooser chooser = new JFileChooser();
    int res = chooser.showSaveDialog(Client.frame);
    if(res != JFileChooser.APPROVE_OPTION) return this;

    Object[] options = new Object[selectedDocument.getSections()];
    for(int i = 0; i < options.length; i++) options[i] = i;
    Object section = JOptionPane.showInputDialog(
      Client.frame,
      "Selezionare la sezione da modificare",
      "Scelta sezione",
      JOptionPane.PLAIN_MESSAGE,
      null,
      options,
      options[0]);
    
    if(section == null) return this;

    int sectionNum = (Integer)section;
    String owner = selectedDocument.getOwner();
    String name = selectedDocument.getName();
    synchronized(Client.connection.writer) {
      Client.connection.writer.addMessage(Message.TYPE_EDIT_SECTION_OWNER, owner);
      Client.connection.writer.addMessage(Message.TYPE_EDIT_SECTION_DOCUMENT, name);
      Client.connection.writer.addMessage(Message.TYPE_EDIT_SECTION_IDX, sectionNum);
    }
    return new WaitingEditSectionConfirm(chooser.getSelectedFile().getPath(), owner, name, sectionNum);
  }

  @Override
  public ClientState handleAction(String action) {
    switch(action) {
      case MainFrame.ACTION_INVITE_USER: {
        String selectedDoc = Client.frame.getSelectedOwnedDocument();
        if(selectedDoc == null) return this;

        synchronized(Client.connection.writer) {
          Client.connection.writer.addMessage(Message.TYPE_LIST_USERS, 0);
        }
        return new WaitingUserList(selectedDoc);
      }
      case MainFrame.ACTION_LOGOUT: {
        synchronized(Client.connection.writer) {
          Client.connection.writer.addMessage(Message.TYPE_LOGOUT, 0);
        }
        Client.frame.setTitle("TURING");
        Client.AttemptLogin();
        return new Started();
      }
      case MainFrame.ACTION_NEW_DOCUMENT: return NewDocument();
      case MainFrame.ACTION_VIEW_DOCUMENT: return ViewDocument();
      case MainFrame.ACTION_VIEW_SECTION: return ViewSection();
      case MainFrame.ACTION_EDIT_SECTION: return EditSection();
      default: return this;
    }
  }
}