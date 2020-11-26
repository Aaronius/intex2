package edu.byu.isys413.group1a.intex2.Controllers;

import edu.byu.isys413.group1a.intex2.BOs.Account;
import edu.byu.isys413.group1a.intex2.BOs.Customer;
import edu.byu.isys413.group1a.intex2.BOs.Membership;
import edu.byu.isys413.group1a.intex2.BOs.Store;
import edu.byu.isys413.group1a.intex2.DAOs.AccountDAO;
import edu.byu.isys413.group1a.intex2.DAOs.CustomerDAO;
import edu.byu.isys413.group1a.intex2.DAOs.MembershipDAO;
import edu.byu.isys413.group1a.intex2.DAOs.MembershipTypeDAO;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * This controller manages membership creation for both new customers on new
 * accounts and new customers on existing accounts.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class AcctManagementController {
  final String FREEMEMBERSHIPTYPEID = "0000010942ed68c2002bb37b001000ac00e900c6";
  
  /**
   * Creates a new instance of class
   */
  public AcctManagementController() {
  }
  
  /**
   * Creates, builds, and returns a customer object from given information
   */
  private Customer addCustomer(String firstName, String lastName, String address,
          String city, String state, String zipCode, String phone) throws DataException{
    
    Customer cust = CustomerDAO.getInstance().create();
    cust.setFirstName(firstName);
    cust.setLastName(lastName);
    cust.setAddress(address);
    cust.setCity(city);
    cust.setState(state);
    cust.setZipCode(zipCode);
    cust.setPhone(phone);
    
    return cust;
  }
  
  /**
   * Creates and builds account, customer, and membership objects and saves them.
   * This is for a new customer with a new account.
   */
  public void addCustomerToNewAccount(String firstName, String lastName,
          String address, String city, String state, String zipCode, String phone, Store store,
          String ccName, String ccNum, int ccExpMonth, int ccExpYear) throws DataException, SQLException {
    
    Account acct = AccountDAO.getInstance().create();
    acct.setAccountNum(AccountDAO.getInstance().getAutoIncrement());
    acct.setStore(store);
    acct.setCcName(ccName);
    acct.setCcNum(ccNum);
    acct.setCcExpMonth(ccExpMonth);
    acct.setCcExpYear(ccExpYear);
    acct.setBalance(0);
    
    Customer cust = addCustomer(firstName, lastName, address, city, state, zipCode, phone);
    
    acct.setOwner(cust);
    acct.addCustomer(cust);
    cust.setAccount(acct);
    
    Membership memb = MembershipDAO.getInstance().create();
    memb.setMembershipType(MembershipTypeDAO.getInstance().read(FREEMEMBERSHIPTYPEID));
    memb.setAccount(acct);
    memb.setStartDate(Calendar.getInstance());
    
    acct.setMembership(memb);
    
    AccountDAO.getInstance().save(acct);
    CustomerDAO.getInstance().save(cust);
    MembershipDAO.getInstance().save(memb);
    
  }
  
  /**
   * Creates and builds customer object, associates it with the given account,
   * and saves it both objects. This is for a new customer with an existing account.
   */
  public void addCustomerWithAccount(String firstName, String lastName, String address,
          String city, String state, String zipCode, String phone, String account) throws DataException{
    
    Account acct = AccountDAO.getInstance().read(account);
    Customer cust = addCustomer(firstName, lastName, address, city, state, zipCode, phone);
    
    acct.addCustomer(cust);
    cust.setAccount(acct);
    
    CustomerDAO.getInstance().save(cust);
  }
  
  /**
   * Searches database and returns a list of members based on name and phone criteria.
   */
  public DefaultListModel lookupMembersByNameOrPhone(String lName, String fName, String phoneNum) throws Exception, NumberFormatException{
    
    phoneNum = formatPhoneChars(phoneNum);
    List<Customer> custList = CustomerDAO.getInstance().readByX(fName, lName, phoneNum);
    DefaultListModel membersList = new DefaultListModel();
    
    for (Iterator<Customer> iter = custList.iterator(); iter.hasNext();){
      Customer cust = iter.next();
      membersList.addElement(cust);
    }
    
    return membersList;
  }
  
  /**
   * Searches database and returns a list of members based on account number.
   */
  public DefaultListModel lookupMembersByAccountNum(String accountNum) throws Exception{
    List<Customer> custList = AccountDAO.getInstance().read(accountNum).getCustomers();
    DefaultListModel membersList = new DefaultListModel();
    
    for (Iterator<Customer> iter = custList.iterator(); iter.hasNext();){
      Customer cust = iter.next();
      //here we need to check and see if the customer is the owner and if it is add owner to GUI
      membersList.addElement(cust);
    }
    
    return membersList;
  }
  
  /**
   * Formats phone characters
   */
  private String formatPhoneChars(String phoneNumber) throws NumberFormatException{
    
    // Don't try formatting if it's empty
    if (phoneNumber.equals("")){
      return phoneNumber;
    }
    
    phoneNumber = phoneNumber.replaceAll("[-]", "");
    phoneNumber = phoneNumber.replaceAll("\\(", "");
    phoneNumber = phoneNumber.replaceAll("\\)", "");
    Double.parseDouble(phoneNumber);
    if (phoneNumber.length() != 10){
      throw new NumberFormatException();
    }
    
    String newPhoneNumber = "";
    
    for (int i = 0; phoneNumber.length() > i; i++){
      newPhoneNumber += phoneNumber.charAt(i);
      if (i == 2 || i == 5){
        newPhoneNumber += "-";
      }
    }
    return newPhoneNumber;
  }
}
