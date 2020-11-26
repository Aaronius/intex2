package edu.byu.isys413.group1a.intex2.BOs;
/**
 * A Store
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class Store extends BusinessObject {
  private Float payMinFeeAmt = null, taxRate = null;
  private String name = null, address = null, state = null, zipCode = null, phone = null;
  
  /** Creates a new instance of Store */
  public Store() {
  }
  /** Gets minimum payment amount */
  public Float getPayMinFeeAmt() {
    return payMinFeeAmt;
  }
  /** Set minimum payment amount */
  public void setPayMinFeeAmt(Float payMinFeeAmt) {
    this.payMinFeeAmt = payMinFeeAmt;
  }
  /** Gets name */
  public String getName() {
    return name;
  }
  /** Sets name */
  public void setName(String name) {
    this.name = name;
  }
  /** Gets address */
  public String getAddress() {
    return address;
  }
  /** Sets address */
  public void setAddress(String address) {
    this.address = address;
  }
  /** Gets state */
  public String getState() {
    return state;
  }
  /** Sets states */
  public void setState(String state) {
    this.state = state;
  }
  /** Gets zip code */
  public String getZipCode() {
    return zipCode;
  }
  /** Sets zip code */
  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }
  /** Gets phone */
  public String getPhone() {
    return phone;
  }
  /** Sets phone */
  public void setPhone(String phone) {
    this.phone = phone;
  }
  /** Gets tax rate */
  public Float getTaxRate() {
    return taxRate;
  }
  /** Sets tax rate */
  public void setTaxRate(Float taxRate) {
    this.taxRate = taxRate;
  }
}//class
