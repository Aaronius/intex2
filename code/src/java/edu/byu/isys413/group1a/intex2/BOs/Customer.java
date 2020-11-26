package edu.byu.isys413.group1a.intex2.BOs;

import edu.byu.isys413.group1a.intex2.DAOs.CustomerDAO;
import edu.byu.isys413.group1a.intex2.Misc.DataException;

/**
 * A customer in our application.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class Customer extends BusinessObject {
  
  private String firstName = null;
  private String lastName = null;
  private String address = null;
  private String city = null;
  private String state = null;
  private String zipCode = null;
  private String phone = null;
  private Account account = null;
  
  /** Creates a new instance of Customer */
  public Customer() {
  }
  /** Gets the account number of this Customer */
  public Account getAccount() {
    return account;
  }
  
  /** Sets the account for this customer.  Do not user this method externally.
   *  Instead call account.addCustomer(cust) */
  public void setAccount(Account acct) {
    this.account = acct;
  }
  /** Gets the first name of this Customer */
  public String getFirstName() {
    return firstName;
  }
  /** Sets the first name of this Customer */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  /** Gets the last name of this Customer */
  public String getLastName() {
    return lastName;
  }
  /** Sets the last name of this Customer */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  /** Gets the Customer's address  */
  public String getAddress() {
    return address;
  }
  /** Sets the Customer's address */
  public void setAddress(String address) {
    this.address = address;
  }
  /** Gets the Customer's city */
  public String getCity() {
    return city;
  }
  /** Sets the Customer's city */
  public void setCity(String city) {
    this.city = city;
  }
  /** Gets the  Customer's state */
  public String getState() {
    return state;
  }
  /** Sets the Customer's state */
  public void setState(String state) {
    this.state = state;
  }
  /** Gets the Customer's zipcode*/
  public String getZipCode() {
    return zipCode;
  }
  /** Sets the Customer's zipcode*/
  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }
  /** Gets the Customer's phone number*/
  public String getPhone() {
    return phone;
  }
  /** Sets the Customer's phone number*/
  public void setPhone(String phone) {
    this.phone = phone;
  }
  /** Returns a string of the customers lastname, a comma, and the first name */
  public String toString() {
    return getLastName() + ", " + getFirstName();
  }
  /** Saves this customer to the Database */
  public void save() throws DataException {
    CustomerDAO.getInstance().save(this);
  }
  
}//class
