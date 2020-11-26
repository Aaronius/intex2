package edu.byu.isys413.group1a.intex2.Controllers;

import edu.byu.isys413.group1a.intex2.BOs.Membership;
import edu.byu.isys413.group1a.intex2.BOs.Payment;
import edu.byu.isys413.group1a.intex2.BOs.Tx;
import edu.byu.isys413.group1a.intex2.BOs.TxLine;
import edu.byu.isys413.group1a.intex2.DAOs.MembershipDAO;
import edu.byu.isys413.group1a.intex2.DAOs.PaymentDAO;
import edu.byu.isys413.group1a.intex2.DAOs.TxDAO;
import edu.byu.isys413.group1a.intex2.DAOs.TxLineDAO;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Renews expired memberships for one month from the expiration date.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class RenewalBatch {
  
  Calendar today = Calendar.getInstance();
  Calendar newExpDate = Calendar.getInstance();
  
  /**
   * Creates instance of class
   */
  public RenewalBatch() {
  }
  
  /**
   * Renews expired memberships for one month from the expiration date.
   */
  public void RenewMemberships() throws Exception{
    //AccountDAO acct = AccountDAO
    Connection conn = ConnectionPool.getInstance().get();
    DecimalFormat formatter = new DecimalFormat("0.00");
    final double TAXPERCENT = .0625;
    long timeToday = today.getTimeInMillis();
    List <Membership> membershipList = MembershipDAO.getInstance().readExpDate(timeToday);
    for(Membership memberRecord: membershipList){
      String memAccount = memberRecord.getAccount().getId();
      float membershipRenewal = memberRecord.getMembershipType().getPrice();
      float grandTotal = Float.parseFloat(formatter.format(membershipRenewal * (1 + (float)TAXPERCENT)));
      float tax = Float.parseFloat(formatter.format(membershipRenewal * (float)TAXPERCENT));
      
      //Change new Expiration date to 1 month after current Exp Date
      newExpDate = memberRecord.getExpDate();
      newExpDate.add(newExpDate.MONTH, 2);
      
      // create payment for fee transaction
      Payment payment = PaymentDAO.getInstance().create();
      payment.setAmount(grandTotal);
      payment.setAmtTendered(payment.getAmount());
      payment.setChange(0);
      
      // create fee transaction
      Tx tx = TxDAO.getInstance().create();
      tx.setCustomer(memberRecord.getAccount().getOwner());
      tx.setStore(memberRecord.getAccount().getStore());
      tx.setPayment(payment);
      tx.setDate(today);
      tx.setTax(tax);
      tx.setTotal(grandTotal);
      
      // create transaction line for
      TxLine txLine = TxLineDAO.getInstance().create();
      txLine.setQuantity(1);
      txLine.setRevenueSource(memberRecord);
      txLine.setSubTotal(membershipRenewal);
      txLine.setSerialNum("001");
      //txLine.setSerialNum(MembershipTypeDAO.getInstance().readById(memberRecord.getMembershipType().getId()));
      txLine.setTx(tx);
      
      // add transaction line to transaction
      tx.addTxLine(txLine);
      
      TxController txController = new TxController();
      txController.saveTx(tx, conn);
      ///////////////////////GET CREDIT CARD STUFFS AND CHARGE IT////////////////////////
      String ccName = memberRecord.getAccount().getCcName();
      int ccExpMonth = memberRecord.getAccount().getCcExpMonth();
      int ccExpYear = memberRecord.getAccount().getCcExpYear();
      String ccNum = memberRecord.getAccount().getCcNum();
      System.out.println("\n"+ ccName + "'s credit card has been charged $" + grandTotal + ".\nThe credit card number is: \t"
              + ccNum + "\nWith expiration date: \t\t" + ccExpMonth + "/"+ ccExpYear);
      memberRecord.setExpDate(newExpDate);
      MembershipDAO.getInstance().save(memberRecord);
      Calendar newDay = memberRecord.getExpDate();
      System.out.println("Your membership has been renewed. \nYour membership expires on " + newDay.get(Calendar.MONTH) +
              "/" + newDay.get(Calendar.DAY_OF_MONTH) + "/" + newDay.get(Calendar.YEAR));
      
    }
    
    conn.commit();
    
  }
}
