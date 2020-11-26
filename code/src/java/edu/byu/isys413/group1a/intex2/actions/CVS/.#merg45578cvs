package edu.byu.isys413.group1a.intex2.actions;

import edu.byu.isys413.group1a.intex2.DAOs.*;
import edu.byu.isys413.group1a.intex2.BOs.*;
import edu.byu.isys413.group1a.intex2.web.Action;
import edu.byu.isys413.group1a.intex2.web.WebException;
import java.util.ArrayList;
import javax.servlet.http.*;
import java.util.List;

/**
 * Gets a list of available rental videos matching a conceptual video.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class GetStoresWithVideo implements Action {
  
  /**
   * Responds to an action call from the Controller.java file.
   * Gets a list of available rental videos matching a conceptual video.
   *
   * @return showStoresWithVideo.jsp
   */
  public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String conceptualVideoId = request.getParameter("conceptualVideoId");
    
    if (conceptualVideoId == null) {
      throw new WebException("You cannot access this page directly.");
    }
    
    List<VCRTCV> vcrtcvList = VCRTCVDAO.getInstance().searchByConceptualVideoId(conceptualVideoId);
    List<RentalVideo> masterRentalVideoList = new ArrayList();
    
    //Create a list of all available videos in all stores
    for (VCRTCV vcrtcv: vcrtcvList){
      List<RentalVideo> rentalVideoList = RentalVideoDAO.getInstance().readAvailableVideosByVCRTCVId(vcrtcv.getId());
      masterRentalVideoList.addAll(rentalVideoList);
    }
    
    //Pass list to JSP
    request.setAttribute("rentalVideos", masterRentalVideoList);
    
    return "showStoresWithVideo.jsp";
  }//process method
  
}
