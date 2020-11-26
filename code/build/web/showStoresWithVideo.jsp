<%@page import="edu.byu.isys413.group1a.intex2.BOs.*"%>
<%@page import="edu.byu.isys413.group1a.intex2.web.*"%>
<%@page import="java.util.*"%>
<jsp:include page="/header.jsp">
  <jsp:param name="title" value="Stores with selected video" />
</jsp:include>

<%
 /**
 * This page shows stores with the selected title from which the users can choose.
 */
%>

<%
   List<RentalVideo> rentalVideos = (List<RentalVideo>)request.getAttribute("rentalVideos");
   
   // Ensure page is not being accessed directly
   if (rentalVideos == null) {
     throw new WebException("You cannot access this page directly.");
   }
%>

<div id="customer">
  Please select the video you would like to reserve:<p>
  
    <% 
      while(rentalVideos.size()>0){ // Until all videos are dealt with
        String tempStoreId = rentalVideos.get(0).getStore().getId(); // get store id for checking against
        List<RentalVideo> tempRVList = new ArrayList(); // set up empty temp array for checking against
        out.println(rentalVideos.get(0).getStore().getName() + "<br>"); // print store name
        out.println(rentalVideos.get(0).getStore().getAddress() + "<br>");
        out.println(rentalVideos.get(0).getStore().getName() + ", " + rentalVideos.get(0).getStore().getState() + " " + rentalVideos.get(0).getStore().getZipCode());
        out.println("<ul>");
        for (int i = 0; i < rentalVideos.size(); i++) { // for each video in list
          if(tempStoreId.equals(rentalVideos.get(i).getStore().getId())){ // if the video item pertains to the store we are on...
            if(checkForUniqueness(rentalVideos.get(i),tempRVList)){ // if this VCRTCV has not been printed already...
    %>
    <li>
      <a href="<%=request.getContextPath()%>/edu.byu.isys413.group1a.intex2.actions.GetCustomersToReserveRental.action?rentalVideoId=<%=rentalVideos.get(i).getId()%>">
        <%=rentalVideos.get(i).getVcrtcv().getCv().getTitle() + "-" + rentalVideos.get(i).getVcrtcv().getVcrt().getVideoCategory().getCategory()%>
      </a>
    </li>
    <%
              } // if
              tempRVList.add(rentalVideos.get(i)); // add video to temp array for checking against
              rentalVideos.remove(i); // remove video from list -- it's printed and done
              i--; // cancel out the increment if we just pulled a video out of the list
            } // if
          } // for
        out.println("</ul>");
        } // while
    %>
  </ul>
  </form>
</div>

<%!
 /** This function checks the current rental video against rental videos
  *  previously printed onto the screen for the store.  If its VCRTCV has
  *  already been printed, there is no need to print it again and it return false.
  ********************************************************************************/
  boolean checkForUniqueness(RentalVideo rv, List<RentalVideo> rvList){
    for(int i=0; i<rvList.size(); i++){
      if(rvList.get(i).getVcrtcv().getId().equals(rv.getVcrtcv().getId())){
        return false;
      }
    }
    return true;
  }
%>

<jsp:include page="/footer.jsp"/>