package edu.byu.isys413.group1a.intex2.BOs;

/**
 * A conceptualFee with sku
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class ConceptualFee extends BusinessObject {
  private String sku = null;
  private String description = null;
  private float amount = 0;
  
  /** Creates a new instance of Customer */
  public ConceptualFee() {
  }
  /** Gets the fee sku*/
  public String getSku() {
    return sku;
  }
  /** Sets the fee sku */
  public void setSku(String sku) {
    this.sku = sku;
  }
  /** Gets the fee description */
  public String getDescription() {
    return description;
  }
  /** Sets the fee description */
  public void setDescription(String description) {
    this.description = description;
  }
  /** Gets the fee amount */
  public float getAmount() {
    return amount;
  }
  /** Sets the fee amount */
  public void setAmount(float amount) {
    this.amount = amount;
  }
  
  
}//class
