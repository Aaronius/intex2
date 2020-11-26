package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.Account;
import edu.byu.isys413.group1a.intex2.BOs.Customer;
import edu.byu.isys413.group1a.intex2.BOs.TxLine;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.*;

/**
 * A singleton object that CRUD's Account objects.
 *
 * @author Conan C. Albrecht modified Group 1A, isys@aaronhardy.com
 */
public class AccountDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static AccountDAO instance = null;
  
  /** Creates a new instance of AccountDAO */
  private AccountDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized AccountDAO getInstance() {
    if (instance == null) {
      instance = new AccountDAO();
    }
    return instance;
  }//getInstance
  
  
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Account in the database */
  public Account create() throws DataException {
    Account acct = new Account();
    acct.setObjectAlreadyInDB(false);
    acct.setId(GUID.generate());
    Cache.getInstance().put(acct.getId(), acct);
    return acct;
  }//create
  
  
  
  
  
  ///////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Account from the database */
  public Account read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (Account)Cache.getInstance().get(id);
    }
    Connection conn = ConnectionPool.getInstance().get();
    try {
      return read(id, conn);
    }catch (Exception e) {
      throw new DataException("An error occurred while reading the business object information.", e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }
  
  /** Internal method to read an existing Account from the database */
  Account read(String id, Connection conn) throws Exception{
    if (Cache.getInstance().containsKey(id)) {
      return (Account)Cache.getInstance().get(id);
    }
    
    PreparedStatement stmt;
    if (id.length() == 7){
      stmt = conn.prepareStatement("SELECT * FROM account WHERE accountnum=?");
    } else {
      stmt = conn.prepareStatement("SELECT * FROM account WHERE id=?");
    }
    
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs, conn);
      }//if
      throw new DataException("Account with id '" + id + "' not found.");
    }catch(Exception e){
      e.printStackTrace();
      return null;
    } finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Account object from a record */
  Account readRecord(ResultSet rs, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (Account)Cache.getInstance().get(rs.getString("id"));
    }
    Account acct = new Account();
    acct.setObjectAlreadyInDB(true);
    acct.setId(rs.getString("id"));
    Cache.getInstance().put(acct.getId(), acct);
    acct.setAccountNum(rs.getInt("accountnum")+"");
    acct.setOwner(CustomerDAO.getInstance().read(rs.getString("ownerid")));
    acct.setStore(StoreDAO.getInstance().read(rs.getString("storeid")));
    acct.setCcName(rs.getString("ccname"));
    acct.setCcNum(rs.getString("ccnum"));
    acct.setCcExpMonth(rs.getInt("ccexpmonth"));
    acct.setCcExpYear(rs.getInt("ccexpyear"));
    acct.setBalance(rs.getFloat("balance"));
    acct.setMembership(MembershipDAO.getInstance().readByAcctId(acct.getId()));
    
    // set up the customer list
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customer WHERE accountid=?");
    try {
      stmt.setString(1, acct.getId());
      ResultSet rs2 = stmt.executeQuery();
      while (rs2.next()) {
        Customer cust = CustomerDAO.getInstance().readRecord(rs2);
        cust.setAccount(acct);
        acct.addCustomer(cust);
        if (cust.getId().equals(rs.getString("ownerid"))) {  // if the owning customer, set it here
          acct.setOwner(cust);
        }
      }
      
    } catch (Exception e){
      throw new DataException("Could not read teh account information from the database!", e);
    } finally{
      stmt.close();
    }
    
    return acct;
  }//readRecord
  
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Account in the database */
  public void save(Account acct) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(acct, conn);
      conn.commit();
    }catch (Exception e) {
      try{
        conn.rollback();
      }catch (SQLException e2) {
        throw new DataException("Could not roll back the database transaction!", e2);
      }
      throw new DataException("An error occurred while saving the business object information.", e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }//update
  
  /** Internal method to update a Account in the database */
  public void save(Account acct, Connection conn) throws Exception {
    Cache.getInstance().put(acct.getId(), acct);
    if (acct.isObjectAlreadyInDB()) {
      update(acct, conn);
    }else{
      insert(acct, conn);
    }//if
    for (Iterator iter = acct.getCustomers().iterator(); iter.hasNext(); ) {
      Customer cust = (Customer)iter.next();
      CustomerDAO.getInstance().save(cust, conn);
    }
  }//update
  
  /** Saves an existing Account to the database */
  private void update(Account acct, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE account SET accountnum=?, ownerid=?, storeid=?, ccname=?, ccnum=?, ccexpmonth=?, ccexpyear=?, balance=? WHERE id=?");
    try {
      stmt.setInt(1, Integer.parseInt(acct.getAccountNum()));
      stmt.setString(2, acct.getOwner() == null ? null : acct.getOwner().getId());
      stmt.setString(3, acct.getStore() == null ? null : acct.getStore().getId());
      stmt.setString(4, acct.getCcName());
      stmt.setString(5, acct.getCcNum());
      stmt.setInt(6, acct.getCcExpMonth());
      stmt.setInt(7, acct.getCcExpYear());
      stmt.setFloat(8, acct.getBalance());
      stmt.setString(9, acct.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Account into the database */
  private void insert(Account acct, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO account (id, accountnum, ownerid, storeid, ccname, ccnum, ccexpmonth, ccexpyear, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
    try {
      stmt.setString(1, acct.getId());
      stmt.setInt(2, Integer.parseInt(acct.getAccountNum()));
      stmt.setString(3, acct.getOwner() == null ? null : acct.getOwner().getId());
      stmt.setString(4, acct.getStore() == null ? null : acct.getStore().getId());
      stmt.setString(5, acct.getCcName());
      stmt.setString(6, acct.getCcNum());
      stmt.setInt(7, acct.getCcExpMonth());
      stmt.setInt(8, acct.getCcExpYear());
      stmt.setFloat(9, acct.getBalance());
      stmt.execute();
      acct.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  
  
  
  //////////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Account from the database */
  public void delete(Account acct) throws DataException {
    delete(acct.getId());
  }
  
  /** Deletes an existing Account from the database, given its id */
  public void delete(String id) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      delete(id, conn);
      conn.commit();
    }catch (Exception e) {
      try{
        conn.rollback();
      }catch (SQLException e2) {
        throw new DataException("Could not roll back the database transaction!", e2);
      }
      throw new DataException("An error occurred while deleting the business object information.", e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }
  
  /** Internal method to delete an existing Account from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM account where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  
  
  
  
  
  //////////////////////////////////////////////////////
  ///   SEARCH methods
  
  /** Retrieves all Accounts from the database */
  public List<Account> getAll() throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      List<Account> accounts = new LinkedList<Account>();
      Statement stmt = conn.createStatement();
      try {
        ResultSet rs = stmt.executeQuery("SELECT * FROM account");
        while (rs.next()) {
          accounts.add(readRecord(rs, conn));
        }
        return accounts;
      }finally{
        stmt.close();
      }
    }catch (Exception e) {
      try{
        conn.rollback();
      }catch (SQLException e2) {
        throw new DataException("Could not roll back the database transaction!", e2);
      }
      throw new DataException("An error occurred while reading the business object information.", e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }
  
  //////////////////////////////////////////////////////
  ///   getMaxRentals and getRentalsOut methods
  
  /** Gets the maximum number of rentals allowed out */
  public int getMaxRentals(Account acct) throws Exception{
    return MembershipDAO.getInstance().readByAcctId(acct.getId()).getMembershipType().getNumAllowedOut();
  }
  
  /** Gets the number of rentals out */
  public int getRentalsOut(Account acct) throws Exception{
    List<Customer> cust = acct.getCustomers();
    List rentals = RentalDAO.getInstance().readRentals(); //All rentals currently checked out
    List<TxLine> txLineList = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    //Get txids from txline where they contain rental
    //call txline and return txs
    List<TxLine> txLines = TxLineDAO.getInstance().getTxLinesFromRentals(rentals);//getTxForRentals()
    for (Iterator<TxLine> iter = txLines.iterator(); iter.hasNext();){
      TxLine next = iter.next();
      for(Iterator<Customer> iter2 = cust.iterator(); iter2.hasNext();){
        //list all txlines if associated with cust
        
        if(next.getTx().getCustomer().getId().equals(iter2.next().getId())){
          txLineList.add(next);
        }
      }
    }
    //Now we have every txline containing an out rental for the  account
    
    return txLineList.size();
  }//read
  
  //////////////////////////////////////////////////////
  ///   AUTO_INCREMENT methods
  
  /** Gets the autoincrement value */
  public String getAutoIncrement() throws DataException, SQLException{
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt = conn.prepareStatement("SELECT MAX(accountNum) AS maxnum FROM account");
    
    int maxnum = 0;
    ResultSet rs = stmt.executeQuery();
    if (rs.next()){
      maxnum = rs.getInt("maxnum");
    } else {
      throw new DataException("An error occurred while autoincrementing account number");
    }
    return maxnum+1+"";
  }
  
}//class
