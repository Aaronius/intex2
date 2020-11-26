package edu.byu.isys413.group1a.intex2.Controllers;

import edu.byu.isys413.group1a.intex2.BOs.Account;
import edu.byu.isys413.group1a.intex2.BOs.Rental;
import edu.byu.isys413.group1a.intex2.BOs.TxLine;
import edu.byu.isys413.group1a.intex2.DAOs.RentalDAO;
import edu.byu.isys413.group1a.intex2.DAOs.TxLineDAO;
import java.util.List;

/**
 * This controller manages the batch assessment of full-cost fees.  A full-cost
 * fee is applied when a member does not return a rental for over a month.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class AssessFullCostFees {
  
  /**
   * Creates a new instance of class
   */
  public AssessFullCostFees() {
  }
  
  /**
   * Finds rentals that are more than a month overdue and charges the respective account.
   */
  public void process() throws Exception{
    List<Rental> rentalList = RentalDAO.getInstance().readMonthOverdueRentals();
    List<TxLine> txLineList = TxLineDAO.getInstance().getTxLinesFromRentals(rentalList);
    for(TxLine txLine: txLineList){
      Account account = txLine.getTx().getCustomer().getAccount();
      float overduePrice = ((Rental)txLine.getRevenueSource()).getRentalVideo().getVcrtcv().getVcrt().getOverduePrice();
      // Charge account's credit card the overdue price
    }
  }
  
}
