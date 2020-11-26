package edu.byu.isys413.group1a.intex2.BOs;

import java.util.Calendar;

/**
 * A journal entry object for accounting
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class JournalEntry extends BusinessObject {
  private Tx tx = null;
  private String glaccount = null;
  private String dorc = null;
  private float amount = 0;
  private Calendar date = null;
  
  /** Creates a new instance of JournalEntry */
  public JournalEntry() {
  }
  /** Gets Transaction */
  public Tx getTx() {
    return tx;
  }
  /** Sets Transaction */
  public void setTx(Tx tx) {
    this.tx = tx;
  }
  /** Gets General Ledger Account */
  public String getGlaccount() {
    return glaccount;
  }
  /** Sets General Ledger Account */
  public void setGlaccount(String glaccount) {
    this.glaccount = glaccount;
  }
  /** Gets Debit or Credit */
  public String getDorc() {
    return dorc;
  }
  /** Sets Debit or Credit */
  public void setDorc(String dorc) {
    this.dorc = dorc;
  }
  /** Gets amount */
  public float getAmount() {
    return amount;
  }
  /** Sets amount */
  public void setAmount(float amount) {
    this.amount = amount;
  }
  /** Gets date */
  public Calendar getDate() {
    return date;
  }
  /** Sets date */
  public void setDate(Calendar date) {
    this.date = date;
  }
  
}//class
