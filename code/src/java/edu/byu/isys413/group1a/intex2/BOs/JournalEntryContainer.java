package edu.byu.isys413.group1a.intex2.BOs;

import edu.byu.isys413.group1a.intex2.DAOs.JournalEntryDAO;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import java.sql.Connection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds Journal Entries
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class JournalEntryContainer extends BusinessObject {
  private List<JournalEntry> journalEntries = new LinkedList<JournalEntry>();
  
  /** Saves Journal Entries */
  public void saveJournalEntries(Connection conn) throws Exception{
    
    // debugger for interal use - prints out quality JE info for a transaction
    boolean debugger = false;
    float debuggerCAmount = 0;
    float debuggerDAmount = 0;
    
    if (debugger){
      System.out.println("/////////////////////////////////////////////////////////");
      System.out.println("//  Journal Entry Debugger");
      System.out.println("/////////////////////////////////////////////////////////");
    }
    
    for (Iterator<JournalEntry> iter = journalEntries.iterator(); iter.hasNext();){
      JournalEntry journalEntry = iter.next();
      JournalEntryDAO.getInstance().save(journalEntry, conn);
      
      if (debugger) {
        if (journalEntry.getDorc().equals("c")) debuggerCAmount += journalEntry.getAmount();
        if (journalEntry.getDorc().equals("d")) debuggerDAmount += journalEntry.getAmount();
        System.out.println("//  " + journalEntry.getGlaccount() + ": " + journalEntry.getDorc() + ": " + journalEntry.getAmount());
      }
      
    }
    
    if (debugger) {
      System.out.println("/////////////////////////////////////////////////////////");
      if (debuggerCAmount == debuggerDAmount){
        System.out.println("//  Journal Entries balanced. Woopty woo!");
      } else {
        System.out.println("//  Journal Entries not balanced. Jump off a cliff.");
      }
      System.out.println("/////////////////////////////////////////////////////////");
    }
  }
  
  /////////////////////////////////
  /////  Tx-level JEs Begin
  
  /** Adds cash debit JE for cash-based Tx's (non-fee related) */
  public void addCashDebitJE(Tx tx) throws DataException {
    if (tx.getPayment().getAmount() != 0){
      JournalEntry je = JournalEntryDAO.getInstance().create();
      je.setGlaccount("Cash");
      je.setAmount(tx.getPayment().getAmount()); // subtotal (w/o tax) + balance payment
      je.setTx(tx);
      je.setDorc("d");
      je.setDate(tx.getDate());
      
      if (!this.journalEntries.contains(je)) {
        this.journalEntries.add(je);
      }
    }
  }
  
  /** Adds accounts receivable debit JE for non-cash Tx's (fee related) */
  public void addAccountsReceivableDebitJE(Tx tx) throws DataException {
    if (tx.getPayment().getAmount() != 0){
      JournalEntry je = JournalEntryDAO.getInstance().create();
      je.setGlaccount("Accounts Receivable");
      je.setAmount(tx.getPayment().getAmount());
      je.setTx(tx);
      je.setDorc("d");
      je.setDate(tx.getDate());
      
      if (!this.journalEntries.contains(je)) {
        this.journalEntries.add(je);
      }
    }
  }
  
  /** Adds a balancePayment JE */
  public void addBalancePaymentJE(Tx tx) throws DataException {
    if (tx.getBalPayment() != 0){
      JournalEntry je = JournalEntryDAO.getInstance().create();
      je.setTx(tx);
      je.setGlaccount("Accounts Receivable");
      je.setAmount(tx.getBalPayment());
      je.setDorc("c");
      je.setDate(tx.getDate());
      
      if (!this.journalEntries.contains(je)) {
        this.journalEntries.add(je);
      }
    }
  }
  
  /** Adds a balancePayment JE */
  public void addTaxPayableJE(Tx tx) throws DataException {
    if (tx.getTax() != 0){
      JournalEntry je = JournalEntryDAO.getInstance().create();
      je.setTx(tx);
      je.setGlaccount("Tax Payable");
      
      if (tx.getTax() < 0){
        je.setDorc("d");
        je.setAmount(-tx.getTax());
      } else {
        je.setDorc("c");
        je.setAmount(tx.getTax());
      }
      
      je.setDate(tx.getDate());
      
      if (!this.journalEntries.contains(je)) {
        this.journalEntries.add(je);
      }
    }
  }
  
  /////  Tx-level JEs End
  /////////////////////////////////
  
  /////////////////////////////////
  /////  TxLine-level JEs Begin
  
  /** Adds membership JE */
  public void addMembershipJE(Tx tx, TxLine txLine) throws DataException {
    if (txLine.getSubTotal() != 0){
      JournalEntry je = JournalEntryDAO.getInstance().create();
      je.setGlaccount("Membership Revenue");
      je.setAmount(txLine.getSubTotal());
      je.setTx(tx);
      je.setDorc("c");
      je.setDate(tx.getDate());
      
      if (!this.journalEntries.contains(je)) {
        this.journalEntries.add(je);
      }
    }
  }
  
  /** Adds fee JE */
  public void addFeeJE(Tx tx, TxLine txLine) throws DataException {
    if (txLine.getSubTotal() != 0){
      JournalEntry je = JournalEntryDAO.getInstance().create();
      je.setGlaccount("Fee Revenue");
      je.setAmount(txLine.getSubTotal());
      je.setTx(tx);
      je.setDorc("c");
      je.setDate(tx.getDate());
      
      if (!this.journalEntries.contains(je)) {
        this.journalEntries.add(je);
      }
    }
  }
  
  /** Adds rental JE */
  public void addRentalJE(Tx tx, TxLine txLine) throws DataException {
    if (txLine.getSubTotal() != 0){
      JournalEntry je = JournalEntryDAO.getInstance().create();
      je.setGlaccount("Rental Revenue");
      je.setAmount(txLine.getSubTotal());
      je.setTx(tx);
      je.setDorc("c");
      je.setDate(tx.getDate());
      
      if (!this.journalEntries.contains(je)) {
        this.journalEntries.add(je);
      }
    }
  }
  
  /** Adds product JE */
  public void addProductJE(Tx tx, TxLine txLine) throws DataException {
    if (txLine.getSubTotal() != 0){
      
      JournalEntry je = JournalEntryDAO.getInstance().create();
      
      if (txLine.getQuantity() < 0){ // If returned product
        
        je.setGlaccount("Sales Returns");
        je.setAmount(-txLine.getSubTotal());
        je.setDorc("d");
        
      } else { // Otherwise, it's not a returned product
        
        je.setGlaccount("Product Revenue");
        je.setAmount(txLine.getSubTotal());
        je.setDorc("c");
        
      }
      
      je.setTx(tx);
      je.setDate(tx.getDate());
      
      if (!this.journalEntries.contains(je)) {
        this.journalEntries.add(je);
      }
    }
  }
  
  /////  TxLine-level JEs End
  ////////////////////////////////
  
}//class