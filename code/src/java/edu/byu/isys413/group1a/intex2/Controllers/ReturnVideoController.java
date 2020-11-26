package edu.byu.isys413.group1a.intex2.Controllers;

import edu.byu.isys413.group1a.intex2.BOs.ConceptualFee;
import edu.byu.isys413.group1a.intex2.BOs.Fee;
import edu.byu.isys413.group1a.intex2.BOs.Payment;
import edu.byu.isys413.group1a.intex2.BOs.Rental;
import edu.byu.isys413.group1a.intex2.BOs.RentalVideo;
import edu.byu.isys413.group1a.intex2.BOs.Tx;
import edu.byu.isys413.group1a.intex2.BOs.TxLine;
import edu.byu.isys413.group1a.intex2.DAOs.ConceptualFeeDAO;
import edu.byu.isys413.group1a.intex2.DAOs.FeeDAO;
import edu.byu.isys413.group1a.intex2.DAOs.PaymentDAO;
import edu.byu.isys413.group1a.intex2.DAOs.RentalDAO;
import edu.byu.isys413.group1a.intex2.DAOs.RentalVideoDAO;
import edu.byu.isys413.group1a.intex2.DAOs.TxDAO;
import edu.byu.isys413.group1a.intex2.DAOs.TxLineDAO;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Checks in a rental video and applies a late fee if necessary.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class ReturnVideoController {
  
  /**
   * Creates instance of class
   */
  public ReturnVideoController() {
  }
  
  /**
   * Checks in a rental video and applies a late fee if necessary.
   */
  public void returnVideo(String serialNum) throws DataException{
    final double TAXPERCENT = .0625;
    final String CONCEPTUALFEESKU = "1234";
    final int MILLISPERDAY = 86400000;
    Connection conn = ConnectionPool.getInstance().get();
    ConceptualFee conceptualFee = ConceptualFeeDAO.getInstance().read(CONCEPTUALFEESKU);
    Calendar today = Calendar.getInstance();
    RentalVideo rentalVideo = RentalVideoDAO.getInstance().read(serialNum);
    
    try{
      Rental rental = RentalDAO.getInstance().readByRentalVideoId(rentalVideo.getId());
      TxLine origTxLine = TxLineDAO.getInstance().readByRsid(rental.getId());
      
      // set rental in date
      rental.setInDate(today);
      RentalDAO.getInstance().save(rental, conn);
      
      // set rental video status to "in""
      rentalVideo.setStatus("in");
      RentalVideoDAO.getInstance().save(rentalVideo,conn);
      
      if (rental.getDueDate().before(today)){ // if rental overdue
        int daysLate = (int)((today.getTimeInMillis() - rental.getDueDate().getTimeInMillis()) / MILLISPERDAY);
        DecimalFormat formatter = new DecimalFormat("0.00");
        float feeTotal = Float.parseFloat(formatter.format(conceptualFee.getAmount() * daysLate));
        float grandTotal = Float.parseFloat(formatter.format(feeTotal * (1 + (float)TAXPERCENT)));
        float tax = Float.parseFloat(formatter.format(feeTotal * (float)TAXPERCENT));
        
        // create fee
        Fee fee = FeeDAO.getInstance().create();
        fee.setConceptualFee(conceptualFee); // also sets description
        fee.setAmount(feeTotal);
        fee.setQuantity(daysLate);
        fee.setRental(rental);
        //System.out.println(fee.getConceptualFee().getSku());
        
        // create payment for fee transaction
        Payment payment = PaymentDAO.getInstance().create();
        payment.setAmount(grandTotal);
        payment.setAmtTendered(0);
        payment.setChange(0);
        
        // create fee transaction
        Tx tx = TxDAO.getInstance().create();
        tx.setCustomer(origTxLine.getTx().getCustomer());
        tx.setStore(origTxLine.getTx().getStore());
        tx.setPayment(payment);
        tx.setDate(today);
        tx.setTax(tax);
        tx.setTotal(grandTotal);
        
        // create transaction line for fee transaction
        TxLine txLine = TxLineDAO.getInstance().create();
        txLine.setQuantity(1);
        txLine.setRevenueSource(fee);
        txLine.setSubTotal(feeTotal);
        txLine.setSerialNum(fee.getConceptualFee().getSku());
        txLine.setTx(tx);
        
        // add transaction line to transaction
        tx.addTxLine(txLine);
        
        TxController txController = new TxController();
        txController.saveTx(tx, conn);
      }
      
      conn.commit();
      
    }catch (Exception e) {
      try{
        conn.rollback();
      }catch (SQLException e2) {
        e.printStackTrace();
        throw new DataException("Could not roll back the database transaction!", e2);
      }
      e.printStackTrace();
      throw new DataException("An error occurred while saving the transaction.", e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }
  
}
