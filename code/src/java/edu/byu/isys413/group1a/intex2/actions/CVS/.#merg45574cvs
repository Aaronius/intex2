package edu.byu.isys413.group1a.intex2.actions;

import edu.byu.isys413.group1a.intex2.DAOs.*;
import edu.byu.isys413.group1a.intex2.BOs.*;
import edu.byu.isys413.group1a.intex2.web.Action;
import edu.byu.isys413.group1a.intex2.web.WebException;
import java.util.ArrayList;
import javax.servlet.http.*;
import java.util.List;

/**
 * Gets a list of rentals that are currently checked out by a given account.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class GetRentalsOutForAccount implements Action {
  
  /**
   * Responds to an action call from the Controller.java file.
   * Gets a list of rentals that are currently checked out by a given account.
   *
   * @return showRentalsOutForAccount.jsp
   */
  public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String accountNumber = request.getParameter("accountNumber");
    
    if (accountNumber == null) {
      throw new WebException("You cannot access this page directly or without entering search parameters.");
    }
    
    if (AccountDAO.getInstance().read(accountNumber) == null) {
      request.setAttribute("noResults", "true");
      return "selectAccountToViewRentalsOut.jsp";
    }
    
    // Get rentals that have not been returned
    List<Rental> rentalList = RentalDAO.getInstance().readRentals();
    
    // Using the rentals list, get the associated txLines
    List<TxLine> txLineList = TxLineDAO.getInstance().getTxLinesFromRentals(rentalList);
    
    List<RentalVideo> rentalVideoList = new ArrayList();
    
    for(TxLine txLine: txLineList){
      if (txLine.getTx().getCustomer().getAccount().getAccountNum().equals(accountNumber)){
        rentalVideoList.add(((Rental)txLine.getRevenueSource()).getRentalVideo());
      }
    }
    
    request.setAttribute("rentalVideoList", rentalVideoList);
    
    return "showRentalsOutForAccount.jsp";
  }//process method
  
}
