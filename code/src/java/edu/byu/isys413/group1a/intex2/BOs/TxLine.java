package edu.byu.isys413.group1a.intex2.BOs;

import java.util.Calendar;

/**
 * A Transaction Line
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class TxLine extends BusinessObject {
  private Tx tx = null;
  private RevenueSource revenueSource= null;
  private float subTotal = 0;
  private int quantity = 0;
  private String serialNum = null;
  
  /** Creates a new instance of TxLine */
  public TxLine() {
  }
  /** Gets transaction */
  public Tx getTx() {
    return tx;
  }
  /** Sets transaction */
  public void setTx(Tx tx) {
    this.tx = tx;
  }
  /** Sets revenue source */
  public RevenueSource getRevenueSource() {
    return revenueSource;
  }
  /** Sets revenue source */
  public void setRevenueSource(RevenueSource revenueSource) {
    this.revenueSource = revenueSource;
    recalcSubTotal();
  }
  /** Gets sub total */
  public float getSubTotal() {
    return subTotal;
  }
  /** Sets sub total */
  public void setSubTotal(float subTotal) {
    this.subTotal = subTotal;
  }
  /** Gets quantity */
  public int getQuantity() {
    return quantity;
  }
  /** Sets quantity */
  public void setQuantity(int quantity) {
    this.quantity = quantity;
    recalcSubTotal();
  }
  /** Gets serial number */
  public String getSerialNum(){
    return serialNum;
  }
  /** Sets serial number */
  public void setSerialNum(String serialNum){
    this.serialNum = serialNum;
  }
  /** Gets due date */
  public String getDueDate(){
    Calendar cal = ((Rental)revenueSource).getDueDate();
    String[] weekdays = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
    return weekdays[cal.get(cal.DAY_OF_WEEK)-1];
  }
  /** Recalculates sub total */
  private void recalcSubTotal(){
    if (quantity != 0 && revenueSource != null){
      subTotal = quantity * revenueSource.getAmount();
    } else {
      subTotal = 0;
    }
  }
  
}//class