package edu.byu.isys413.group1a.intex2.BOs;

import java.util.Calendar;

/**
 * A Membership
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class Membership extends RevenueSource {
  private float amount = 0;
  private String description = null;
  private Calendar startDate = null;
  private Calendar expDate = null;
  private Calendar cancelDate = null;
  private Account account = null;
  private MembershipType membershipType = null;
  
  /** Creates a new instance of Membership */
  public Membership() {
  }
  /** Get amount */
  public float getAmount(){
    return amount;
  }
  /** Set amount */
  public void setAmount(float amount){
    this.amount = amount;
  }
  /** Get description */
  public String getDescription(){
    return description;
  }
  /** Set description */
  public void setDescription(String description){
    this.description = description;
  }
  /** Get start date */
  public Calendar getStartDate() {
    return startDate;
  }
  /** Set start date */
  public void setStartDate(Calendar startDate) {
    this.startDate = startDate;
  }
  /** Get expiration date */
  public Calendar getExpDate() {
    return expDate;
  }
  /** Set expiration date */
  public void setExpDate(Calendar expDate) {
    this.expDate = expDate;
  }
  /** Get cancel date */
  public Calendar getCancelDate() {
    return cancelDate;
  }
  /** Set cancel date */
  public void setCancelDate(Calendar cancelDate) {
    this.cancelDate = cancelDate;
  }
  /** Get account */
  public Account getAccount() {
    return account;
  }
  /** Set account */
  public void setAccount(Account account) {
    this.account = account;
  }
  /** Get membership type */
  public MembershipType getMembershipType() {
    return membershipType;
  }
  /** Set membership type */
  public void setMembershipType(MembershipType membershipType) {
    this.membershipType = membershipType;
  }
  
}//class
