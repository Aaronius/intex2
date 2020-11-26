package edu.byu.isys413.group1a.intex2.Controllers;

import edu.byu.isys413.group1a.intex2.BOs.Account;
import edu.byu.isys413.group1a.intex2.BOs.Customer;
import edu.byu.isys413.group1a.intex2.BOs.Fee;
import edu.byu.isys413.group1a.intex2.BOs.JournalEntry;
import edu.byu.isys413.group1a.intex2.BOs.JournalEntryContainer;
import edu.byu.isys413.group1a.intex2.BOs.Membership;
import edu.byu.isys413.group1a.intex2.BOs.Payment;
import edu.byu.isys413.group1a.intex2.BOs.Rental;
import edu.byu.isys413.group1a.intex2.BOs.RevenueSource;
import edu.byu.isys413.group1a.intex2.BOs.Store;
import edu.byu.isys413.group1a.intex2.BOs.StoreProduct;
import edu.byu.isys413.group1a.intex2.BOs.Tx;
import edu.byu.isys413.group1a.intex2.BOs.TxLine;
import edu.byu.isys413.group1a.intex2.DAOs.AccountDAO;
import edu.byu.isys413.group1a.intex2.DAOs.FeeDAO;
import edu.byu.isys413.group1a.intex2.DAOs.JournalEntryDAO;
import edu.byu.isys413.group1a.intex2.DAOs.MembershipDAO;
import edu.byu.isys413.group1a.intex2.DAOs.PaymentDAO;
import edu.byu.isys413.group1a.intex2.DAOs.RentalDAO;
import edu.byu.isys413.group1a.intex2.DAOs.RentalVideoDAO;
import edu.byu.isys413.group1a.intex2.DAOs.StoreDAO;
import edu.byu.isys413.group1a.intex2.DAOs.StoreProductDAO;
import edu.byu.isys413.group1a.intex2.DAOs.TxDAO;
import edu.byu.isys413.group1a.intex2.DAOs.TxLineDAO;
import edu.byu.isys413.group1a.intex2.BOs.TxDisplay;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;

/**
 * Controls a transaction (and possible returns) and its related associations.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class TxController {
  
  private final String STOREID = "0000010942ed61aeb33d203e001000ac00e900c6";
  private Customer curCust = null;
  private Tx curTx = null;
  private TxDisplay txd= null;
  private final double TAXPERCENT = .0625;
  private double minFeeAmt = 0;
  private int actual = 0;
  private int allowed = 0;
  private Vector tableVector = new Vector();
  private JournalEntryContainer jeContainer = new JournalEntryContainer();
  
  
  /**
   * Creates instance of class
   */
  public TxController() {
  }
  
  /**
   * This is a temporary method to sustain our sweet legacy software
   */
  public void saveTx(Tx tx, float balPayment) throws Exception{
    tx.setBalPayment(balPayment);
    saveTx(tx);
  }
  
  /**
   * Save Transaction without connection passed in
   */
  public void saveTx(Tx tx) throws Exception{
    Connection conn = ConnectionPool.getInstance().get();
    saveTx(tx, conn);
    ConnectionPool.getInstance().release(conn);
  }
  
  /**
   * Save Transaction
   */
  public void saveTx(Tx tx, Connection conn) throws Exception{
    try {
      //curTx.setStore(StoreDAO.getInstance().read(STOREID));
      savePayment(tx.getPayment(), conn);
      adjustAccount(tx.getCustomer().getAccount(),tx.getBalPayment(), conn);
      adjustTxLineAssociations(tx, conn);
      addTxLevelJEs(tx);
      jeContainer.saveJournalEntries(conn);
      TxDAO.getInstance().save(tx, conn);
      
      // if tx is from fee, cash was paid and cash JE should be made
      
      conn.commit();
    }catch (Exception e) {
      try{
        conn.rollback();
      }catch (SQLException e2) {
        e.printStackTrace();
        throw new DataException("Could not roll back the database transaction!", e2);
      }
      e.printStackTrace();
      throw new DataException("An error occurred while saving the transaction.", e);
    }
  }
  
  /**
   * Adjust associations of specific txLine type
   */
  private void adjustTxLineAssociations(Tx tx, Connection conn) throws Exception{
    List<TxLine> txLines = tx.getTxLines();//?
    
    // For each transaction line
    for (Iterator<TxLine> iter = txLines.iterator(); iter.hasNext();){
      TxLine txLine = iter.next();
      TxLineDAO.getInstance().save(txLine,conn);
      
      // Set up JE for txLine
      JournalEntry journalEntry = JournalEntryDAO.getInstance().create();
      
      switch (txLine.getSerialNum().length()) {
        case 2: // Membership
          Membership mem = (Membership)txLine.getRevenueSource();
          mem.setAccount(tx.getCustomer().getAccount());
          MembershipDAO.getInstance().save(mem, conn);
          Account curAcct = tx.getCustomer().getAccount();
          curAcct.setMembership(mem);
          jeContainer.addMembershipJE(tx, txLine);
          
          break;
          
        case 4: // Fee
          
          FeeDAO.getInstance().save((Fee)txLine.getRevenueSource(),conn);
          
          //This occurs because a fee does not have a payment
          Account acct = tx.getCustomer().getAccount();
          acct.setBalance(acct.getBalance() + tx.getTotal());
          AccountDAO.getInstance().save(acct, conn);
          jeContainer.addFeeJE(tx, txLine);
          
          break;
          
        case 8: // Rental
          
          adjustRentalInfo((Rental)txLine.getRevenueSource(), conn);
          jeContainer.addRentalJE(tx, txLine);
          
          break;
          
        case 12: // Product
          // Note: This subtracts a negative if its a returned product - all good.
          adjustQOH(tx.getStore(),txLine.getRevenueSource(), txLine.getQuantity(), conn);
          jeContainer.addProductJE(tx, txLine);
          
          break;
      }
      
    }
  }
  
  /**
   * Adjusts the Quantity on Hand when a transaction is saved
   */
  private void adjustQOH(Store store, RevenueSource revenueSource, int quantity, Connection conn) throws Exception{
    StoreProduct sp = StoreProductDAO.getInstance().readByForeign(store.getId(), revenueSource.getId(), conn);
    sp.setQtyOnHand(sp.getQtyOnHand()-quantity);
    StoreProductDAO.getInstance().save(sp, conn);
  }
  
  /**
   * Adjusts the Rental Info when a transaction is saved
   */
  private void adjustRentalInfo(Rental rental, Connection conn) throws Exception{
    RentalDAO.getInstance().save(rental, conn);
    rental.getRentalVideo().setStatus("out");
    RentalVideoDAO.getInstance().save(rental.getRentalVideo(), conn);
  }
  
  /**
   * Adjusts the Account Info when a transaction is saved
   */
  private void adjustAccount(Account acct, float balPayment, Connection conn) throws Exception{
    acct.setBalance(acct.getBalance()-balPayment);
    AccountDAO.getInstance().save(acct, conn);
  }
  
  /**
   * Saves a Payment record
   */
  private void savePayment(Payment payment, Connection conn) throws Exception{
    PaymentDAO.getInstance().save(payment, conn);
  }
  
  /**
   * Saves tx related JEs
   */
  private void addTxLevelJEs(Tx tx) throws Exception{
    if (tx.getPayment().getAmtTendered() > 0){ // If there's a payment, we're going to debit cash
      
      jeContainer.addCashDebitJE(tx);
      
      // If some previous balance is paid off, we must credit A/R for that amount
      if (tx.getBalPayment() > 0){
        jeContainer.addBalancePaymentJE(tx);
      }
      
    } else { // If there's no payment, it must be fee - add A/R debit JE
      
      jeContainer.addAccountsReceivableDebitJE(tx);
      
    }
    
    jeContainer.addTaxPayableJE(tx);
  }
  
  /**
   * Initializes a new transaction for a customer
   */
  public void newTx(Customer cust) throws Exception{
    try{
      curTx = TxDAO.getInstance().create();
      curTx.setCustomer(cust);
      this.curCust = cust;
      txd = new TxDisplay();
    } catch(Exception e){
      throw new DataException("An error occurred while creating a new transaction");
    }
    try {
      minFeeAmt = Double.parseDouble(StoreDAO.getInstance().read(STOREID).getPayMinFeeAmt().toString());
    }catch(Exception e){
      throw new DataException("The store database could not be accessed");
    }
  }
  
  /**
   * Returns the GUI information for a transaction
   */
  public TxDisplay getTxDisplay(){
    return txd;
  }
  
  /**
   * Sets the GUI information for a transaction
   */
  public void setTxDisplay(TxDisplay txd){
    this.txd = txd;
  }
  
  /**
   * Returns the Vector representation of the table for a previous
   * transaction of a return
   */
  public Vector getVector(Tx tx){
    List<TxLine> txLines = tx.getTxLines();
    Vector txVector = new Vector();
    
    for (int i = 0; i < txLines.size(); i++){
      TxLine txLine = txLines.get(i);
      String serialNum = txLine.getSerialNum();
      
      Vector blankRow = new Vector();
      blankRow.add(0, txLine.getQuantity());
      blankRow.add(1, txLine.getRevenueSource().getDescription());
      blankRow.add(2, txLine.getSerialNum());
      
      //checks to make sure the sku # is 8 digits, which means the item is a rental not a product or something else
      if(serialNum.length() == 8){
        blankRow.add(3, txLine.getDueDate());
      }else{
        blankRow.add(3, "");
      }
      DecimalFormat fmt = new DecimalFormat("0.00");
      blankRow.add(4, fmt.format(txLine.getSubTotal()));
      txVector.add(blankRow);//table.insertRow(0, (Vector)blankRow);
    }
    return txVector;
  }
  
  /**
   * Generates the Vector representation of a table for a transaction
   */
  public void createTableVector(){
    tableVector.clear();
    List<TxLine> lines = curTx.getTxLines();
    for(int i = 0; i < lines.size(); i++){
      TxLine txLine = lines.get(i);
      Vector blankRow = new Vector();
      blankRow.add(0, txLine.getQuantity());
      blankRow.add(1, txLine.getRevenueSource().getDescription());
      blankRow.add(2, txLine.getSerialNum());
      
      //checks to make sure the sku # is 8 digits, which means the item is a rental not a product or something else
      if(txLine.getSerialNum().length() == 8){
        blankRow.add(3, txLine.getDueDate());
      }else{
        blankRow.add(3, "");
      }
      DecimalFormat fmt = new DecimalFormat("0.00");
      blankRow.add(4, fmt.format(txLine.getSubTotal()));
      tableVector.add(blankRow);//table.insertRow(0, (Vector)blankRow);
    }
  }
  
  /**
   * Adds a transaction line to the table and returns the Vector
   * representation of the table
   */
  public Vector addTransactionLine(String serialNum, String quantity) throws Exception{//Vector should be stored
    
    // Make sure serial number was entered
    if (serialNum.equals("")){
      throw new DataException("You must enter a sku/serial to add the transaction line item.");
    }
    
    // If membership, rental, or product, add txline
    if (serialNum.length() == 2 || serialNum.length() == 8 || serialNum.length() == 12){
      TxLine txLine = curTx.newTxLine(Integer.parseInt(quantity), serialNum, STOREID);
      calculateTaxSubTotals();
      calculateTotal();
    } else{
      throw new DataException("You must enter a rental serial of 8 digits\n or a product sku of 12 digits to add the transaction line item");
    }
    //Create Table Vector
    createTableVector();
    return tableVector;
  }
  
  /**
   * Removes a transaction line from the table and returns the Vector
   * representation of the table
   */
  public Vector removeTransactionLine(int num){
    curTx.removeTxLine(num);
    createTableVector();
    return tableVector;
  }
  
  /**
   * Formats the variables to have two decimal places
   **/
  private double formatDisplay(double value){
    DecimalFormat fmt = new DecimalFormat("0.00");
    return Double.parseDouble(fmt.format(value));
  }
  
  /**
   * Calculates the final total and sets value in txDisplay
   */
  public void calculateTotal(){//Pass table object
    double total = 0.00;
    
    //calculates the total and prints it
    total = txd.getTax() + txd.getSubTotal() + txd.getBalancePayment();
    total = formatDisplay(total);
    txd.setTotal(total);
    txd.setPayment(total);
    txd.setChange(0);
  }
  
  /**
   * Calculates the total paid and sets value in txDisplay
   */
  public void calculateTotalPaid(){//Pass table object
    double total = 0.00;
    
    //calculates the total and prints it
    total = txd.getTax() + txd.getSubTotal() + txd.getBalancePayment();
    txd.setTotal(formatDisplay(total));
    txd.setChange(txd.getPayment()-txd.getTotal());
  }
  
  /**
   * Calculates the subTotal and sets value in txDisplay
   */
  public void calculateTaxSubTotals(){
    double subTotal = 0, tax = 0, price = 0;
    String priceString = " ", taxString;
    //checks to make sure there are rows
    
    if (tableVector.size() >= 0){
      //loops through the rows and gets the prices to calculate the subtotal
      for (int counter = 0; counter < tableVector.size(); counter++){
        price = Double.parseDouble(((Vector)tableVector.get(counter)).get(4).toString());
        subTotal += price;
      }
      tax = subTotal * TAXPERCENT;
      DecimalFormat fmt = new DecimalFormat("0.00");
      taxString = fmt.format(tax);
      tax = Double.parseDouble(taxString);
    }
    txd.setSubTotal(subTotal);
    txd.setTax(tax);
  }
  
  /**
   * Calculates the minimum balance due and sets value in txDisplay
   */
  public void calculateMinBalance(){
    double accountBalance = 0.00, minBalanceDue = 0.00;
    
    //Calculates a minimum amount due so the outstanding balance is never greater than $5.00
    txd.setBalance(curCust.getAccount().getBalance());
    if (txd.getBalance() > minFeeAmt){
      //make them pay X =>  accountBalance - X = minFeeAmt      usually minFeeAmt = $5.00
      //minBalanceDue = accountBalance - minFeeAmt;
      
      txd.setMinAmtDue((txd.getBalance() - minFeeAmt));
      
    } else{
      txd.setMinAmtDue(0);
    }
    txd.setPayment(txd.getBalance());
    txd.setBalancePayment(txd.getBalance());
  }
  
  /**
   * Creates a payment record
   */
  public void makePayment() throws Exception{
    try{
      
      Payment payment = PaymentDAO.getInstance().create();
      payment.setAmount((float)txd.getTotal());
      payment.setAmtTendered((float)txd.getPayment());
      payment.setChange((float)txd.getChange());
      
      curTx.setPayment(payment);
      curTx.setTax((float)txd.getTax());
      curTx.setTotal((float)txd.getTotal());
      curTx.setStore(StoreDAO.getInstance().read(STOREID));
      try {
        saveTx(curTx, (float)txd.getBalancePayment());
      } catch (Exception e) {
        e.printStackTrace();
        throw new DataException("There was an error while processing the transaction");
      }
      
    } catch (Exception e){
      e.printStackTrace();
      throw new DataException(e.getMessage());
    }
  }
  
  /**
   * Looks up a transaction from a transaction number
   */
  public Tx lookupTx(String txNum) throws Exception{
    curTx = TxDAO.getInstance().read(txNum);
    return curTx;
  }
  
  /**
   * Sets current customer
   */
  public Customer getCust(){
    return curCust;
  }
  
  /**
   * Sets current customer
   */
  public void setCust(Customer cust){
    this.curCust = cust;
  }
}
