package edu.byu.isys413.group1a.intex2.BOs;

/**
 * A payment
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class Payment extends BusinessObject {
  private float amount = 0;
  private float amtTendered = 0;
  private float change = 0;
  
  /** Creates a new instance of Payment */
  public Payment() {
  }
  /** Gets amount */
  public float getAmount() {
    return amount;
  }
  /** Sets amount */
  public void setAmount(float amount) {
    this.amount = amount;
  }
  /** Gets Amount Tendered */
  public float getAmtTendered() {
    return amtTendered;
  }
  /** Sets Amoutn Tendered */
  public void setAmtTendered(float amtTendered) {
    this.amtTendered = amtTendered;
  }
  /** Gets change */
  public float getChange() {
    return change;
  }
  /** Sets Change */
  public void setChange(float change) {
    this.change = change;
  }
  
}//class
