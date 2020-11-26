package edu.byu.isys413.group1a.intex2.BOs;

import java.util.Calendar;

/**
 * A rental Account
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class RentalVideo extends BusinessObject {
  
  private Store store = null;
  private VCRTCV vcrtcv = null;
  private String serialNum = null;
  private String status = null;
  private Calendar reserveTime = null;
  private Account reserveAcct = null;
  
  /** Creates a new instance of Account */
  public RentalVideo() {
  }
  /** Returns string representation of date */
  public String toString() {
    return "<RentalVideo " + getId();
  }
  /** Gets store */
  public Store getStore() {
    return store;
  }
  /** Sets store */
  public void setStore(Store store) {
    this.store = store;
  }
  /** Gets VCRTCV */
  public VCRTCV getVcrtcv() {
    return vcrtcv;
  }
  /** Sets VCRTCV */
  public void setVcrtcv(VCRTCV vcrtcv) {
    this.vcrtcv = vcrtcv;
  }
  /** Gets serial number */
  public String getSerialNum() {
    return serialNum;
  }
  /** Sets serial number */
  public void setSerialNum(String serialNum) {
    this.serialNum = serialNum;
  }
  /** Gets status */
  public String getStatus() {
    return status;
  }
  /** Sets status */
  public void setStatus(String status) {
    this.status = status;
  }
  /** Gets reserve time */
  public Calendar getReserveTime() {
    return reserveTime;
  }
  /** Sets reserver time */
  public void setReserveTime(Calendar reserveTime) {
    this.reserveTime = reserveTime;
  }
  /** Gets reserve time */
  public Account getReserveAcct() {
    return reserveAcct;
  }
  /** Sets reserve time */
  public void setReserveAcct(Account reserveAcct) {
    this.reserveAcct = reserveAcct;
  }
  
}//class
