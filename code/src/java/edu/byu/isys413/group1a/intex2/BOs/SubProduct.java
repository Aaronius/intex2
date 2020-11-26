package edu.byu.isys413.group1a.intex2.BOs;

/**
 * A Sub-Product
 *
 * @author Group 1A, isys@aaronhardy.com
 */
// We have to repeat RevenueSource's abstract methods because
// multi-inheritance is not allowed in Java
public abstract class SubProduct extends BusinessObject{
  
  /** Creates a new instance of RevenueSource */
  public SubProduct() {
  }
  /** Gets amount */
  public abstract float getAmount();
  /** Sets amount */
  public abstract void setAmount(float amount);
  /** Gets description */
  public abstract String getDescription();
  /** Sets description */
  public abstract void setDescription(String description);
  
}//class
