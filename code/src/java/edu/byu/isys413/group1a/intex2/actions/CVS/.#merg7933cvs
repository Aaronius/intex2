package edu.byu.isys413.group1a.intex2.actions;

import edu.byu.isys413.group1a.intex2.BOs.Account;
import edu.byu.isys413.group1a.intex2.BOs.RentalVideo;
import edu.byu.isys413.group1a.intex2.DAOs.AccountDAO;
import edu.byu.isys413.group1a.intex2.DAOs.RentalVideoDAO;
import edu.byu.isys413.group1a.intex2.web.Action;
import java.util.Calendar;
import javax.servlet.http.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Reserves a rental for a given account for one hour.  Also sends a text message
 * to the store manager for pulling a rental video off the store shelves.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class ReserveRental implements Action {
  PasswordAuthentication authentication;
  
  /**
   * Responds to an action call from the Controller.java file.
   * Reserves a rental for a given account for one hour.  Also sends a text message
   * to the store manager for pulling a rental video off the store shelves.
   *
   * @return reservationConfirmation.jsp
   */
  public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Account account = AccountDAO.getInstance().read(request.getParameter("accountNumber"));
    
    //Handle problem of an account not being found
    if (account == null){
      request.setAttribute("accountNotFound", true);
      request.setAttribute("rentalVideoId", request.getParameter("rentalVideoId"));
      return "selectAccountToReserveRental.jsp";
    }
    
    
    RentalVideo rentalVideo = RentalVideoDAO.getInstance().read(request.getParameter("rentalVideoId"));
    Calendar currentTime = Calendar.getInstance();
    
    //Gather the number of rentals reserved within a 1 hour timeframe
    if (RentalVideoDAO.getInstance().getNumberReservedByAccount(account.getId()) > 2){
      request.setAttribute("tooManyRentals", "true");
      return "reservationConfirmation.jsp";
    }else{
      request.setAttribute("tooManyRentals", "false");
      request.setAttribute("rentalVideo", rentalVideo);
      rentalVideo.setReserveTime(currentTime);
      rentalVideo.setReserveAcct(account);
      sendMessageToManager(rentalVideo, request);
      RentalVideoDAO.getInstance().save(rentalVideo);
    }
    
    return "reservationConfirmation.jsp";
  }//process method
  
  /** 
   * Sends a text message to the store manager for pulling a rental
   * video off the store shelves.
   */
  private void sendMessageToManager(RentalVideo rentalVideo, HttpServletRequest request) throws Exception{
    String host = "nm.byu.edu";
    String from = "reservation@blockwood.com";
    String to = "8018304948@tmomail.net";
    final String username = "jm395";
    final String password = "passisys1";
    int port = 465;
    
    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    
    // Get system properties
    Properties props = System.getProperties();
    
    props.put("mail.smtps.host", host);
    props.put("mail.smtps.auth", "true");
    props.put("mail.smtps.port", "465");
    
    props.setProperty( "mail.smtps.socketFactory.class", SSL_FACTORY);
    
    Session session = Session.getInstance(props, null);
    
    // Define message
    MimeMessage message = new MimeMessage(session);
    
    // Set the from address
    message.setFrom(new InternetAddress(from));
    
    // Set the to address
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    
    // Set the subject
    message.setSubject("Reservation");
    
    // Set the content
    message.setText("Rental Video Title: " + rentalVideo.getVcrtcv().getCv().getTitle() + "\n" +
            "Rental Video Number: " + rentalVideo.getSerialNum() + "\n" +
            "Account Number: " + request.getParameter("accountNumber"));
    
    //Useful for fixing errors
    //session.setDebug(true);
    
    //Class for sending mail and defining connection
    Transport transport = session.getTransport("smtps");
    transport.connect(host, username, password);
    transport.sendMessage(message, message.getAllRecipients());
    transport.close();
    
  }
  
}
