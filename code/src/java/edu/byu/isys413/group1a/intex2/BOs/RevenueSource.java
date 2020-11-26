/**
 * A Revenue Source abstract class
 *
 * @author Group 1A, isys@aaronhardy.com
 */

package edu.byu.isys413.group1a.intex2.BOs;

public abstract class RevenueSource extends BusinessObject {
  
  /** Creates a new instance of RevenueSource */
  public RevenueSource() {
  }
  /** Gets amount */
  public abstract float getAmount();
  /** Sets amount */
  public abstract void setAmount(float amount);
  /** Gets description */
  public abstract String getDescription();
  /** Gets description */
  public abstract void setDescription(String description);
  
}//class
