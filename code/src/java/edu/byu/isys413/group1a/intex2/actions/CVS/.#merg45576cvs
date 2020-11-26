package edu.byu.isys413.group1a.intex2.actions;

import edu.byu.isys413.group1a.intex2.DAOs.*;
import edu.byu.isys413.group1a.intex2.BOs.*;
import edu.byu.isys413.group1a.intex2.web.Action;
import edu.byu.isys413.group1a.intex2.web.WebException;
import javax.servlet.http.*;
import java.util.List;

/**
 * Gets a list of conceptual videos that match a title search criterion.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class GetSearchVideos implements Action {
  
  /**
   * Responds to an action call from the Controller.java file.
   * Gets a list of conceptual videos that match a title search criterion.
   *
   * @return showSearchVideos.jsp
   */
  public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String searchTitle = request.getParameter("searchTitle");
    
    if (searchTitle == null) {
      throw new WebException("You cannot access this page directly or without entering search parameters.");
    }
    
    //Create a list of all available videos meeting search criteria
    List<ConceptualVideo> conceptualVideos = ConceptualVideoDAO.getInstance().searchByTitle(searchTitle);
    
    //Send results of search to JSP
    if (conceptualVideos.size()==0) {
      request.setAttribute("noResults", "true");
      return "searchAvailableVideos.jsp";
    } else {
      request.setAttribute("conceptualVideos", conceptualVideos);
      return "showSearchVideos.jsp";
    }   
    
  }//process method
  
}
