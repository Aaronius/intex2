package edu.byu.isys413.group1a.intex2.BOs;

/**
 * A fee object
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class Fee extends RevenueSource {
  private ConceptualFee conceptualFee = null;
  private Rental rental = null;
  private String description = null;
  private float amount = 0;
  private int quantity = 0;
  
  /** Creates a new instance of Customer */
  public Fee() {
  }
  /** Gets the conceptual fee */
  public ConceptualFee getConceptualFee() {
    return conceptualFee;
  }
  /** Sets the conceptual fee */
  public void setConceptualFee(ConceptualFee conceptualFee) {
    this.conceptualFee = conceptualFee;
    setDescription(conceptualFee.getDescription());
  }
  /** Gets the rental object */
  public Rental getRental() {
    return rental;
  }
  /** Sets the rental object */
  public void setRental(Rental rental) {
    this.rental = rental;
  }
  /** Gets the fee description */
  public String getDescription(){
    return description;
  }
  /** Sets the fee description */
  public void setDescription(String description){
    this.description = description;
  }
  /** Gets the fee amount */
  public float getAmount(){
    return amount;
  }
  /** Sets the fee amount */
  public void setAmount(float amount){
    this.amount = amount;
  }
  /** Gets the fee quantity */
  public int getQuantity() {
    return quantity;
  }
  /** Sets the fee quantity */
  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
  
}//class
