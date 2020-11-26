/*
 * MainFrameTransaction.java
 *
 * Created on September 16, 2005, 3:11 PM
 * This program will interface with the user in form of a GUI to process transactions.
 */

package edu.byu.isys413.group1a.intex2.GUIs;

import edu.byu.isys413.group1a.intex2.BOs.Customer;
import edu.byu.isys413.group1a.intex2.BOs.Store;
import edu.byu.isys413.group1a.intex2.BOs.TxDisplay;
import edu.byu.isys413.group1a.intex2.Controllers.AcctManagementController;
import edu.byu.isys413.group1a.intex2.Controllers.RenewalBatch;
import edu.byu.isys413.group1a.intex2.Controllers.TxController;
import edu.byu.isys413.group1a.intex2.DAOs.StoreDAO;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.RenewalException;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Group 1A
 *
 */
public class GUIAcctManagement extends javax.swing.JFrame {
  
  private final String STORE = "0000010942ed61aeb33d203e001000ac00e900c6";
  private DefaultTableModel tableTransactionModel = null;
  private DefaultTableModel tableReturnsModel = null;
  private TxController txc = null;
  private TxDisplay txd = null;
  private AcctManagementController amc = null;
  
  /** Creates new form MainFrameTransaction */
  public GUIAcctManagement() {
    /* Sets the GUI to a windows look and feel  */
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception exc) {
      System.err.println("Error loading L&F: " + exc);
    }
    
    initComponents();
    clearTransaction();
    radioCreateAccount.requestFocus();
  }
  
  /** Sets the information in the GUI lookup */
  private void setAccountLookupInfo(){
    //checks to make sure a value is selected
    if (accountsList.getSelectedValue() != (null)){
      Customer cust = (Customer)accountsList.getSelectedValue();
      lastNameTextField.setText(cust.getLastName());
      firstNameTextField.setText(cust.getFirstName());
      phoneNumTextField.setText(cust.getPhone());
      accountNumTextField.setText(cust.getAccount().getAccountNum());
    }
  }
  /** This makes sure that an account has been selected or loaded and if not, one is loaded from current selection */
  private boolean accountMustBeSelected(){
    if (infoAccountNum.getText().equals("") && accountsList.getSelectedValue() == null){
      getAccountSummary.requestFocus();
      JOptionPane.showMessageDialog(this, "You must select an account member to add a customer.", "Account Not Selected", JOptionPane.ERROR_MESSAGE);
      return false;
    } else{
      Customer cust = (Customer)accountsList.getSelectedValue();
      getAccountSummary(cust);
    }
    return true;
  }
  
  /** This loads all of the account summary information and puts loads it to the GUI */
  private void getAccountSummary(Customer cust){
    if (cust == null){
      return;
    }
    //Set variables from customer object
    infoAccountNum.setText(cust.getAccount().getAccountNum());
    infoPhoneNum.setText(cust.getPhone());
    infoAddress.setText(cust.getAddress());
    infoCityStateZip.setText(cust.getCity() + ", " + cust.getState() + " " + cust.getZipCode());
    membershipType.setText(cust.getAccount().getMembership().getDescription());
    DefaultListModel accountMembersListModel = null;
    
    //Look up members by account number
    try{
      accountMembersListModel = amc.lookupMembersByAccountNum(cust.getAccount().getAccountNum());
    } catch (Exception e){
      JOptionPane.showMessageDialog(this, "An error occured while retrieving membership info.\n" +
              "Please try again.", "Membership Retreival Error", JOptionPane.ERROR_MESSAGE);
    }
    
    accountMembersList.setModel(accountMembersListModel);
    
    DefaultListModel singledOutMember = new DefaultListModel();
    singledOutMember.addElement(cust);
    accountsList.setModel(singledOutMember);
  }
  
  private void enableCustPanel(boolean enabled){
    firstNameNewCust.setEditable(enabled);
    lastNameNewCust.setEditable(enabled);
    phoneNumNewCust.setEditable(enabled);
    addressNewCust.setEditable(enabled);
    zipcodeNewCust.setEditable(enabled);
    cityNewCust.setEditable(enabled);
    stateNewCust.setEditable(enabled);
    addCustomer.setEnabled(enabled);
  }
  private void enableNewAcctPanel(boolean enabled){
    ccNum.setEditable(enabled);
    ccName.setEditable(enabled);
    ccExpMonth.setEditable(enabled);
    ccExpYear.setEditable(enabled);
    save.setEnabled(enabled);
    cancel.setEnabled(enabled);
  }
  private void enableLookupPreviousAcct(boolean enabled){
    firstNameTextField.setEditable(enabled);
    lastNameTextField.setEditable(enabled);
    phoneNumTextField.setEditable(enabled);
    accountNumTextField.setEditable(enabled);
    lookupMembers.setEnabled(enabled);
    clearSearch.setEnabled(enabled);
    getAccountSummary.setEnabled(enabled);
    changeAccountInfo.setEnabled(enabled);
    accountsList.setEnabled(enabled);
    addCustomer.setEnabled(enabled);
    cancel1.setEnabled(enabled);
  }
  
  /** This clears the all fields and the transaction for a clean transaction **/
  private void clearTransaction(){
    
    //clears the search fields panel
    lastNameTextField.setText("");
    firstNameTextField.setText("");
    phoneNumTextField.setText("");
    accountNumTextField.setText("");
    
    //clears the Account info panel
    infoAccountNum.setText("");
    infoPhoneNum.setText("");
    infoCityStateZip.setText("");
    infoAddress.setText("");
    membershipType.setText("");
    DefaultListModel listMod = new DefaultListModel();
    listMod.clear();
    accountMembersList.setModel(listMod);
    accountsList.setModel(listMod);
    
    tableTransactionModel = new DefaultTableModel();
    tableReturnsModel = new DefaultTableModel();
    txd = new TxDisplay();
    amc = new AcctManagementController();
    txc = new TxController();
    
    
  }
  /** Adds a new customer to an existing account**/
  private void addCustomer(){
    String firstName = firstNameNewCust.getText();
    String lastName = lastNameNewCust.getText();
    String address = addressNewCust.getText();
    String city = cityNewCust.getText();
    String state = stateNewCust.getText();
    String zipCode = zipcodeNewCust.getText();
    String phone = phoneNumNewCust.getText();
    String account = infoAccountNum.getText();
    
    AcctManagementController amc = new AcctManagementController();
    try{
      amc.addCustomerWithAccount(firstName, lastName, address, city, state, zipCode, phone, account);
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  /** Creates a new account with a new customer as the owner*/
  private void createsAccount() throws Exception{
    
    String firstName = firstNameNewCust.getText();
    String lastName = lastNameNewCust.getText();
    String address = addressNewCust.getText();
    String city = cityNewCust.getText();
    String state = stateNewCust.getText();
    String zipCode = zipcodeNewCust.getText();
    String phone = phoneNumNewCust.getText();
    Store store = StoreDAO.getInstance().read(STORE);
    String cName = ccName.getText();
    String cNum = ccNum.getText();
    int cExpMonth = Integer.parseInt(ccExpMonth.getText());
    int cExpYear = Integer.parseInt(ccExpYear.getText());
    
    AcctManagementController amc = new AcctManagementController();
    amc.addCustomerToNewAccount(firstName, lastName, address, city, state, zipCode, phone, store, cName, cNum, cExpMonth, cExpYear);
  }
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {
    buttonGroup1 = new javax.swing.ButtonGroup();
    mainScrollPanel = new javax.swing.JScrollPane();
    jPanel1 = new javax.swing.JPanel();
    mainPanel = new javax.swing.JPanel();
    accountInfo = new javax.swing.JPanel();
    changeAccountInfo = new javax.swing.JButton();
    jScrollPane2 = new javax.swing.JScrollPane();
    accountMembersList = new javax.swing.JList();
    infoAccountNumLabel = new javax.swing.JLabel();
    infoAccountNum = new javax.swing.JLabel();
    infoCityStateZip = new javax.swing.JLabel();
    membershipType = new javax.swing.JLabel();
    membershipTypeLabel = new javax.swing.JLabel();
    infoPhoneNum = new javax.swing.JLabel();
    accountMembersLabel = new javax.swing.JLabel();
    infoAddress = new javax.swing.JLabel();
    newAccountInfo = new javax.swing.JPanel();
    save = new javax.swing.JButton();
    cancel = new javax.swing.JButton();
    ownerLabel = new javax.swing.JLabel();
    owner = new javax.swing.JLabel();
    ccNumLabel = new javax.swing.JLabel();
    ccNameLabel = new javax.swing.JLabel();
    ccExpMonthLabel = new javax.swing.JLabel();
    ccExpYearLabel = new javax.swing.JLabel();
    ccNum = new javax.swing.JTextField();
    ccName = new javax.swing.JTextField();
    ccExpMonth = new javax.swing.JTextField();
    ccExpYear = new javax.swing.JTextField();
    membershipTypeNewAcctLabel = new javax.swing.JLabel();
    membershipTypeNewAcctLabel1 = new javax.swing.JLabel();
    membershipTypeNewAcctLabel2 = new javax.swing.JLabel();
    newCustomerPanel = new javax.swing.JPanel();
    firstNameNewCustLabel = new javax.swing.JLabel();
    addCustomer = new javax.swing.JButton();
    lastnameNewCust = new javax.swing.JLabel();
    phoneNumNewCustLabel = new javax.swing.JLabel();
    firstNameNewCust = new javax.swing.JTextField();
    lastNameNewCust = new javax.swing.JTextField();
    phoneNumNewCust = new javax.swing.JTextField();
    addressNewCust = new javax.swing.JTextField();
    addressNewCustLabel = new javax.swing.JLabel();
    cityNewCustLabel = new javax.swing.JLabel();
    zipCodeNewCustLabel = new javax.swing.JLabel();
    stateNewCustLabel = new javax.swing.JLabel();
    zipcodeNewCust = new javax.swing.JTextField();
    cityNewCust = new javax.swing.JTextField();
    stateNewCust = new javax.swing.JTextField();
    cancel1 = new javax.swing.JButton();
    members = new javax.swing.JPanel();
    getAccountSummary = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    accountsList = new javax.swing.JList();
    lookupMember = new javax.swing.JPanel();
    lastNameTextField = new javax.swing.JTextField();
    firstNameTextField = new javax.swing.JTextField();
    phoneNumTextField = new javax.swing.JTextField();
    accountNumTextField = new javax.swing.JTextField();
    lastNameLabel = new javax.swing.JLabel();
    firstNameLabel = new javax.swing.JLabel();
    phoneNumLabel = new javax.swing.JLabel();
    accountNumLabel = new javax.swing.JLabel();
    lookupMembers = new javax.swing.JButton();
    clearSearch = new javax.swing.JButton();
    newAccountOrCustomer = new javax.swing.JPanel();
    radioAddCustomer = new javax.swing.JRadioButton();
    radioCreateAccount = new javax.swing.JRadioButton();
    mainLabelPanel = new javax.swing.JPanel();
    blockWoodLabel = new javax.swing.JLabel();
    logPanel = new javax.swing.JPanel();
    logLabel = new javax.swing.JLabel();
    jSeparator1 = new javax.swing.JSeparator();
    employeeName = new javax.swing.JLabel();
    jSeparator2 = new javax.swing.JSeparator();
    employeeIDLabel = new javax.swing.JLabel();
    employeeID = new javax.swing.JLabel();
    jSeparator4 = new javax.swing.JSeparator();
    logButton = new javax.swing.JToggleButton();
    group1aLabel = new javax.swing.JLabel();
    mainMenuBar = new javax.swing.JMenuBar();
    File = new javax.swing.JMenu();
    login = new javax.swing.JMenuItem();
    logout = new javax.swing.JMenuItem();
    properties = new javax.swing.JMenuItem();
    exit = new javax.swing.JMenuItem();
    Rentals = new javax.swing.JMenu();
    transaction = new javax.swing.JMenuItem();
    checkInVideo = new javax.swing.JMenuItem();
    movieAvailability = new javax.swing.JMenuItem();
    reserveMovie = new javax.swing.JMenuItem();
    Accounts = new javax.swing.JMenu();
    createAccount = new javax.swing.JMenuItem();
    addCustomer1 = new javax.swing.JMenuItem();
    updateAccount = new javax.swing.JMenuItem();
    buyMembership = new javax.swing.JMenuItem();
    renewMembership = new javax.swing.JMenuItem();
    DeleteAccount = new javax.swing.JMenuItem();
    Orders = new javax.swing.JMenu();
    jMenuItem15 = new javax.swing.JMenuItem();
    jMenuItem16 = new javax.swing.JMenuItem();
    jMenuItem17 = new javax.swing.JMenuItem();
    jMenuItem18 = new javax.swing.JMenuItem();
    jMenuItem19 = new javax.swing.JMenuItem();
    Reports = new javax.swing.JMenu();
    jMenuItem5 = new javax.swing.JMenuItem();
    Print = new javax.swing.JMenuItem();
    jMenuItem21 = new javax.swing.JMenuItem();
    jMenuItem22 = new javax.swing.JMenuItem();
    jMenuItem23 = new javax.swing.JMenuItem();
    Help = new javax.swing.JMenu();
    jMenuItem24 = new javax.swing.JMenuItem();
    jMenuItem25 = new javax.swing.JMenuItem();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("BlockWoodVideo");
    setBackground(new java.awt.Color(255, 255, 255));
    accountInfo.setBorder(javax.swing.BorderFactory.createTitledBorder("Account Information"));
    changeAccountInfo.setText("Change Account Info");
    changeAccountInfo.setToolTipText("Update Account Information");
    changeAccountInfo.setEnabled(false);
    changeAccountInfo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        changeAccountInfoActionPerformed(evt);
      }
    });

    accountMembersList.setBackground(new java.awt.Color(236, 233, 216));
    jScrollPane2.setViewportView(accountMembersList);

    infoAccountNumLabel.setText("Account #");

    infoAccountNum.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    infoAccountNum.setText(" ");

    infoCityStateZip.setText(" ");

    membershipType.setFont(new java.awt.Font("Tahoma", 1, 13));
    membershipType.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    membershipType.setText("Gold");

    membershipTypeLabel.setText("Membership Type:");

    accountMembersLabel.setFont(new java.awt.Font("Tahoma", 1, 12));
    accountMembersLabel.setText("Account Members:");

    org.jdesktop.layout.GroupLayout accountInfoLayout = new org.jdesktop.layout.GroupLayout(accountInfo);
    accountInfo.setLayout(accountInfoLayout);
    accountInfoLayout.setHorizontalGroup(
      accountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(accountInfoLayout.createSequentialGroup()
        .addContainerGap()
        .add(accountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(accountInfoLayout.createSequentialGroup()
            .add(membershipTypeLabel)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(membershipType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
          .add(accountInfoLayout.createSequentialGroup()
            .add(infoAccountNumLabel)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 97, Short.MAX_VALUE)
            .add(infoAccountNum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
          .add(infoPhoneNum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(infoAddress, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
          .add(infoCityStateZip, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 177, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .add(60, 60, 60)
        .add(accountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(jScrollPane2, 0, 0, Short.MAX_VALUE)
          .add(accountInfoLayout.createSequentialGroup()
            .add(accountMembersLabel)
            .add(96, 96, 96))
          .add(changeAccountInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 210, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
    accountInfoLayout.setVerticalGroup(
      accountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(accountInfoLayout.createSequentialGroup()
        .add(accountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(accountInfoLayout.createSequentialGroup()
            .add(accountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(infoAccountNumLabel)
              .add(infoAccountNum))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(infoPhoneNum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(infoAddress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(infoCityStateZip)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(accountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(membershipTypeLabel)
              .add(membershipType)))
          .add(accountInfoLayout.createSequentialGroup()
            .add(accountMembersLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(changeAccountInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    newAccountInfo.setBorder(javax.swing.BorderFactory.createTitledBorder("New Account Information"));
    save.setText("Save");
    save.setToolTipText("Make Payment");
    save.setEnabled(false);
    save.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveActionPerformed(evt);
      }
    });

    cancel.setText("Cancel");
    cancel.setToolTipText("Cancel Transaction");
    cancel.setEnabled(false);
    cancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelActionPerformed(evt);
      }
    });

    ownerLabel.setText("Owner:");

    ccNumLabel.setText("Credit Card Number:");

    ccNameLabel.setText("Name on Credit Card:");

    ccExpMonthLabel.setText("Expiration Month:");

    ccExpYearLabel.setText("Expiration Year:");

    ccNum.setEditable(false);

    ccName.setEditable(false);

    ccExpMonth.setEditable(false);

    ccExpYear.setEditable(false);

    membershipTypeNewAcctLabel.setText("All new accounts are free memberships.");

    membershipTypeNewAcctLabel1.setText("To upgrade an account, it may be purchased");

    membershipTypeNewAcctLabel2.setText("through a sales transaction.");

    org.jdesktop.layout.GroupLayout newAccountInfoLayout = new org.jdesktop.layout.GroupLayout(newAccountInfo);
    newAccountInfo.setLayout(newAccountInfoLayout);
    newAccountInfoLayout.setHorizontalGroup(
      newAccountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, newAccountInfoLayout.createSequentialGroup()
        .addContainerGap()
        .add(newAccountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(ccExpMonthLabel)
          .add(ccNameLabel)
          .add(ownerLabel)
          .add(ccNumLabel)
          .add(ccExpYearLabel))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(newAccountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
          .add(owner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
          .add(ccNum)
          .add(ccName)
          .add(ccExpYear, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(ccExpMonth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .add(60, 60, 60)
        .add(newAccountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(newAccountInfoLayout.createSequentialGroup()
            .add(save)
            .add(37, 37, 37)
            .add(cancel))
          .add(membershipTypeNewAcctLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 271, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(membershipTypeNewAcctLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 271, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(membershipTypeNewAcctLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 271, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(272, Short.MAX_VALUE))
    );
    newAccountInfoLayout.setVerticalGroup(
      newAccountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, newAccountInfoLayout.createSequentialGroup()
        .add(newAccountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(newAccountInfoLayout.createSequentialGroup()
            .add(newAccountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(owner)
              .add(ownerLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(newAccountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(ccNumLabel)
              .add(ccNum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(newAccountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(ccNameLabel)
              .add(ccName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(newAccountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(ccExpMonthLabel)
              .add(ccExpMonth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
          .add(newAccountInfoLayout.createSequentialGroup()
            .add(24, 24, 24)
            .add(membershipTypeNewAcctLabel)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(membershipTypeNewAcctLabel1)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(membershipTypeNewAcctLabel2)))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(newAccountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(newAccountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
            .add(ccExpYear, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(save)
            .add(cancel))
          .add(ccExpYearLabel)))
    );

    newCustomerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("New Customer Information"));
    firstNameNewCustLabel.setText("First Name:");

    addCustomer.setText("Add Customer");
    addCustomer.setToolTipText("Add Item to Transaction");
    addCustomer.setEnabled(false);
    addCustomer.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addCustomerActionPerformed(evt);
      }
    });

    lastnameNewCust.setText("Last Name:");

    phoneNumNewCustLabel.setText("Phone Number:");

    firstNameNewCust.setEditable(false);

    lastNameNewCust.setEditable(false);

    phoneNumNewCust.setEditable(false);

    addressNewCust.setEditable(false);

    addressNewCustLabel.setText("Address:");

    cityNewCustLabel.setText("City:");

    zipCodeNewCustLabel.setText("Zip Code:");

    stateNewCustLabel.setText("State:");

    zipcodeNewCust.setEditable(false);

    cityNewCust.setEditable(false);

    stateNewCust.setEditable(false);

    cancel1.setText("Cancel");
    cancel1.setToolTipText("Cancel Transaction");
    cancel1.setEnabled(false);
    cancel1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancel1ActionPerformed(evt);
      }
    });

    org.jdesktop.layout.GroupLayout newCustomerPanelLayout = new org.jdesktop.layout.GroupLayout(newCustomerPanel);
    newCustomerPanel.setLayout(newCustomerPanelLayout);
    newCustomerPanelLayout.setHorizontalGroup(
      newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(newCustomerPanelLayout.createSequentialGroup()
        .add(newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(newCustomerPanelLayout.createSequentialGroup()
            .add(39, 39, 39)
            .add(newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
              .add(cityNewCustLabel)
              .add(zipCodeNewCustLabel)
              .add(phoneNumNewCustLabel)
              .add(lastnameNewCust)
              .add(addressNewCustLabel)
              .add(stateNewCustLabel)
              .add(firstNameNewCustLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(phoneNumNewCust, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
              .add(addressNewCust, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
              .add(cityNewCust, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
              .add(lastNameNewCust, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
              .add(zipcodeNewCust, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(stateNewCust, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(firstNameNewCust, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)))
          .add(org.jdesktop.layout.GroupLayout.TRAILING, newCustomerPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(addCustomer)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(cancel1)))
        .addContainerGap())
    );
    newCustomerPanelLayout.setVerticalGroup(
      newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(newCustomerPanelLayout.createSequentialGroup()
        .add(newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(firstNameNewCustLabel)
          .add(firstNameNewCust, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lastnameNewCust)
          .add(lastNameNewCust, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(phoneNumNewCustLabel)
          .add(phoneNumNewCust, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(addressNewCustLabel)
          .add(addressNewCust, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(zipCodeNewCustLabel)
          .add(zipcodeNewCust, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(cityNewCustLabel)
          .add(cityNewCust, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(stateNewCustLabel)
          .add(stateNewCust, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(newCustomerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(cancel1)
          .add(addCustomer))
        .addContainerGap())
    );

    members.setBorder(javax.swing.BorderFactory.createTitledBorder("BlockWood Members Lookup"));
    getAccountSummary.setText("Get Account Summary");
    getAccountSummary.setToolTipText("Get Account Summary");
    getAccountSummary.setEnabled(false);
    getAccountSummary.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        getAccountSummaryActionPerformed(evt);
      }
    });

    accountsList.setEnabled(false);
    accountsList.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(java.awt.event.KeyEvent evt) {
        accountsListKeyTyped(evt);
      }
    });
    accountsList.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        accountsListMouseClicked(evt);
      }
    });

    jScrollPane1.setViewportView(accountsList);

    org.jdesktop.layout.GroupLayout membersLayout = new org.jdesktop.layout.GroupLayout(members);
    members.setLayout(membersLayout);
    membersLayout.setHorizontalGroup(
      membersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, membersLayout.createSequentialGroup()
        .addContainerGap()
        .add(membersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, getAccountSummary, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
        .addContainerGap())
    );
    membersLayout.setVerticalGroup(
      membersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, membersLayout.createSequentialGroup()
        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(getAccountSummary))
    );

    lookupMember.setBorder(javax.swing.BorderFactory.createTitledBorder("Lookup Previous Account"));
    lastNameTextField.setEditable(false);
    lastNameTextField.setPreferredSize(new java.awt.Dimension(100, 21));
    lastNameTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lookupMembersActionPerformed(evt);
      }
    });
    lastNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        lastNameTextFieldFocusGained(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        lastNameTextFieldFocusLost(evt);
      }
    });

    firstNameTextField.setEditable(false);
    firstNameTextField.setPreferredSize(new java.awt.Dimension(100, 21));
    firstNameTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lookupMembersActionPerformed(evt);
      }
    });
    firstNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        firstNameTextFieldFocusGained(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        firstNameTextFieldFocusLost(evt);
      }
    });

    phoneNumTextField.setEditable(false);
    phoneNumTextField.setPreferredSize(new java.awt.Dimension(50, 21));
    phoneNumTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lookupMembersActionPerformed(evt);
      }
    });
    phoneNumTextField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        phoneNumTextFieldFocusGained(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        phoneNumTextFieldFocusLost(evt);
      }
    });

    accountNumTextField.setEditable(false);
    accountNumTextField.setPreferredSize(new java.awt.Dimension(145, 21));
    accountNumTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lookupMembersActionPerformed(evt);
      }
    });
    accountNumTextField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        accountNumTextFieldFocusGained(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        accountNumTextFieldFocusLost(evt);
      }
    });

    lastNameLabel.setText("Last Name ");

    firstNameLabel.setText("First Name");

    phoneNumLabel.setText("Phone Number");

    accountNumLabel.setText("Account #");

    lookupMembers.setText("Lookup Members");
    lookupMembers.setEnabled(false);
    lookupMembers.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lookupMembersActionPerformed(evt);
      }
    });

    clearSearch.setText("Clear Search");
    clearSearch.setEnabled(false);
    clearSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        clearSearchActionPerformed(evt);
      }
    });

    org.jdesktop.layout.GroupLayout lookupMemberLayout = new org.jdesktop.layout.GroupLayout(lookupMember);
    lookupMember.setLayout(lookupMemberLayout);
    lookupMemberLayout.setHorizontalGroup(
      lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, lookupMemberLayout.createSequentialGroup()
        .addContainerGap()
        .add(lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
          .add(lookupMemberLayout.createSequentialGroup()
            .add(3, 3, 3)
            .add(phoneNumLabel))
          .add(lastNameLabel)
          .add(lookupMembers, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(phoneNumTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(lastNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .add(13, 13, 13)
        .add(lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(accountNumTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
          .add(accountNumLabel)
          .add(firstNameLabel)
          .add(clearSearch, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
          .add(firstNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))
        .addContainerGap())
    );
    lookupMemberLayout.setVerticalGroup(
      lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(lookupMemberLayout.createSequentialGroup()
        .add(lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(lookupMemberLayout.createSequentialGroup()
            .add(lastNameLabel)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(lastNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(phoneNumLabel)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(phoneNumTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
          .add(lookupMemberLayout.createSequentialGroup()
            .add(firstNameLabel)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(firstNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(accountNumLabel)
            .add(7, 7, 7)
            .add(accountNumTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(clearSearch)
          .add(lookupMembers)))
    );

    newAccountOrCustomer.setBorder(javax.swing.BorderFactory.createTitledBorder("New Account or Customer"));
    buttonGroup1.add(radioAddCustomer);
    radioAddCustomer.setText("Add Customer");
    radioAddCustomer.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    radioAddCustomer.setMargin(new java.awt.Insets(0, 0, 0, 0));
    radioAddCustomer.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        radioAddCustomerActionPerformed(evt);
      }
    });

    buttonGroup1.add(radioCreateAccount);
    radioCreateAccount.setText("Create New Account");
    radioCreateAccount.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    radioCreateAccount.setMargin(new java.awt.Insets(0, 0, 0, 0));
    radioCreateAccount.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        radioCreateAccountActionPerformed(evt);
      }
    });

    org.jdesktop.layout.GroupLayout newAccountOrCustomerLayout = new org.jdesktop.layout.GroupLayout(newAccountOrCustomer);
    newAccountOrCustomer.setLayout(newAccountOrCustomerLayout);
    newAccountOrCustomerLayout.setHorizontalGroup(
      newAccountOrCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(newAccountOrCustomerLayout.createSequentialGroup()
        .add(25, 25, 25)
        .add(radioCreateAccount)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(radioAddCustomer)
        .addContainerGap(51, Short.MAX_VALUE))
    );
    newAccountOrCustomerLayout.setVerticalGroup(
      newAccountOrCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(newAccountOrCustomerLayout.createSequentialGroup()
        .add(newAccountOrCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(radioCreateAccount)
          .add(radioAddCustomer))
        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
    mainPanel.setLayout(mainPanelLayout);
    mainPanelLayout.setHorizontalGroup(
      mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(mainPanelLayout.createSequentialGroup()
        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
          .add(org.jdesktop.layout.GroupLayout.LEADING, mainPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(newAccountInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .add(org.jdesktop.layout.GroupLayout.LEADING, mainPanelLayout.createSequentialGroup()
            .add(12, 12, 12)
            .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
              .add(newAccountOrCustomer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .add(newCustomerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
              .add(mainPanelLayout.createSequentialGroup()
                .add(lookupMember, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(members, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
              .add(accountInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    mainPanelLayout.setVerticalGroup(
      mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(mainPanelLayout.createSequentialGroup()
        .addContainerGap()
        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(mainPanelLayout.createSequentialGroup()
            .add(newAccountOrCustomer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(newCustomerPanel, 0, 265, Short.MAX_VALUE))
          .add(mainPanelLayout.createSequentialGroup()
            .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
              .add(members, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(lookupMember, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(accountInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(newAccountInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(27, 27, 27))
    );
    jPanel1.add(mainPanel);

    mainScrollPanel.setViewportView(jPanel1);

    getContentPane().add(mainScrollPanel, java.awt.BorderLayout.CENTER);

    mainLabelPanel.setLayout(new java.awt.BorderLayout());

    mainLabelPanel.setAutoscrolls(true);
    blockWoodLabel.setBackground(new java.awt.Color(204, 0, 51));
    blockWoodLabel.setFont(new java.awt.Font("Monotype Corsiva", 0, 36));
    blockWoodLabel.setForeground(new java.awt.Color(255, 255, 255));
    blockWoodLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    blockWoodLabel.setText("BlockWood Video Account Management");
    blockWoodLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    blockWoodLabel.setMaximumSize(new java.awt.Dimension(627, 45));
    blockWoodLabel.setOpaque(true);
    blockWoodLabel.setPreferredSize(new java.awt.Dimension(412, 50));
    mainLabelPanel.add(blockWoodLabel, java.awt.BorderLayout.NORTH);

    logPanel.setBackground(new java.awt.Color(204, 0, 51));
    logPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    logPanel.setForeground(new java.awt.Color(255, 255, 255));
    logPanel.setMinimumSize(new java.awt.Dimension(155, 41));
    logPanel.setPreferredSize(new java.awt.Dimension(155, 41));
    logLabel.setBackground(new java.awt.Color(215, 214, 214));
    logLabel.setForeground(new java.awt.Color(255, 255, 255));
    logLabel.setText("Logged in as:");
    logPanel.add(logLabel);

    jSeparator1.setBackground(new java.awt.Color(204, 0, 51));
    jSeparator1.setForeground(new java.awt.Color(204, 0, 51));
    jSeparator1.setPreferredSize(new java.awt.Dimension(25, 10));
    logPanel.add(jSeparator1);

    employeeName.setBackground(new java.awt.Color(215, 214, 214));
    employeeName.setForeground(new java.awt.Color(255, 255, 255));
    employeeName.setText("Ben Robinson");
    logPanel.add(employeeName);

    jSeparator2.setBackground(new java.awt.Color(204, 0, 51));
    jSeparator2.setForeground(new java.awt.Color(204, 0, 51));
    jSeparator2.setPreferredSize(new java.awt.Dimension(25, 10));
    logPanel.add(jSeparator2);

    employeeIDLabel.setForeground(new java.awt.Color(255, 255, 255));
    employeeIDLabel.setText("Employee ID:");
    logPanel.add(employeeIDLabel);

    employeeID.setForeground(new java.awt.Color(255, 255, 255));
    employeeID.setText("788997");
    logPanel.add(employeeID);

    jSeparator4.setBackground(new java.awt.Color(204, 0, 51));
    jSeparator4.setForeground(new java.awt.Color(204, 0, 51));
    jSeparator4.setPreferredSize(new java.awt.Dimension(25, 10));
    logPanel.add(jSeparator4);

    logButton.setText("Logout");
    logButton.setToolTipText("Logout Current User");
    logButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        logButtonActionPerformed(evt);
      }
    });

    logPanel.add(logButton);

    mainLabelPanel.add(logPanel, java.awt.BorderLayout.SOUTH);

    getContentPane().add(mainLabelPanel, java.awt.BorderLayout.NORTH);

    group1aLabel.setBackground(new java.awt.Color(204, 0, 51));
    group1aLabel.setForeground(new java.awt.Color(255, 255, 255));
    group1aLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    group1aLabel.setText("Created by Group 1A");
    group1aLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    group1aLabel.setMinimumSize(new java.awt.Dimension(100, 25));
    group1aLabel.setOpaque(true);
    group1aLabel.setPreferredSize(new java.awt.Dimension(100, 25));
    getContentPane().add(group1aLabel, java.awt.BorderLayout.SOUTH);

    File.setText("File");
    login.setText("Login");
    File.add(login);

    logout.setText("Logout");
    File.add(logout);

    properties.setText("Properties");
    File.add(properties);

    exit.setText("Exit");
    File.add(exit);

    mainMenuBar.add(File);

    Rentals.setText("Rentals");
    transaction.setText("Make Another Transaction");
    Rentals.add(transaction);

    checkInVideo.setText("Check-in Video");
    Rentals.add(checkInVideo);

    movieAvailability.setText("Check Movie Availability");
    Rentals.add(movieAvailability);

    reserveMovie.setText("Reserve Movie");
    Rentals.add(reserveMovie);

    mainMenuBar.add(Rentals);

    Accounts.setText("Accounts");
    createAccount.setText("Create New Account");
    createAccount.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        createAccountActionPerformed(evt);
      }
    });

    Accounts.add(createAccount);

    addCustomer1.setText("Add Customer to Existing Account");
    addCustomer1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addCustomer1ActionPerformed(evt);
      }
    });

    Accounts.add(addCustomer1);

    updateAccount.setText("Update Customer Account");
    Accounts.add(updateAccount);

    buyMembership.setText("Buy Membership");
    buyMembership.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buyMembershipActionPerformed(evt);
      }
    });

    Accounts.add(buyMembership);

    renewMembership.setText("Renew Expired Memberships");
    renewMembership.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        renewMembershipActionPerformed(evt);
      }
    });

    Accounts.add(renewMembership);

    DeleteAccount.setLabel("Delete Account");
    Accounts.add(DeleteAccount);

    mainMenuBar.add(Accounts);

    Orders.setText("Orders");
    jMenuItem15.setText("Check New Releases");
    Orders.add(jMenuItem15);

    jMenuItem16.setText("Order New Videos");
    Orders.add(jMenuItem16);

    jMenuItem17.setText("Order Other Inventory");
    Orders.add(jMenuItem17);

    jMenuItem18.setText("Check-in Received Videos");
    Orders.add(jMenuItem18);

    jMenuItem19.setText("Transfer Inventory From Rental to Sale");
    Orders.add(jMenuItem19);

    mainMenuBar.add(Orders);

    Reports.setText("Reports");
    jMenuItem5.setText("Print Overdue List");
    Reports.add(jMenuItem5);

    Print.setText("Print Rental Inventory");
    Reports.add(Print);

    jMenuItem21.setText("Print For Sale Inventory");
    Reports.add(jMenuItem21);

    jMenuItem22.setText("Print Daily/Weekly/Monthly Revenue");
    Reports.add(jMenuItem22);

    jMenuItem23.setText("Print Rental Statistics");
    Reports.add(jMenuItem23);

    mainMenuBar.add(Reports);

    Help.setText("Help");
    jMenuItem24.setText("About This Program");
    Help.add(jMenuItem24);

    jMenuItem25.setText("Help Topics");
    Help.add(jMenuItem25);

    mainMenuBar.add(Help);

    setJMenuBar(mainMenuBar);

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void renewMembershipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renewMembershipActionPerformed
      try{
        RenewalBatch rb = new RenewalBatch();
        rb.RenewMemberships();
        JOptionPane.showMessageDialog(this,"The expired memberships have been renewed.", "Membership Renewal", JOptionPane.INFORMATION_MESSAGE);
      } catch(RenewalException e1 ){
        JOptionPane.showMessageDialog(this, e1.getMessage(), "Membership Renewal Error", JOptionPane.ERROR_MESSAGE);
      } catch(Exception e ){
        JOptionPane.showMessageDialog(this, "An error occured while the renewing the expired memberships.\n" + e.getMessage(), "Membership Renewal Error", JOptionPane.ERROR_MESSAGE);
      }
    }//GEN-LAST:event_renewMembershipActionPerformed

    private void buyMembershipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buyMembershipActionPerformed
//here we can have a box ask which kind of membership and set the sku and add it if a customer is selected
    }//GEN-LAST:event_buyMembershipActionPerformed

    private void addCustomer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCustomer1ActionPerformed
      GUIAcctManagement guiAcctManage = new GUIAcctManagement();
      guiAcctManage.setVisible(true);
    }//GEN-LAST:event_addCustomer1ActionPerformed

    private void createAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createAccountActionPerformed
      GUIAcctManagement guiAcctManage = new GUIAcctManagement();
      guiAcctManage.setVisible(true);
    }//GEN-LAST:event_createAccountActionPerformed
    
    private void cancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel1ActionPerformed
      clearTransaction();
    }//GEN-LAST:event_cancel1ActionPerformed
    
    private void radioCreateAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioCreateAccountActionPerformed
      enableCustPanel(true);
      enableNewAcctPanel(true);
      enableLookupPreviousAcct(false);
      firstNameNewCust.requestFocus();
    }//GEN-LAST:event_radioCreateAccountActionPerformed
    
    private void radioAddCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioAddCustomerActionPerformed
      enableCustPanel(true);
      enableNewAcctPanel(false);
      enableLookupPreviousAcct(true);
      lastNameTextField.requestFocus();
    }//GEN-LAST:event_radioAddCustomerActionPerformed
    
    private void accountsListKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_accountsListKeyTyped
      setAccountLookupInfo();
    }//GEN-LAST:event_accountsListKeyTyped
    
    private void accountsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_accountsListMouseClicked
      setAccountLookupInfo();
    }//GEN-LAST:event_accountsListMouseClicked
    
    private void getAccountSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getAccountSummaryActionPerformed
      Customer cust = (Customer)accountsList.getSelectedValue();
      getAccountSummary(cust);
    }//GEN-LAST:event_getAccountSummaryActionPerformed
    
    private void clearSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearSearchActionPerformed
      clearTransaction();
    }//GEN-LAST:event_clearSearchActionPerformed
    
    private void phoneNumTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_phoneNumTextFieldFocusLost
      accountNumTextField.setEditable(true);
    }//GEN-LAST:event_phoneNumTextFieldFocusLost
    
    private void phoneNumTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_phoneNumTextFieldFocusGained
      accountNumTextField.setEditable(false);
    }//GEN-LAST:event_phoneNumTextFieldFocusGained
    
    private void firstNameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_firstNameTextFieldFocusGained
      accountNumTextField.setEditable(false);
    }//GEN-LAST:event_firstNameTextFieldFocusGained
    
    private void firstNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_firstNameTextFieldFocusLost
      accountNumTextField.setEditable(true);
    }//GEN-LAST:event_firstNameTextFieldFocusLost
    
    private void lastNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lastNameTextFieldFocusLost
      accountNumTextField.setEditable(true);
    }//GEN-LAST:event_lastNameTextFieldFocusLost
    
    private void lastNameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lastNameTextFieldFocusGained
      accountNumTextField.setEditable(false);
    }//GEN-LAST:event_lastNameTextFieldFocusGained
    
    private void accountNumTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_accountNumTextFieldFocusGained
      lastNameTextField.setEditable(false);
      firstNameTextField.setEditable(false);
      phoneNumTextField.setEditable(false);
    }//GEN-LAST:event_accountNumTextFieldFocusGained
    
    private void accountNumTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_accountNumTextFieldFocusLost
      lastNameTextField.setEditable(true);
      firstNameTextField.setEditable(true);
      phoneNumTextField.setEditable(true);
    }//GEN-LAST:event_accountNumTextFieldFocusLost
    
    private void lookupMembersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lookupMembersActionPerformed
      String lName = lastNameTextField.getText();
      String fName = firstNameTextField.getText();
      String phoneNum = phoneNumTextField.getText();
      String accountNum = accountNumTextField.getText();
      
      //checks to make sure they are all not blank; there must be a search criteria
      if (accountNum.equals("") && phoneNum.equals("") && fName.equals("") && lName.equals("")){
        return;
      }
      
      try{
        
        DefaultListModel membersListModel;
        
        // Look up accountsList if account is null then it searchs by first name, last name, or phone number
        if(accountNum.equals("") || (!accountNumTextField.isEditable())){
          membersListModel = amc.lookupMembersByNameOrPhone(lName, fName, phoneNum);
        } else{
          if (accountNum.length() != 7){
            JOptionPane.showMessageDialog(this, "All account numbers are 7 digits.\n" + "Please enter a 7 digit account number.",
                    "Invalid Account Number", JOptionPane.ERROR_MESSAGE);
            return;
          }
          try{
            membersListModel = amc.lookupMembersByAccountNum(accountNum);
            //custList= AccountDAO.getInstance().read(accountNum).getCustomers();
          } catch (DataException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occured while retrieving membership info.\n" +
                    "Please try again.", "Membership Retreival Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
        
        accountsList.setModel(membersListModel);
        accountsList.setSelectedIndex(0);
        accountsList.requestFocus();
        
      } catch (NumberFormatException e){
        JOptionPane.showMessageDialog(this, "You have entered an invalid phone number.\n" + "Try one of these formats:\n" +
                "801-555-5555 \n(801)-555-5555\n8015555555" , "Invalid Phone Number", JOptionPane.ERROR_MESSAGE);
      } catch (Exception e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage() , "", JOptionPane.ERROR_MESSAGE);
      }
      
    }//GEN-LAST:event_lookupMembersActionPerformed
    
    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
      try{
        createsAccount();
      }catch(Exception e){
        JOptionPane.showMessageDialog(this, e.getMessage(), "Account Creation Error", JOptionPane.ERROR_MESSAGE);
      }
      JOptionPane.showMessageDialog(this, "The new account and customer have been created.", "New Account Created", JOptionPane.PLAIN_MESSAGE);
    }//GEN-LAST:event_saveActionPerformed
    
    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
      clearTransaction();
    }//GEN-LAST:event_cancelActionPerformed
    
    private void changeAccountInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeAccountInfoActionPerformed
      JOptionPane.showMessageDialog(this, "Currently, this function is not available.", "Update Account Information", JOptionPane.PLAIN_MESSAGE);
    }//GEN-LAST:event_changeAccountInfoActionPerformed
    
    private void addCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCustomerActionPerformed
      if(!accountMustBeSelected()){
        return;
      }
      addCustomer();
      JOptionPane.showMessageDialog(this, "The customer has been added to the account.", "Customer Added to Account", JOptionPane.PLAIN_MESSAGE);
    }//GEN-LAST:event_addCustomerActionPerformed
    
    private void logButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logButtonActionPerformed
      int i = JOptionPane.showConfirmDialog(this, "Do you really want to logout?\n This will close the program."
              , "Login/Logout", JOptionPane.ERROR_MESSAGE);
      if (i == 0){
        System.exit(0);
      }
    }//GEN-LAST:event_logButtonActionPerformed
    /** Prints the data from the table when the button is pressed     */    /** Deletes a row from the table when the button is pressed     */    /** Adds a row to the table when the button is pressed     */
    /**
     * @param args the command line arguments
     * This runs the mainframe and makes it visible
     */
    public static void GUIAcctManagement(String args[]) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          new GUIAcctManagement().setVisible(true);
        }
      });
    }
    
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JMenu Accounts;
  private javax.swing.JMenuItem DeleteAccount;
  private javax.swing.JMenu File;
  private javax.swing.JMenu Help;
  private javax.swing.JMenu Orders;
  private javax.swing.JMenuItem Print;
  private javax.swing.JMenu Rentals;
  private javax.swing.JMenu Reports;
  private javax.swing.JPanel accountInfo;
  private javax.swing.JLabel accountMembersLabel;
  private javax.swing.JList accountMembersList;
  private javax.swing.JLabel accountNumLabel;
  private javax.swing.JTextField accountNumTextField;
  private javax.swing.JList accountsList;
  private javax.swing.JButton addCustomer;
  private javax.swing.JMenuItem addCustomer1;
  private javax.swing.JTextField addressNewCust;
  private javax.swing.JLabel addressNewCustLabel;
  private javax.swing.JLabel blockWoodLabel;
  private javax.swing.ButtonGroup buttonGroup1;
  private javax.swing.JMenuItem buyMembership;
  private javax.swing.JButton cancel;
  private javax.swing.JButton cancel1;
  private javax.swing.JTextField ccExpMonth;
  private javax.swing.JLabel ccExpMonthLabel;
  private javax.swing.JTextField ccExpYear;
  private javax.swing.JLabel ccExpYearLabel;
  private javax.swing.JTextField ccName;
  private javax.swing.JLabel ccNameLabel;
  private javax.swing.JTextField ccNum;
  private javax.swing.JLabel ccNumLabel;
  private javax.swing.JButton changeAccountInfo;
  private javax.swing.JMenuItem checkInVideo;
  private javax.swing.JTextField cityNewCust;
  private javax.swing.JLabel cityNewCustLabel;
  private javax.swing.JButton clearSearch;
  private javax.swing.JMenuItem createAccount;
  private javax.swing.JLabel employeeID;
  private javax.swing.JLabel employeeIDLabel;
  private javax.swing.JLabel employeeName;
  private javax.swing.JMenuItem exit;
  private javax.swing.JLabel firstNameLabel;
  private javax.swing.JTextField firstNameNewCust;
  private javax.swing.JLabel firstNameNewCustLabel;
  private javax.swing.JTextField firstNameTextField;
  private javax.swing.JButton getAccountSummary;
  private javax.swing.JLabel group1aLabel;
  private javax.swing.JLabel infoAccountNum;
  private javax.swing.JLabel infoAccountNumLabel;
  private javax.swing.JLabel infoAddress;
  private javax.swing.JLabel infoCityStateZip;
  private javax.swing.JLabel infoPhoneNum;
  private javax.swing.JMenuItem jMenuItem15;
  private javax.swing.JMenuItem jMenuItem16;
  private javax.swing.JMenuItem jMenuItem17;
  private javax.swing.JMenuItem jMenuItem18;
  private javax.swing.JMenuItem jMenuItem19;
  private javax.swing.JMenuItem jMenuItem21;
  private javax.swing.JMenuItem jMenuItem22;
  private javax.swing.JMenuItem jMenuItem23;
  private javax.swing.JMenuItem jMenuItem24;
  private javax.swing.JMenuItem jMenuItem25;
  private javax.swing.JMenuItem jMenuItem5;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JSeparator jSeparator4;
  private javax.swing.JLabel lastNameLabel;
  private javax.swing.JTextField lastNameNewCust;
  private javax.swing.JTextField lastNameTextField;
  private javax.swing.JLabel lastnameNewCust;
  private javax.swing.JToggleButton logButton;
  private javax.swing.JLabel logLabel;
  private javax.swing.JPanel logPanel;
  private javax.swing.JMenuItem login;
  private javax.swing.JMenuItem logout;
  private javax.swing.JPanel lookupMember;
  private javax.swing.JButton lookupMembers;
  private javax.swing.JPanel mainLabelPanel;
  private javax.swing.JMenuBar mainMenuBar;
  private javax.swing.JPanel mainPanel;
  private javax.swing.JScrollPane mainScrollPanel;
  private javax.swing.JPanel members;
  private javax.swing.JLabel membershipType;
  private javax.swing.JLabel membershipTypeLabel;
  private javax.swing.JLabel membershipTypeNewAcctLabel;
  private javax.swing.JLabel membershipTypeNewAcctLabel1;
  private javax.swing.JLabel membershipTypeNewAcctLabel2;
  private javax.swing.JMenuItem movieAvailability;
  private javax.swing.JPanel newAccountInfo;
  private javax.swing.JPanel newAccountOrCustomer;
  private javax.swing.JPanel newCustomerPanel;
  private javax.swing.JLabel owner;
  private javax.swing.JLabel ownerLabel;
  private javax.swing.JLabel phoneNumLabel;
  private javax.swing.JTextField phoneNumNewCust;
  private javax.swing.JLabel phoneNumNewCustLabel;
  private javax.swing.JTextField phoneNumTextField;
  private javax.swing.JMenuItem properties;
  private javax.swing.JRadioButton radioAddCustomer;
  private javax.swing.JRadioButton radioCreateAccount;
  private javax.swing.JMenuItem renewMembership;
  private javax.swing.JMenuItem reserveMovie;
  private javax.swing.JButton save;
  private javax.swing.JTextField stateNewCust;
  private javax.swing.JLabel stateNewCustLabel;
  private javax.swing.JMenuItem transaction;
  private javax.swing.JMenuItem updateAccount;
  private javax.swing.JLabel zipCodeNewCustLabel;
  private javax.swing.JTextField zipcodeNewCust;
  // End of variables declaration//GEN-END:variables
    
}
