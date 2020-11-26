package edu.byu.isys413.group1a.intex2.BOs;

/**
 * A Product
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class Product extends RevenueSource {
  private float amount = 0;
  private String description = null;
  private String sku = null;
  private String type = null;
  private SubProduct subProduct = null;
  
  /** Creates a new instance of Product */
  public Product() {
  }
  
  /** Gets amount */
  public float getAmount(){
    return amount;
  }
  /** Sets amount */
  public void setAmount(float amount){
    this.amount = amount;
  }
  /** Gets description */
  public String getDescription() {
    return description;
  }
  /** Sets description */
  public void setDescription(String description) {
    this.description = description;
  }
  /** Gets Sku */
  public String getSku() {
    return sku;
  }
  /** Sets Sku */
  public void setSku(String sku) {
    this.sku = sku;
  }
  /** Gets type */
  public String getType() {
    return type;
  }
  /** Sets type */
  public void setType(String type) {
    this.type = type;
  }
  /** Gets sub-product */
  public SubProduct getSubProduct() {
    return subProduct;
  }
  /** Sets sub-product */
  public void setSubProduct(SubProduct subProduct) {
    this.subProduct = subProduct;
  }
  
}//class
