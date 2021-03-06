/*
 * MainFrameTransaction.java
 *
 * Created on September 16, 2005, 3:11 PM
 * This program will interface with the user in form of a GUI to process transactions.
 */

package edu.byu.isys413.group1a.intex2.GUIs;

import edu.byu.isys413.group1a.intex2.BOs.Customer;
import edu.byu.isys413.group1a.intex2.BOs.Tx;
import edu.byu.isys413.group1a.intex2.BOs.TxDisplay;
import edu.byu.isys413.group1a.intex2.Controllers.AcctManagementController;
import edu.byu.isys413.group1a.intex2.Controllers.AssessFullCostFees;
import edu.byu.isys413.group1a.intex2.Controllers.RenewalBatch;
import edu.byu.isys413.group1a.intex2.Controllers.TxController;
import edu.byu.isys413.group1a.intex2.DAOs.AccountDAO;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.RenewalException;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Group 1A
 *
 */
public class GUIReturnsTransaction extends javax.swing.JFrame {
  
  private DefaultTableModel tableTransactionModel = null;
  private DefaultTableModel tableReturnsModel = null;
  private TxController txc = null;
  private TxDisplay txd = null;
  private Vector columnIdentifiers = null;
  private AcctManagementController amc = null;
  
  /** Creates new form MainFrameTransaction */
  public GUIReturnsTransaction() {
    /* Sets the GUI to a windows look and feel  */
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception exc) {
      System.err.println("Error loading L&F: " + exc);
    }
    
    initComponents();
    clearTransaction();
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
      JOptionPane.showMessageDialog(this, "You must select an account member to make a purchase.", "Account Not Selected", JOptionPane.ERROR_MESSAGE);
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
    try{
      maxRentals.setText(AccountDAO.getInstance().getRentalsOut(cust.getAccount()) + "/" + AccountDAO.getInstance().getMaxRentals(cust.getAccount()));
    } catch (Exception e){
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, e.getMessage() +
              "Please try again.", "Rental Retreival Error", JOptionPane.ERROR_MESSAGE);
    }
    outstandingBalance.setText((cust.getAccount().getBalance()) +"");
    
    txd.setBalance(Double.parseDouble(outstandingBalance.getText()));
    
    DefaultListModel accountMembersListModel = null;
    
    //Look up members by account number
    try{
      accountMembersListModel = amc.lookupMembersByAccountNum(cust.getAccount().getAccountNum());
    } catch (Exception e){
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, "An error occured while retrieving membership info.\n" +
              "Please try again.", "Membership Retreival Error", JOptionPane.ERROR_MESSAGE);
    }
    
    accountMembersList.setModel(accountMembersListModel);
    
    DefaultListModel singledOutMember = new DefaultListModel();
    singledOutMember.addElement(cust);
    accountsList.setModel(singledOutMember);
    txc.setCust(cust);//
    
    // Initialize transaction
    try{
      tableTransactionModel.setDataVector(null, columnIdentifiers);
      txc.newTx(cust);//Problem
    }catch (Exception e){
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, "An error occured while initializing transaction", "Sku/Serial Number Not Valid", JOptionPane.PLAIN_MESSAGE);
    }
    txc.setTxDisplay(txd);
    txc.calculateMinBalance();
    txd = txc.getTxDisplay();
    
    //txc.calculateTaxSubTotals();
    //txc.calculateTotal();
    
    setDisplay();
    
    skuTextField.requestFocus();
  }
  
  /** This adds an item to the transaction **/
  private void addItem(String skuSerialNum, String quantity){
    //this checks to make sure an account is selected before adding an item to a transaction
    if(!accountMustBeSelected()){
      return;
    }
    if (skuSerialNum.length() == 8){
      String rentals = maxRentals.getText();
      String [] out = rentals.split("/");
      if(Integer.parseInt(out[1])- Integer.parseInt(out[0])>0){
        quantity = "0";
      }else{
        if(!out[1].equals("0")){
          JOptionPane.showMessageDialog(this, "This rental will require a fee", "Maximum free rentals exceeded", JOptionPane.ERROR_MESSAGE);
        }
      }
      maxRentals.setText((Integer.parseInt(out[0])+1)+"/" +out[1]);//?
    }
    if (skuSerialNum.length() == 8 || skuSerialNum.length() == 12 || skuSerialNum.length() == 2 ){
      try{
        Vector data = txc.addTransactionLine(skuSerialNum, quantity);
        tableTransactionModel.setDataVector(data, columnIdentifiers);
        txc.calculateTaxSubTotals();
        txc.calculateTotal();
        setDisplay();
      } catch (Exception e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(), "Sku/Serial Number Not Valid", JOptionPane.PLAIN_MESSAGE);
      }
    } else{
      JOptionPane.showMessageDialog(this, "You must enter a rental serial of 8 digits\n or a product sku of 12 digits to add the transaction line item.", "Must Enter Sku/Serial Number", JOptionPane.PLAIN_MESSAGE);
    }
    skuTextField.setText("");
    skuTextField.requestFocus();
    quantityTextField.setText("1");
  }
  
  /** Remove item from transaction */
  public void removeItem(){
    int rowNewTx = transactionTable.getSelectedRow();
    int columnOfQuantity = 0, quantityNewTx = 0, quantityOldTx = 0, rowOfOldTx = 0, columnOfSku = 0;
    // Checks to make sure the row is valid
    // Gets the column number of sku
    columnOfSku = columnIdentifiers.indexOf("Sku/Serial #");
    // Gets the column number of quantity
    columnOfQuantity = columnIdentifiers.indexOf("Quantity");
    //gets the quantity in the new transaction to see if it is negative
    quantityNewTx =  Integer.parseInt(tableTransactionModel.getValueAt(rowNewTx, columnOfQuantity).toString());
    
    String skuOldTx = "";
    String skuNewTx = "";
    //Gets the sku of the new transaction
    skuNewTx = tableTransactionModel.getValueAt(rowNewTx, columnOfSku).toString();
    // Check to see if the quantity is negative, which means it is a return
    // Then adds the quantity back to the old transaction
    if (quantityNewTx < 0){
      //Loops to find the return item sku in order to get the row
      for (int i=0; i < transactionTableReturns.getRowCount(); i++){
        skuOldTx= tableReturnsModel.getValueAt(i, columnOfSku).toString();
        //checks to see if the skus are the same in the old and new and sets the rowcount
        if (skuNewTx.equals(skuOldTx)){
          rowOfOldTx = i;
          i = transactionTableReturns.getRowCount()+1;
        }
      }
      quantityOldTx = Integer.parseInt(tableReturnsModel.getValueAt(rowOfOldTx, columnOfQuantity).toString());
      //if quantity is greater than zero
      if (quantityOldTx >= 0 ){
        quantityOldTx++;
        tableReturnsModel.setValueAt(quantityOldTx, rowOfOldTx, columnOfQuantity);
      }
    }
    if(rowNewTx >= 0){
      Vector data = txc.removeTransactionLine(rowNewTx);
      tableTransactionModel.setDataVector(data, columnIdentifiers);
      txc.calculateTaxSubTotals();
      txc.calculateTotal();
      setDisplay();
      displayTotals();
      
      if (skuNewTx.length() == 8){
        String rentals = maxRentals.getText();
        String [] out = rentals.split("/");
        maxRentals.setText((Integer.parseInt(out[0])-1)+"/" +out[1]);//?
      }
    }
  }
  
  /** This sets the display to have the current values **/
  private void setDisplay() {//throws Exception{
    txd = txc.getTxDisplay();
    subTotalTextField.setText(formatDisplay(txd.getSubTotal()));
    totalTextField.setText(formatDisplay(txd.getTotal()));
    paymentTextField.setText(formatDisplay(txd.getPayment()));
    changeDueTextField.setText(formatDisplay(txd.getChange()));
    taxTextField.setText(formatDisplay(txd.getTax()));
    minBalance.setText(formatDisplay(txd.getMinAmtDue()));
    outstandingBalance.setText(formatDisplay(txd.getBalance()));
    balancePaymentTextField.setText(formatDisplay(txd.getBalancePayment()));
  }
  
  /** Formats the variables to have two decimal places **/
  private String formatDisplay(double value){
    DecimalFormat fmt = new DecimalFormat("0.00");
    return fmt.format(value);
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
    
    //clears the totals panel
    maxRentals.setText("0/0");
    subTotalTextField.setText("0.00");
    outstandingBalance.setText("0.00");
    minBalance.setText("0.00");
    taxTextField.setText("0.00");
    balancePaymentTextField.setText("0.00");
    totalTextField.setText("0.00");
    paymentTextField.setText("0.00");
    changeDueTextField.setText("0.00");
    
    //clears the sku transaction data
    skuTextField.setText("");
    quantityTextField.setText("1");
    tableTransactionModel = new DefaultTableModel();
    tableReturnsModel = new DefaultTableModel();
    txd = new TxDisplay();
    amc = new AcctManagementController();
    txc = new TxController();
    
    //Sets the new table for transaction
    columnIdentifiers = new Vector();
    columnIdentifiers.add("Quantity");
    columnIdentifiers.add("Description");
    columnIdentifiers.add("Sku/Serial #");
    columnIdentifiers.add("Due Date");
    columnIdentifiers.add("Price");
    tableTransactionModel.setColumnIdentifiers(columnIdentifiers);
    transactionTable.setModel(tableTransactionModel);
    
    //Sets the new table for returns
    tableReturnsModel.setColumnIdentifiers(columnIdentifiers);
    transactionTableReturns.setModel(tableReturnsModel);
    transactionID.setText("");
    
    tableTransactionModel.fireTableDataChanged();
    displayReturns();
    //sends the focus to lookup an account
    lastNameTextField.requestFocus();
  }
  /** Displays the panel with the returns information */
  private void displayReturns(){
    returnsPanel.setVisible(true);
    totals.setVisible(false);
    returns.setText("Totals");
  }
  /** Displays the panel with the totals information */
  private void displayTotals(){
    returnsPanel.setVisible(false);
    totals.setVisible(true);
    returns.setText("Returns");
  }
  /** Checks to make sure the balance payment is not too much nor too little */
  private void checkBalancePayment(){
    txd = txc.getTxDisplay();
    
    try{
      if (Double.parseDouble(balancePaymentTextField.getText()) < txd.getMinAmtDue()){
        JOptionPane.showMessageDialog(this, "Customer must pay at least the minimum balance.\n" +
                "The minimum amount due is " + txd.getMinAmtDue() , "Minimum Balance Due", JOptionPane.ERROR_MESSAGE);
        balancePaymentTextField.setText(formatDisplay(txd.getMinAmtDue()));
      } else if (Double.parseDouble(balancePaymentTextField.getText()) > txd.getBalance()){
        JOptionPane.showMessageDialog(this, "The payment amount is greater than the amount due.\n" +
                "The outstanding balance is " + txd.getBalance(), "Exceeded Balance Payment", JOptionPane.ERROR_MESSAGE);
        txd.setBalancePayment(Double.parseDouble(outstandingBalance.getText()));
      } else{
        txd.setBalancePayment(Double.parseDouble(balancePaymentTextField.getText()));
      }
      
    } catch (NumberFormatException e){
      JOptionPane.showMessageDialog(this, "You have entered an invalid character.", "Invalid Character Entered", JOptionPane.ERROR_MESSAGE);
      txd.setBalancePayment(txd.getMinAmtDue());
    } finally{
      txc.setTxDisplay(txd);
      txc.calculateTotal();
      setDisplay();
    }
    paymentTextField.requestFocus();
  }
  /** Checks to make sure payment amount is valid*/
  private void checkPayment(){
    txd = txc.getTxDisplay();
    try{
      txd.setPayment(Double.parseDouble(paymentTextField.getText()));
      txc.setTxDisplay(txd);
      txc.calculateTotalPaid();
      txd = txc.getTxDisplay();
      
      changeDueTextField.setText(formatDisplay(txd.getChange()));
      
      if (txd.getChange() < 0){
        JOptionPane.showMessageDialog(this, "The amount entered is insufficient.\n" +
                "The minimum payment for this transaction is: " + txd.getTotal() , "Minimum Payment Due", JOptionPane.ERROR_MESSAGE);
        paymentTextField.setText(formatDisplay(txd.getTotal()));
        changeDueTextField.setText("0.00");
      }
    } catch (NumberFormatException e){
      JOptionPane.showMessageDialog(this, "You have entered an invalid character.", "Invalid Character Entered", JOptionPane.ERROR_MESSAGE);
      paymentTextField.setText(formatDisplay(txd.getTotal()));
      changeDueTextField.setText("0.00");
    }
  }
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {
    blockWoodLabel = new javax.swing.JLabel();
    mainScrollPanel = new javax.swing.JScrollPane();
    jPanel1 = new javax.swing.JPanel();
    mainPanel = new javax.swing.JPanel();
    accountInfo = new javax.swing.JPanel();
    updateAccountInfo = new javax.swing.JButton();
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
    totals = new javax.swing.JPanel();
    totalLabel = new javax.swing.JLabel();
    subTotalLabel = new javax.swing.JLabel();
    taxLabel = new javax.swing.JLabel();
    balancePaymentLabel = new javax.swing.JLabel();
    balancePaymentTextField = new javax.swing.JTextField();
    totalTextField = new javax.swing.JTextField();
    taxTextField = new javax.swing.JTextField();
    subTotalTextField = new javax.swing.JTextField();
    changeDueLabel = new javax.swing.JLabel();
    paymentLabel = new javax.swing.JLabel();
    changeDueTextField = new javax.swing.JTextField();
    paymentTextField = new javax.swing.JTextField();
    makePayment = new javax.swing.JButton();
    cancel = new javax.swing.JButton();
    outstandingBalanceLabel = new javax.swing.JLabel();
    outstandingBalance = new javax.swing.JLabel();
    minBalanceDueLabel = new javax.swing.JLabel();
    minBalance = new javax.swing.JLabel();
    maxRentalsLabel = new javax.swing.JLabel();
    maxRentals = new javax.swing.JLabel();
    transactionPanel = new javax.swing.JPanel();
    quantityLabel = new javax.swing.JLabel();
    quantityTextField = new javax.swing.JTextField();
    skuTextField = new javax.swing.JTextField();
    lookupItem = new javax.swing.JButton();
    addItem = new javax.swing.JButton();
    removeSelectedItem = new javax.swing.JButton();
    skuLabel = new javax.swing.JLabel();
    tableScrollPanel = new javax.swing.JScrollPane();
    transactionTable = new javax.swing.JTable();
    returns = new javax.swing.JButton();
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
    createNewAccount = new javax.swing.JButton();
    lookupMembers = new javax.swing.JButton();
    clearSearch = new javax.swing.JButton();
    returnsPanel = new javax.swing.JPanel();
    transactionIDLabel = new javax.swing.JLabel();
    transactionID = new javax.swing.JTextField();
    returnItem = new javax.swing.JButton();
    cancelReturn = new javax.swing.JButton();
    lookupTransaction = new javax.swing.JButton();
    tableScrollPanel1 = new javax.swing.JScrollPane();
    transactionTableReturns = new javax.swing.JTable();
    mainLabelPanel = new javax.swing.JPanel();
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
    assessFees = new javax.swing.JMenuItem();
    Accounts = new javax.swing.JMenu();
    createAccount = new javax.swing.JMenuItem();
    addCustomer = new javax.swing.JMenuItem();
    updateAccount = new javax.swing.JMenuItem();
    buyMembership = new javax.swing.JMenuItem();
    renewMembership = new javax.swing.JMenuItem();
    DeleteAccount = new javax.swing.JMenuItem();
    InventoryManagement = new javax.swing.JMenu();
    rentalVideo = new javax.swing.JMenuItem();
    conceptualVideo = new javax.swing.JMenuItem();
    store = new javax.swing.JMenuItem();
    product = new javax.swing.JMenuItem();
    storeProduct = new javax.swing.JMenuItem();
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
    jMenuItem25 = new javax.swing.JMenuItem();
    jMenuItem24 = new javax.swing.JMenuItem();

    blockWoodLabel.setBackground(new java.awt.Color(204, 0, 51));
    blockWoodLabel.setFont(new java.awt.Font("Monotype Corsiva", 0, 36));
    blockWoodLabel.setForeground(new java.awt.Color(255, 255, 255));
    blockWoodLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    blockWoodLabel.setText("BlockWood Video Returns Transaction");
    blockWoodLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    blockWoodLabel.setMaximumSize(new java.awt.Dimension(627, 45));
    blockWoodLabel.setOpaque(true);
    blockWoodLabel.setPreferredSize(new java.awt.Dimension(412, 50));

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("BlockWoodVideo");
    setBackground(new java.awt.Color(255, 255, 255));
    accountInfo.setBorder(javax.swing.BorderFactory.createTitledBorder("Account Information"));
    updateAccountInfo.setText("Add Customer/Update Account");
    updateAccountInfo.setToolTipText("Update Account Information");
    updateAccountInfo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        updateAccountInfoActionPerformed(evt);
      }
    });

    accountMembersList.setBackground(new java.awt.Color(236, 233, 216));
    jScrollPane2.setViewportView(accountMembersList);

    infoAccountNumLabel.setText("Account #");

    infoAccountNum.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

    infoCityStateZip.setText(" ");

    membershipType.setFont(new java.awt.Font("Tahoma", 1, 13));
    membershipType.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

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
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
            .addContainerGap())
          .add(accountInfoLayout.createSequentialGroup()
            .add(accountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(accountInfoLayout.createSequentialGroup()
                .add(infoAccountNumLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 102, Short.MAX_VALUE)
                .add(infoAccountNum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(9, 9, 9))
              .add(infoPhoneNum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(infoAddress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 234, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(accountInfoLayout.createSequentialGroup()
                .add(2, 2, 2)
                .add(membershipTypeLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(membershipType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
            .add(12, 12, 12))
          .add(accountInfoLayout.createSequentialGroup()
            .add(updateAccountInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
            .addContainerGap())
          .add(accountInfoLayout.createSequentialGroup()
            .add(accountMembersLabel)
            .addContainerGap(164, Short.MAX_VALUE))
          .add(org.jdesktop.layout.GroupLayout.TRAILING, accountInfoLayout.createSequentialGroup()
            .add(infoCityStateZip, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
            .addContainerGap())))
    );
    accountInfoLayout.setVerticalGroup(
      accountInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
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
          .add(membershipType))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(accountMembersLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(updateAccountInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
    );

    totals.setBorder(javax.swing.BorderFactory.createTitledBorder("Totals"));
    totalLabel.setFont(new java.awt.Font("Tahoma", 1, 13));
    totalLabel.setText("Total:");

    subTotalLabel.setText("SubTotal:");

    taxLabel.setText("Tax:");

    balancePaymentLabel.setText("Balance Payment Amount:");

    balancePaymentTextField.setText("0.00");
    balancePaymentTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        balancePaymentTextFieldActionPerformed(evt);
      }
    });
    balancePaymentTextField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        balancePaymentTextFieldFocusLost(evt);
      }
    });

    totalTextField.setEditable(false);
    totalTextField.setText("0.00");

    taxTextField.setEditable(false);
    taxTextField.setText("0.00");

    subTotalTextField.setEditable(false);
    subTotalTextField.setText("0.00");

    changeDueLabel.setText("Change Due:");

    paymentLabel.setFont(new java.awt.Font("Tahoma", 1, 13));
    paymentLabel.setText("Enter Payment Amount:");

    changeDueTextField.setEditable(false);
    changeDueTextField.setText("$ 0.00");
    changeDueTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        makePaymentActionPerformed(evt);
      }
    });

    paymentTextField.setText("$ 0.00");
    paymentTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        paymentTextFieldActionPerformed(evt);
      }
    });
    paymentTextField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        paymentTextFieldFocusLost(evt);
      }
    });

    makePayment.setText("Make payment");
    makePayment.setToolTipText("Make Payment");
    makePayment.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        makePaymentActionPerformed(evt);
      }
    });

    cancel.setText("Cancel");
    cancel.setToolTipText("Cancel Transaction");
    cancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelActionPerformed(evt);
      }
    });

    outstandingBalanceLabel.setText("Outstanding Balance:");

    outstandingBalance.setFont(new java.awt.Font("Tahoma", 1, 13));
    outstandingBalance.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    outstandingBalance.setText("0.00");

    minBalanceDueLabel.setText("Minimum Balance Due:");

    minBalance.setFont(new java.awt.Font("Tahoma", 1, 13));
    minBalance.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    minBalance.setText("0.00");

    maxRentalsLabel.setText("Max Free Rentals Allowed:");

    maxRentals.setFont(new java.awt.Font("Tahoma", 1, 13));
    maxRentals.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    maxRentals.setText("0/0");

    org.jdesktop.layout.GroupLayout totalsLayout = new org.jdesktop.layout.GroupLayout(totals);
    totals.setLayout(totalsLayout);
    totalsLayout.setHorizontalGroup(
      totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(totalsLayout.createSequentialGroup()
        .addContainerGap()
        .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(totalsLayout.createSequentialGroup()
            .add(10, 10, 10)
            .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
              .add(totalLabel)
              .add(subTotalLabel)
              .add(taxLabel)
              .add(balancePaymentLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
              .add(balancePaymentTextField)
              .add(totalTextField)
              .add(taxTextField)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, subTotalTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(totalsLayout.createSequentialGroup()
                .add(30, 30, 30)
                .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                  .add(changeDueLabel)
                  .add(paymentLabel))
                .add(17, 17, 17)
                .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                  .add(changeDueTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                  .add(paymentTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)))
              .add(totalsLayout.createSequentialGroup()
                .add(61, 61, 61)
                .add(makePayment)
                .add(31, 31, 31)
                .add(cancel)))
            .addContainerGap())
          .add(totalsLayout.createSequentialGroup()
            .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(minBalanceDueLabel)
              .add(outstandingBalanceLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 134, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(outstandingBalance, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, minBalance, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(maxRentalsLabel)
            .add(20, 20, 20)
            .add(maxRentals)
            .add(160, 160, 160))))
    );
    totalsLayout.setVerticalGroup(
      totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(totalsLayout.createSequentialGroup()
        .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(outstandingBalanceLabel)
          .add(outstandingBalance)
          .add(maxRentalsLabel)
          .add(maxRentals))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(minBalanceDueLabel)
          .add(minBalance))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(subTotalTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(subTotalLabel)
          .add(paymentLabel)
          .add(paymentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(taxTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(taxLabel)
          .add(changeDueLabel)
          .add(changeDueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(totalsLayout.createSequentialGroup()
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(balancePaymentLabel)
              .add(balancePaymentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(totalLabel)
              .add(totalTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
          .add(totalsLayout.createSequentialGroup()
            .add(15, 15, 15)
            .add(totalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(makePayment, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(cancel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
    );

    transactionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Transaction"));
    quantityLabel.setFont(new java.awt.Font("Tahoma", 1, 13));
    quantityLabel.setText("Quantity");

    quantityTextField.setText("1");
    quantityTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addItemActionPerformed(evt);
      }
    });
    quantityTextField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        quantityTextFieldFocusLost(evt);
      }
    });

    skuTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addItemActionPerformed(evt);
      }
    });
    skuTextField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        skuTextFieldFocusGained(evt);
      }
    });

    lookupItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/byu/isys413/group1a/intex2/GUIs/lookup_icon.gif")));
    lookupItem.setToolTipText("Lookup Product Item");
    lookupItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lookupItemActionPerformed(evt);
      }
    });

    addItem.setText("Add Item");
    addItem.setToolTipText("Add Item to Transaction");
    addItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addItemActionPerformed(evt);
      }
    });

    removeSelectedItem.setText("Remove Selected Item");
    removeSelectedItem.setToolTipText("Remove Selected Item");
    removeSelectedItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        removeSelectedItemActionPerformed(evt);
      }
    });

    skuLabel.setFont(new java.awt.Font("Tahoma", 1, 13));
    skuLabel.setText("Serial/Sku #");

    transactionTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {"1", "Rocky I", "523452345", "3 days", "$3.50"},
        {"1", "Lord of the Rings", "454352345", "3 days", "$3.50"}
      },
      new String [] {
        "Quantity", "Description", "Serial/Sku", "Due Date", "Price"
      }
    ) {
      boolean[] canEdit = new boolean [] {
        false, false, false, false, false
      };

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    transactionTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    transactionTable.setDragEnabled(true);
    transactionTable.setEditingColumn(0);
    transactionTable.setEditingRow(0);
    transactionTable.setGridColor(new java.awt.Color(255, 255, 255));
    transactionTable.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        transactionTableMouseClicked(evt);
      }
    });

    tableScrollPanel.setViewportView(transactionTable);

    returns.setFont(new java.awt.Font("Tahoma", 1, 13));
    returns.setText("Totals");
    returns.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        returnsActionPerformed(evt);
      }
    });

    org.jdesktop.layout.GroupLayout transactionPanelLayout = new org.jdesktop.layout.GroupLayout(transactionPanel);
    transactionPanel.setLayout(transactionPanelLayout);
    transactionPanelLayout.setHorizontalGroup(
      transactionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(transactionPanelLayout.createSequentialGroup()
        .addContainerGap()
        .add(transactionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(tableScrollPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 597, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(transactionPanelLayout.createSequentialGroup()
            .add(transactionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(quantityLabel)
              .add(transactionPanelLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(quantityTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .add(20, 20, 20)
            .add(transactionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(transactionPanelLayout.createSequentialGroup()
                .add(skuTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lookupItem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addItem)
                .add(18, 18, 18)
                .add(returns))
              .add(skuLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(removeSelectedItem)
            .add(124, 124, 124)))
        .addContainerGap())
    );
    transactionPanelLayout.setVerticalGroup(
      transactionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(transactionPanelLayout.createSequentialGroup()
        .add(transactionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(quantityLabel)
          .add(skuLabel))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(transactionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(quantityTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(skuTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(addItem)
          .add(lookupItem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(returns)
          .add(removeSelectedItem))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(tableScrollPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    members.setBorder(javax.swing.BorderFactory.createTitledBorder("BlockWood Members Lookup"));
    getAccountSummary.setText("Get Account Summary");
    getAccountSummary.setToolTipText("Get Account Summary");
    getAccountSummary.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        getAccountSummaryActionPerformed(evt);
      }
    });

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
      .add(membersLayout.createSequentialGroup()
        .addContainerGap()
        .add(membersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, getAccountSummary, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE))
        .addContainerGap())
    );
    membersLayout.setVerticalGroup(
      membersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(membersLayout.createSequentialGroup()
        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(getAccountSummary)
        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    lookupMember.setBorder(javax.swing.BorderFactory.createTitledBorder("Lookup Account Member"));
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

    createNewAccount.setText("Create New Account");
    createNewAccount.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        createNewAccountActionPerformed(evt);
      }
    });

    lookupMembers.setText("Lookup Members");
    lookupMembers.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lookupMembersActionPerformed(evt);
      }
    });

    clearSearch.setText("Clear Search");
    clearSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        clearSearchActionPerformed(evt);
      }
    });

    org.jdesktop.layout.GroupLayout lookupMemberLayout = new org.jdesktop.layout.GroupLayout(lookupMember);
    lookupMember.setLayout(lookupMemberLayout);
    lookupMemberLayout.setHorizontalGroup(
      lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(lookupMemberLayout.createSequentialGroup()
        .addContainerGap()
        .add(lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lastNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 109, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lastNameLabel))
        .add(15, 15, 15)
        .add(lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lookupMemberLayout.createSequentialGroup()
            .add(firstNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(14, 14, 14)
            .add(phoneNumTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 109, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
          .add(lookupMemberLayout.createSequentialGroup()
            .add(firstNameLabel)
            .add(55, 55, 55)
            .add(phoneNumLabel)))
        .add(15, 15, 15)
        .add(lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lookupMemberLayout.createSequentialGroup()
            .add(accountNumTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(lookupMembers, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 135, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(clearSearch)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(createNewAccount))
          .add(accountNumLabel))
        .addContainerGap(47, Short.MAX_VALUE))
    );
    lookupMemberLayout.setVerticalGroup(
      lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(lookupMemberLayout.createSequentialGroup()
        .add(lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(phoneNumLabel)
          .add(firstNameLabel)
          .add(accountNumLabel)
          .add(lastNameLabel))
        .add(7, 7, 7)
        .add(lookupMemberLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(phoneNumTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(accountNumTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(firstNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lastNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lookupMembers)
          .add(createNewAccount)
          .add(clearSearch)))
    );

    returnsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Returns"));
    returnsPanel.setEnabled(false);
    transactionIDLabel.setText("Transaction ID:");

    transactionID.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lookupTransactionActionPerformed(evt);
      }
    });
    transactionID.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        transactionIDbalancePaymentTextField1FocusLost(evt);
      }
    });

    returnItem.setText("Return Item");
    returnItem.setToolTipText("Make Payment");
    returnItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        returnItemActionPerformed(evt);
      }
    });

    cancelReturn.setText("Cancel");
    cancelReturn.setToolTipText("Cancel Transaction");
    cancelReturn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelReturnActionPerformed(evt);
      }
    });

    lookupTransaction.setText("Lookup Transaction");
    lookupTransaction.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lookupTransactionActionPerformed(evt);
      }
    });

    transactionTableReturns.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {"1", "Rocky I", "523452345", "3 days", "$3.50"},
        {"1", "Lord of the Rings", "454352345", "3 days", "$3.50"}
      },
      new String [] {
        "Quantity", "Description", "Serial/Sku", "Due Date", "Price"
      }
    ) {
      boolean[] canEdit = new boolean [] {
        false, false, false, false, false
      };

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    transactionTableReturns.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    transactionTableReturns.setDragEnabled(true);
    transactionTableReturns.setEditingColumn(0);
    transactionTableReturns.setEditingRow(0);
    transactionTableReturns.setGridColor(new java.awt.Color(255, 255, 255));
    transactionTableReturns.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        transactionTableReturnsMouseClicked(evt);
      }
    });

    tableScrollPanel1.setViewportView(transactionTableReturns);

    org.jdesktop.layout.GroupLayout returnsPanelLayout = new org.jdesktop.layout.GroupLayout(returnsPanel);
    returnsPanel.setLayout(returnsPanelLayout);
    returnsPanelLayout.setHorizontalGroup(
      returnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(returnsPanelLayout.createSequentialGroup()
        .addContainerGap()
        .add(returnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(returnsPanelLayout.createSequentialGroup()
            .add(transactionIDLabel)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(transactionID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 158, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(lookupTransaction)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(returnItem)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(cancelReturn))
          .add(tableScrollPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 597, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    returnsPanelLayout.setVerticalGroup(
      returnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(returnsPanelLayout.createSequentialGroup()
        .add(returnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(transactionIDLabel)
          .add(transactionID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lookupTransaction)
          .add(returnItem)
          .add(cancelReturn))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(tableScrollPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        .addContainerGap())
    );

    org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
    mainPanel.setLayout(mainPanelLayout);
    mainPanelLayout.setHorizontalGroup(
      mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(mainPanelLayout.createSequentialGroup()
        .addContainerGap()
        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
          .add(org.jdesktop.layout.GroupLayout.LEADING, lookupMember, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, mainPanelLayout.createSequentialGroup()
            .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
              .add(mainPanelLayout.createSequentialGroup()
                .add(3, 3, 3)
                .add(members, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
              .add(mainPanelLayout.createSequentialGroup()
                .add(1, 1, 1)
                .add(accountInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
              .add(totals, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .add(transactionPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
              .add(returnsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        .addContainerGap(29, Short.MAX_VALUE))
    );
    mainPanelLayout.setVerticalGroup(
      mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(mainPanelLayout.createSequentialGroup()
        .addContainerGap()
        .add(lookupMember, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(mainPanelLayout.createSequentialGroup()
            .add(members, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(accountInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
          .add(mainPanelLayout.createSequentialGroup()
            .add(transactionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(totals, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(returnsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel1.add(mainPanel);

    mainScrollPanel.setViewportView(jPanel1);

    getContentPane().add(mainScrollPanel, java.awt.BorderLayout.CENTER);

    mainLabelPanel.setLayout(new java.awt.BorderLayout());

    mainLabelPanel.setAutoscrolls(true);
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
    employeeName.setText("James Christensen");
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
    logout.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        logoutActionPerformed(evt);
      }
    });

    File.add(logout);

    properties.setText("Properties");
    File.add(properties);

    exit.setText("Exit");
    exit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exitActionPerformed(evt);
      }
    });

    File.add(exit);

    mainMenuBar.add(File);

    Rentals.setText("Rentals");
    transaction.setText("Make Another Transaction");
    transaction.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        transactionActionPerformed(evt);
      }
    });

    Rentals.add(transaction);

    checkInVideo.setText("Check-in Video");
    checkInVideo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkInVideoActionPerformed(evt);
      }
    });

    Rentals.add(checkInVideo);

    movieAvailability.setText("Check Movie Availability");
    Rentals.add(movieAvailability);

    reserveMovie.setText("Reserve Movie");
    Rentals.add(reserveMovie);

    assessFees.setText("Charge For Deliquent Rentals");
    assessFees.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        assessFeesActionPerformed(evt);
      }
    });

    Rentals.add(assessFees);

    mainMenuBar.add(Rentals);

    Accounts.setText("Accounts");
    createAccount.setText("Create New Account");
    createAccount.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        createAccountActionPerformed(evt);
      }
    });

    Accounts.add(createAccount);

    addCustomer.setText("Add Customer to Existing Account");
    addCustomer.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addCustomerActionPerformed(evt);
      }
    });

    Accounts.add(addCustomer);

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

    InventoryManagement.setText("Inventory Management");
    rentalVideo.setText("Rental Video");
    rentalVideo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rentalVideoActionPerformed(evt);
      }
    });

    InventoryManagement.add(rentalVideo);

    conceptualVideo.setText("Conceptual Video");
    conceptualVideo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        conceptualVideoActionPerformed(evt);
      }
    });

    InventoryManagement.add(conceptualVideo);

    store.setText("Store");
    store.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        storeActionPerformed(evt);
      }
    });

    InventoryManagement.add(store);

    product.setText("Product");
    product.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        productActionPerformed(evt);
      }
    });

    InventoryManagement.add(product);

    storeProduct.setText("Store Product");
    storeProduct.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        storeProductActionPerformed(evt);
      }
    });

    InventoryManagement.add(storeProduct);

    mainMenuBar.add(InventoryManagement);

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
    jMenuItem25.setText("Help Topics");
    Help.add(jMenuItem25);

    jMenuItem24.setText("About");
    Help.add(jMenuItem24);

    mainMenuBar.add(Help);

    setJMenuBar(mainMenuBar);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void storeProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_storeProductActionPerformed
    GUIDBMStoreProductDisplay dbStoreProduct = new GUIDBMStoreProductDisplay();
    dbStoreProduct.setVisible(true);
  }//GEN-LAST:event_storeProductActionPerformed

  private void productActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productActionPerformed
    GUIDBMProductDisplay dbProduct = new GUIDBMProductDisplay();
    dbProduct.setVisible(true);
  }//GEN-LAST:event_productActionPerformed

  private void storeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_storeActionPerformed
    GUIDBMStoreDisplay dbStore = new GUIDBMStoreDisplay();
    dbStore.setVisible(true);
  }//GEN-LAST:event_storeActionPerformed

  private void conceptualVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conceptualVideoActionPerformed
    GUIDBMConceptualVideoDisplay dbConceptualVideo = new GUIDBMConceptualVideoDisplay();
    dbConceptualVideo.setVisible(true);
  }//GEN-LAST:event_conceptualVideoActionPerformed

  private void rentalVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rentalVideoActionPerformed
    GUIDBMRentalVideoDisplay dbRental = new GUIDBMRentalVideoDisplay();
    dbRental.setVisible(true);
  }//GEN-LAST:event_rentalVideoActionPerformed

  private void assessFeesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_assessFeesActionPerformed
    AssessFullCostFees assessFullCostFees = new AssessFullCostFees();
    try{
      assessFullCostFees.process();
      JOptionPane.showMessageDialog(this,"The full-cost fees have been assessed.", "Full-cost Fees Assessed", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e){
      JOptionPane.showMessageDialog(this, "An error occurred while assessing rental fees", "Error Assessing fees", JOptionPane.ERROR_MESSAGE);
    }
  }//GEN-LAST:event_assessFeesActionPerformed

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

  private void addCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCustomerActionPerformed
    GUIAcctManagement guiAcctManage = new GUIAcctManagement();
    guiAcctManage.setVisible(true);
  }//GEN-LAST:event_addCustomerActionPerformed

  private void createAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createAccountActionPerformed
    GUIAcctManagement guiAcctManage = new GUIAcctManagement();
    guiAcctManage.setVisible(true);
  }//GEN-LAST:event_createAccountActionPerformed
    
    private void balancePaymentTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_balancePaymentTextFieldActionPerformed
      checkBalancePayment();
    }//GEN-LAST:event_balancePaymentTextFieldActionPerformed
                
    private void transactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transactionActionPerformed
      clearTransaction();
    }//GEN-LAST:event_transactionActionPerformed
    
    private void checkInVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkInVideoActionPerformed
      GUIReturnRental guiReturn = new GUIReturnRental();
      guiReturn.setVisible(true);
    }//GEN-LAST:event_checkInVideoActionPerformed
    
    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
      logButtonActionPerformed(evt);
    }//GEN-LAST:event_logoutActionPerformed
    
    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
      int i = JOptionPane.showConfirmDialog(this, "Do you really want to exit?\n This will close the program."
              , "Exit Program", JOptionPane.ERROR_MESSAGE);
      if (i == 0){
        System.exit(0);
      }
    }//GEN-LAST:event_exitActionPerformed
    
    private void clearSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearSearchActionPerformed
      clearTransaction();
    }//GEN-LAST:event_clearSearchActionPerformed
    /** Looks up an existing transaction and displays it to the table */
    private void lookupTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lookupTransactionActionPerformed
      int i = JOptionPane.showConfirmDialog(this, "This will clear the current transaction and current customer.\n " +
              "Are you sure you want to do this?", "Clear Current Transaction and Customer", JOptionPane.ERROR_MESSAGE);
      if (i == 1){
        return;
      }
      String txID = transactionID.getText();
      //checks to make sure the txid is not empty
      if (txID.equals("")){
        return;
      }
      Tx tx = null;
      Customer cust = null;
      try{
        TxController txc = new TxController();
        
        //gets the transaction object of the return
        tx = txc.lookupTx(txID);
        //gets the customer of the return
        cust = tx.getCustomer();
        //sets the account number in the GUI
        getAccountSummary(cust);
        tableReturnsModel = new DefaultTableModel();
        //Sets the new table for returns
        tableReturnsModel.setColumnIdentifiers(columnIdentifiers);
        Vector<Vector> vect = txc.getVector(tx);
        tableReturnsModel.setDataVector(vect, columnIdentifiers);
        tableReturnsModel.fireTableDataChanged();
        transactionTableReturns.setModel(tableReturnsModel);
        transactionID.setText("");
        displayReturns();
        //sends the focus in order to lookup an account
        transactionID.requestFocus();
        
      } catch (Exception e){
        JOptionPane.showMessageDialog(this, e.getMessage(), "Previous Transaction Lookup Error", JOptionPane.PLAIN_MESSAGE);
      }
      
      
    }//GEN-LAST:event_lookupTransactionActionPerformed
    /** Checks to make sure the the sku is valid and the quantity is valid for a return and calls addItem */
    private void returnItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnItemActionPerformed
      int row = transactionTableReturns.getSelectedRow();
      String skuNum = null;
      int quantity = 0;
      //checks to make sure the returns table has a valid row and is not null or negative
      if(row >= 0){
        //Gets the quantity from the existing transaction and parses the object to a string and to an integer
        quantity = (Integer.parseInt(tableReturnsModel.getValueAt(row, columnIdentifiers.indexOf("Quantity")).toString()   )  );
        skuNum = tableReturnsModel.getValueAt(row, columnIdentifiers.indexOf("Sku/Serial #")).toString();
        
        //Checks the quantity available to return and checks to make sure the item is a product (not a rental or membership)
        if (quantity > 0 && skuNum.length() == 12 && !skuNum.equals(null)){
          addItem(skuNum, "-1");
          quantity--;
          //this changes the trasactionline returned to one quantity less
          tableReturnsModel.setValueAt(quantity, row, columnIdentifiers.indexOf("Quantity"));
        }else if (!(skuNum.length() == 12)){
          JOptionPane.showMessageDialog(this, "Only Products can be returned, which have skus of length 12.\n"
                  , "Not Valid Item", JOptionPane.PLAIN_MESSAGE);
          return;
        }else if (quantity <= 0){
          JOptionPane.showMessageDialog(this, "Quantity must be greater than zero to return the product."
                  , "Quantity Error", JOptionPane.PLAIN_MESSAGE);
          return;
        }
      }
    }//GEN-LAST:event_returnItemActionPerformed
    
    private void transactionTableReturnsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_transactionTableReturnsMouseClicked
      transactionID.requestFocus();
    }//GEN-LAST:event_transactionTableReturnsMouseClicked
    
    private void cancelReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelReturnActionPerformed
      displayTotals();
    }//GEN-LAST:event_cancelReturnActionPerformed
    
    private void transactionIDbalancePaymentTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_transactionIDbalancePaymentTextField1FocusLost
      paymentTextField.requestFocus();
    }//GEN-LAST:event_transactionIDbalancePaymentTextField1FocusLost
    
    private void returnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnsActionPerformed
      if(returns.getText().equalsIgnoreCase("Returns")){
        displayReturns();
      }else{
        displayTotals();
      }
      
    }//GEN-LAST:event_returnsActionPerformed
    
    private void skuTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_skuTextFieldFocusGained
      accountMustBeSelected();
    }//GEN-LAST:event_skuTextFieldFocusGained
    
    private void phoneNumTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_phoneNumTextFieldFocusLost
      accountNumTextField.setEditable(true);
    }//GEN-LAST:event_phoneNumTextFieldFocusLost
    
    private void phoneNumTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_phoneNumTextFieldFocusGained
      accountNumTextField.setEditable(false);
      accountNumTextField.setText("");
    }//GEN-LAST:event_phoneNumTextFieldFocusGained
    
    private void firstNameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_firstNameTextFieldFocusGained
      accountNumTextField.setEditable(false);
      accountNumTextField.setText("");
    }//GEN-LAST:event_firstNameTextFieldFocusGained
    
    private void firstNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_firstNameTextFieldFocusLost
      accountNumTextField.setEditable(true);
    }//GEN-LAST:event_firstNameTextFieldFocusLost
    
    private void lastNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lastNameTextFieldFocusLost
      accountNumTextField.setEditable(true);
    }//GEN-LAST:event_lastNameTextFieldFocusLost
    
    private void lastNameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lastNameTextFieldFocusGained
      accountNumTextField.setEditable(false);
      accountNumTextField.setText("");
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
    
    private void accountsListKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_accountsListKeyTyped
      setAccountLookupInfo();
    }//GEN-LAST:event_accountsListKeyTyped
    
    private void createNewAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewAccountActionPerformed
      GUIAcctManagement guiAcct = new GUIAcctManagement();
      guiAcct.setVisible(true);
    }//GEN-LAST:event_createNewAccountActionPerformed
    
    private void accountsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_accountsListMouseClicked
      setAccountLookupInfo();
    }//GEN-LAST:event_accountsListMouseClicked
    
    private void paymentTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentTextFieldActionPerformed
      makePayment.requestFocus();
    }//GEN-LAST:event_paymentTextFieldActionPerformed
    
    private void transactionTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_transactionTableMouseClicked
      skuTextField.requestFocus();
    }//GEN-LAST:event_transactionTableMouseClicked
    /** Checks to make sure the quantity entered is at least 1 */
    private void quantityTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_quantityTextFieldFocusLost
      int quantity = 1;
      try{
        
        quantity = Integer.parseInt(quantityTextField.getText());
        if (quantity < 1){
          JOptionPane.showMessageDialog(this, "The quantity must be at least 1.\n",
                  "Minimum Quantity Not Met", JOptionPane.ERROR_MESSAGE);
          quantityTextField.setText("1");
        }
      } catch (NumberFormatException e){
        JOptionPane.showMessageDialog(this, "You have entered an invalid character.", "Invalid Character Entered", JOptionPane.ERROR_MESSAGE);
        quantityTextField.setText("1");
      }
    }//GEN-LAST:event_quantityTextFieldFocusLost
    
    private void paymentTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_paymentTextFieldFocusLost
      checkPayment();
    }//GEN-LAST:event_paymentTextFieldFocusLost
    
    private void balancePaymentTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_balancePaymentTextFieldFocusLost
      checkBalancePayment();
    }//GEN-LAST:event_balancePaymentTextFieldFocusLost
    /** Looks up a member and checks to see if it should look them up by account # or by phone, firstname, and lastname */
    private void lookupMembersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lookupMembersActionPerformed
      String lName = lastNameTextField.getText();
      String fName = firstNameTextField.getText();
      String phoneNum = phoneNumTextField.getText();
      String accountNum = "";
      if ((accountNumTextField.isEditable())){
        accountNum = accountNumTextField.getText();
      }
      
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
    /** Makes a payment by calling the transaction controller method called makepayment */        
    private void makePaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makePaymentActionPerformed
      
      //does nothing by returning if an account is not selected or if there aren't any transaction lines'
      if(!accountMustBeSelected() || (tableTransactionModel.getRowCount() == 0)){
        return;
      }
      
      try{
        txc.makePayment();
        JOptionPane.showMessageDialog(this, "The transaction has been saved." , "Transaction Completed Successfully", JOptionPane.PLAIN_MESSAGE);
        clearTransaction();
      } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage() , "Error Processing Transaction", JOptionPane.ERROR_MESSAGE);
      }
      
    }//GEN-LAST:event_makePaymentActionPerformed
    
    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
      clearTransaction();
    }//GEN-LAST:event_cancelActionPerformed
    
    private void updateAccountInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateAccountInfoActionPerformed
      GUIAcctManagement guiAcct = new GUIAcctManagement();
      guiAcct.setVisible(true);
    }//GEN-LAST:event_updateAccountInfoActionPerformed
    
    private void getAccountSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getAccountSummaryActionPerformed
      Customer cust = (Customer)accountsList.getSelectedValue();
      getAccountSummary(cust);
    }//GEN-LAST:event_getAccountSummaryActionPerformed
    
    private void removeSelectedItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSelectedItemActionPerformed
      removeItem();
    }//GEN-LAST:event_removeSelectedItemActionPerformed
    /** Checks to make sure the sku is not null and adds the item to the transaction by passing the quantity and sku to the additem method*/        
    private void addItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemActionPerformed
      //checks to make sure the sku is not null or empty
      if (skuTextField.getText().equals("")){
        JOptionPane.showMessageDialog(this, "You must enter a sku/serial to add the transaction Line Item.", "Must Enter Sku/Serial Number", JOptionPane.PLAIN_MESSAGE);
      } else{
        addItem(skuTextField.getText(),quantityTextField.getText());
        displayTotals();
      }
    }//GEN-LAST:event_addItemActionPerformed
    
    private void lookupItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lookupItemActionPerformed
      JOptionPane.showMessageDialog(this, "Currently, this function is not available.", "Lookup Product Item", JOptionPane.PLAIN_MESSAGE);
    }//GEN-LAST:event_lookupItemActionPerformed
    /** Closes the program and eventually will log the user off */        
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
    public static void GUIReturnsTransaction(String args[]) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            
            GUIReturnsTransaction grt = new GUIReturnsTransaction();
            grt.setVisible(true);          
         
        }
      });
    }
    
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JMenu Accounts;
  private javax.swing.JMenuItem DeleteAccount;
  private javax.swing.JMenu File;
  private javax.swing.JMenu Help;
  private javax.swing.JMenu InventoryManagement;
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
  private javax.swing.JMenuItem addCustomer;
  private javax.swing.JButton addItem;
  private javax.swing.JMenuItem assessFees;
  private javax.swing.JLabel balancePaymentLabel;
  private javax.swing.JTextField balancePaymentTextField;
  private javax.swing.JLabel blockWoodLabel;
  private javax.swing.JMenuItem buyMembership;
  private javax.swing.JButton cancel;
  private javax.swing.JButton cancelReturn;
  private javax.swing.JLabel changeDueLabel;
  private javax.swing.JTextField changeDueTextField;
  private javax.swing.JMenuItem checkInVideo;
  private javax.swing.JButton clearSearch;
  private javax.swing.JMenuItem conceptualVideo;
  private javax.swing.JMenuItem createAccount;
  private javax.swing.JButton createNewAccount;
  private javax.swing.JLabel employeeID;
  private javax.swing.JLabel employeeIDLabel;
  private javax.swing.JLabel employeeName;
  private javax.swing.JMenuItem exit;
  private javax.swing.JLabel firstNameLabel;
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
  private javax.swing.JTextField lastNameTextField;
  private javax.swing.JToggleButton logButton;
  private javax.swing.JLabel logLabel;
  private javax.swing.JPanel logPanel;
  private javax.swing.JMenuItem login;
  private javax.swing.JMenuItem logout;
  private javax.swing.JButton lookupItem;
  private javax.swing.JPanel lookupMember;
  private javax.swing.JButton lookupMembers;
  private javax.swing.JButton lookupTransaction;
  private javax.swing.JPanel mainLabelPanel;
  private javax.swing.JMenuBar mainMenuBar;
  private javax.swing.JPanel mainPanel;
  private javax.swing.JScrollPane mainScrollPanel;
  private javax.swing.JButton makePayment;
  private javax.swing.JLabel maxRentals;
  private javax.swing.JLabel maxRentalsLabel;
  private javax.swing.JPanel members;
  private javax.swing.JLabel membershipType;
  private javax.swing.JLabel membershipTypeLabel;
  private javax.swing.JLabel minBalance;
  private javax.swing.JLabel minBalanceDueLabel;
  private javax.swing.JMenuItem movieAvailability;
  private javax.swing.JLabel outstandingBalance;
  private javax.swing.JLabel outstandingBalanceLabel;
  private javax.swing.JLabel paymentLabel;
  private javax.swing.JTextField paymentTextField;
  private javax.swing.JLabel phoneNumLabel;
  private javax.swing.JTextField phoneNumTextField;
  private javax.swing.JMenuItem product;
  private javax.swing.JMenuItem properties;
  private javax.swing.JLabel quantityLabel;
  private javax.swing.JTextField quantityTextField;
  private javax.swing.JButton removeSelectedItem;
  private javax.swing.JMenuItem renewMembership;
  private javax.swing.JMenuItem rentalVideo;
  private javax.swing.JMenuItem reserveMovie;
  private javax.swing.JButton returnItem;
  private javax.swing.JButton returns;
  private javax.swing.JPanel returnsPanel;
  private javax.swing.JLabel skuLabel;
  private javax.swing.JTextField skuTextField;
  private javax.swing.JMenuItem store;
  private javax.swing.JMenuItem storeProduct;
  private javax.swing.JLabel subTotalLabel;
  private javax.swing.JTextField subTotalTextField;
  private javax.swing.JScrollPane tableScrollPanel;
  private javax.swing.JScrollPane tableScrollPanel1;
  private javax.swing.JLabel taxLabel;
  private javax.swing.JTextField taxTextField;
  private javax.swing.JLabel totalLabel;
  private javax.swing.JTextField totalTextField;
  private javax.swing.JPanel totals;
  private javax.swing.JMenuItem transaction;
  private javax.swing.JTextField transactionID;
  private javax.swing.JLabel transactionIDLabel;
  private javax.swing.JPanel transactionPanel;
  private javax.swing.JTable transactionTable;
  private javax.swing.JTable transactionTableReturns;
  private javax.swing.JMenuItem updateAccount;
  private javax.swing.JButton updateAccountInfo;
  // End of variables declaration//GEN-END:variables
    
}
