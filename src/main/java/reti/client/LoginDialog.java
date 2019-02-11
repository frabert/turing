package reti.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.registry.*;
import reti.*;
import reti.exceptions.*;

public class LoginDialog
    extends JDialog
    implements ActionListener
{
  static final long serialVersionUID = 1L;

  private JTextField userField;
  private JPasswordField pwdField;
  private JButton loginButton, registerButton;

  public LoginDialog(Frame parent) {
    super(parent, "Effettuare l'accesso", true);

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    JLabel userLabel = new JLabel("Username:");
    JLabel pwdLabel = new JLabel("Password:");

    loginButton = new JButton("Login");
    registerButton = new JButton("Registrazione");

    loginButton.setActionCommand("Login");
    loginButton.addActionListener(this);

    registerButton.setActionCommand("Register");
    registerButton.addActionListener(this);

    userField = new JTextField(16);
    pwdField = new JPasswordField(16);

    userLabel.setLabelFor(userField);
    pwdLabel.setLabelFor(pwdField);

    JPanel userPane = new JPanel();
    userPane.setLayout(new BoxLayout(userPane, BoxLayout.LINE_AXIS));
    userPane.add(userLabel);
    userPane.add(Box.createRigidArea(new Dimension(5,0)));
    userPane.add(userField);
    userPane.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));

    JPanel pwdPane = new JPanel();
    pwdPane.setLayout(new BoxLayout(pwdPane, BoxLayout.LINE_AXIS));
    pwdPane.add(pwdLabel);
    pwdPane.add(Box.createRigidArea(new Dimension(5,0)));
    pwdPane.add(pwdField);
    pwdPane.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));

    JPanel fieldsPane = new JPanel();
    fieldsPane.setLayout(new BoxLayout(fieldsPane, BoxLayout.PAGE_AXIS));
    fieldsPane.add(userPane);
    fieldsPane.add(pwdPane);
    fieldsPane.add(Box.createRigidArea(new Dimension(0,5)));
    fieldsPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    buttonPane.add(Box.createHorizontalGlue());
    buttonPane.add(loginButton);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPane.add(registerButton);

    Container contentPane = getContentPane();
    contentPane.add(fieldsPane, BorderLayout.CENTER);
    contentPane.add(buttonPane, BorderLayout.PAGE_END);

    JRootPane rootPane = SwingUtilities.getRootPane(loginButton);
    rootPane.setDefaultButton(loginButton);

    pack();
  }

  String username, password;

  public String getUsername() { return username; }
  public String getPassword() { return password; }

  @SuppressWarnings("deprecation")
  void login() {
    username = userField.getText();
    password = pwdField.getText();
    this.setVisible(false);
  }

  void register() {
    RegistrationService regService;
    Remote regServer;
    try {
      Registry registry = LocateRegistry.getRegistry(Client.host, Client.registrationPort);
      regServer = registry.lookup("TURING-REGISTER");
      regService = (RegistrationService)regServer;

      regService.registerAccount(userField.getText(), new String(pwdField.getPassword()));

      JOptionPane.showMessageDialog(this, "Registrazione effettuata con successo");
      login();

    } catch(RemoteException ex) {
      JOptionPane.showMessageDialog(this,
        new Object[] {
          "Errore di comunicazione", ex.getMessage()
        }, "Errore", JOptionPane.ERROR_MESSAGE);
    } catch(NotBoundException ex) {
      JOptionPane.showMessageDialog(this,
        new Object[] {
          "Servizio di registrazione non trovato", ex.getMessage()
        }, "Errore", JOptionPane.ERROR_MESSAGE);
    } catch(InvalidPasswordException ex) {
      JOptionPane.showMessageDialog(this, "Password non valida", "Errore", JOptionPane.ERROR_MESSAGE);
    } catch(InvalidUsernameException ex) {
      JOptionPane.showMessageDialog(this, "Username non valido", "Errore", JOptionPane.ERROR_MESSAGE);
    } catch(DuplicateAccountException ex) {
      JOptionPane.showMessageDialog(this, "Account già esistente", "Errore", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void actionPerformed(ActionEvent e) {
    switch(e.getActionCommand()) {
      case "Register": register(); break;
      case "Login": login(); break;
    }
  }
}