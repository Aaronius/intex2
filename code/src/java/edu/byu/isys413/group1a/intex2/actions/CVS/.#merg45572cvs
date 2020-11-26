package edu.byu.isys413.group1a.intex2.actions;

import edu.byu.isys413.group1a.intex2.DAOs.CustomerDAO;
import edu.byu.isys413.group1a.intex2.BOs.Customer;
import edu.byu.isys413.group1a.intex2.web.Action;
import edu.byu.isys413.group1a.intex2.web.WebException;
import javax.servlet.http.*;
import java.util.List;

/**
 * Gets a list of all customers and sets its as an attribute. Also passes on the
 * rental video ID that is passed in as a parameter.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class GetCustomersToReserveRental implements Action {
  
  /**
   * Responds to an action call from the Controller.java file. 
   * Gets a list of all customers and sets its as an attribute. Also passes on
   * the rental video ID that is passed in as a parameter.
   *
   * @return selectAccountToReserveRental.jsp
   */
  public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String rentalVideoId = request.getParameter("rentalVideoId");
    
    if (rentalVideoId == null){
      throw new WebException("You cannot access this page directly.");
    }
    
    request.setAttribute("rentalVideoId", rentalVideoId);
    
    // Get the list of customers and place it in the request
    List<Customer> customers = CustomerDAO.getInstance().getAll();
    request.setAttribute("customers", customers);
    
    return "selectAccountToReserveRental.jsp";
  }//process method
  
}//class
