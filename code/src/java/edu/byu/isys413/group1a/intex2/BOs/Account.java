package edu.byu.isys413.group1a.intex2.BOs;

import java.util.List;
import java.util.LinkedList;

/**
 * A rental Account
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class Account extends BusinessObject {
  
  private String accountNum = null;
  private Customer owner = null;
  private Store store = null;
  private String ccName = null;
  private String ccNum = null;
  private int ccExpMonth = 0;
  private int ccExpYear = 0;
  private float balance = 0;
  private Membership membership = null;
  
  private List<Customer> customers = new LinkedList<Customer>();
  
  /** Creates a new instance of Account */
  public Account() {
  }//account
  
  /** Returns the ids of the accounts with the number of customers */
  public String toString() {
    return "<Account " + getId() + " with " + customers.size() + " customers>";
  }
  /** Gets the account number */
  public String getAccountNum() {
    return accountNum;
  }
  /** Sets the account number */
  public void setAccountNum(String accountNum) {
    this.accountNum = accountNum;
  }
  /** Gets the owner which is a customer object */
  public Customer getOwner() {
    return owner;
  }
  /** Sets the owner which is a customer object */
  public void setOwner(Customer owner) {
    this.owner = owner;
  }
  /** Gets the store */
  public Store getStore() {
    return store;
  }
  /** Sets the store */
  public void setStore(Store store) {
    this.store = store;
  }
  /** Gets the name of the person on the credit card */
  public String getCcName() {
    return ccName;
  }
  /** Sets the name of the person on the credit card */
  public void setCcName(String ccName) {
    this.ccName = ccName;
  }
  /** Gets the credit card number */
  public String getCcNum() {
    return ccNum;
  }
  /** Sets the credit card number */
  public void setCcNum(String ccNum) {
    this.ccNum = ccNum;
  }
  /** Gets the expiration month on the credit card */
  public int getCcExpMonth() {
    return ccExpMonth;
  }
  /** Sets the expiration month on the credit card */
  public void setCcExpMonth(int ccExpMonth) {
    this.ccExpMonth = ccExpMonth;
  }
  /** Gets the expiration year on the credit card */
  public int getCcExpYear() {
    return ccExpYear;
  }
  /** Sets the expiration year on the credit card */
  public void setCcExpYear(int ccExpYear) {
    this.ccExpYear = ccExpYear;
  }
  /** Gets the account balance */
  public float getBalance() {
    return balance;
  }
  /** Sets the account balance */
  public void setBalance(float balance) {
    this.balance = balance;
  }
  /** Gets the list of customers that belong to the account */
  public List<Customer> getCustomers() {
    return customers;
  }
  /** Adds a customer to the list of customers that belong to the account */
  public void addCustomer(Customer customer) {
    if (!this.customers.contains(customer)) {
      this.customers.add(customer);
    }
    customer.setAccount(this);
  }
  /** Removes a customer to the list of customers that belong to the account */
  public void removeCustomer(Customer customer) {
    this.customers.remove(customer);
    customer.setAccount(null);
  }
  
  /** Gets the membership associated with the account */
  public Membership getMembership() {
    return membership;
  }
  
  /** Sets the membership associated with the account */
  public void setMembership(Membership membership) {
    this.membership = membership;
  }
  
}//class
