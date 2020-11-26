package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.Customer;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.List;
import java.util.LinkedList;

/**
 * A singleton object that CRUD's Customer objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class CustomerDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static CustomerDAO instance = null;
  
  /** Creates a new instance of CustomerDAO */
  private CustomerDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized CustomerDAO getInstance() {
    if (instance == null) {
      instance = new CustomerDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Customer in the database */
  public Customer create() throws DataException {
    Customer cust = new Customer();
    cust.setObjectAlreadyInDB(false);
    cust.setId(GUID.generate());
    Cache.getInstance().put(cust.getId(), cust);
    return cust;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Customer from the database */
  public Customer read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (Customer)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Customer from the database */
  synchronized Customer read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (Customer)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customer where id=?");
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("Customer with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Customer object from a record */
  public synchronized Customer readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (Customer)Cache.getInstance().get(rs.getString("id"));
    }
    
    Customer cust = new Customer();
    try{
      cust.setObjectAlreadyInDB(true);
      cust.setId(rs.getString("id"));
      Cache.getInstance().put(cust.getId(), cust);
      cust.setLastName(rs.getString("lname"));
      cust.setFirstName(rs.getString("fname"));
      cust.setAddress(rs.getString("address"));
      cust.setCity(rs.getString("city"));
      cust.setState(rs.getString("state"));
      cust.setZipCode(rs.getString("zipCode"));
      cust.setPhone(rs.getString("phone"));
      cust.setAccount(AccountDAO.getInstance().read(rs.getString("accountid")));
    }catch (Exception e){
      e.printStackTrace();
    }
    return cust;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Customer in the database */
  public void save(Customer cust) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(cust, conn);
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
  
  /** Internal method to update a Customer in the database */
  public void save(Customer cust, Connection conn) throws Exception {
    Cache.getInstance().put(cust.getId(), cust);
    if (cust.isObjectAlreadyInDB()) {
      update(cust, conn);
    }else{
      insert(cust, conn);
    }//if
    
  }//save
  
  /** Saves an existing Customer to the database */
  private void update(Customer cust, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE customer SET lname=?, fname=?, address=?, city=?, state=?, zipCode=?, phone=?, accountid=? WHERE id=?");
    try {
      stmt.setString(1, cust.getLastName());
      stmt.setString(2, cust.getFirstName());
      stmt.setString(3, cust.getAddress());
      stmt.setString(4, cust.getCity());
      stmt.setString(5, cust.getState());
      stmt.setString(6, cust.getZipCode());
      stmt.setString(7, cust.getPhone());
      stmt.setString(8, cust.getAccount() == null ? null : cust.getAccount().getId());
      stmt.setString(9, cust.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Customer into the database */
  private void insert(Customer cust, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO customer (id, lname, fname, address, city, state, zipCode, phone, accountid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
    try {
      stmt.setString(1, cust.getId());
      stmt.setString(2, cust.getLastName());
      stmt.setString(3, cust.getFirstName());
      stmt.setString(4, cust.getAddress());
      stmt.setString(5, cust.getCity());
      stmt.setString(6, cust.getState());
      stmt.setString(7, cust.getZipCode());
      stmt.setString(8, cust.getPhone());
      stmt.setString(9, cust.getAccount() == null ? null : cust.getAccount().getId());
      stmt.execute();
      cust.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Customer from the database */
  public void delete(Customer cust) throws DataException {
    delete(cust.getId());
  }
  
  /** Deletes an existing Customer from the database, given its id */
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
  
  /** Internal method to delete an existing Customer from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM customer where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  
  
  ////////////////////////////////////////////////
  ///   SEARCH methods
  
  /** Retrieves all Customers from the database */
  public List<Customer> getAll() throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customer");
      return search(stmt);
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
  
  /** Retrieves all Customers from the database with the given phone number */
  public List<Customer> getByPhone(String phone) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customer WHERE phone=?");
      stmt.setString(1, phone);
      return search(stmt);
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
  
  /** Internal method to search by certain fields */
  private List<Customer> search(PreparedStatement stmt) throws Exception {
    List<Customer> customers = new LinkedList<Customer>();
    try {
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        customers.add(readRecord(rs));
      }
    }finally{
      stmt.close();
    }
    return customers;
  }
  /** Looks up a Customer from the database by either a firstname, lastname, phonenumber or any combination of the three */
  public List<Customer> readByX(String fname, String lname, String phone) throws Exception {
    String stmtString = "SELECT * FROM customer WHERE ";
    List<String> valueList = new LinkedList();
    List<Customer> custList = new LinkedList();
    int counter = 0;
    
    boolean fnameValid = fname.equals("");
    boolean lnameValid = lname.equals("");
    boolean phoneValid = phone.equals("");
    
    if (!fnameValid) {
      stmtString += "LOWER(fname) like ?";
      valueList.add(fname.toLowerCase()+"%");
      counter ++;
    }
    
    if (!lnameValid) {
      if (counter > 0)
        stmtString += " AND ";
      stmtString += "LOWER(lname) like ?";
      valueList.add(lname.toLowerCase()+"%");
      counter ++;
    }
    
    if (!phoneValid) {
      if (counter > 0)
        stmtString += " AND ";
      stmtString += "phone=?";
      valueList.add(phone);
      counter ++;
    }
    
    Connection conn = null;
    try{
      conn = ConnectionPool.getInstance().get();
      PreparedStatement stmt = conn.prepareStatement(stmtString);
      if (counter == 0) {
        //throw new DataException ("Please enter valid search criteria!");
      }
      for(int count = 0; count < valueList.size(); count++) {
        stmt.setString(count + 1, valueList.get(count));
        
      }
      
      ResultSet rs = stmt.executeQuery();
      
      while(rs.next()){
        Customer cust = this.create();
        cust.setObjectAlreadyInDB(true);
        cust.setId(rs.getString("id"));
        Cache.getInstance().put(cust.getId(), cust);
        cust.setLastName(rs.getString("lname"));
        cust.setFirstName(rs.getString("fname"));
        cust.setAddress(rs.getString("address"));
        cust.setCity(rs.getString("city"));
        cust.setState(rs.getString("state"));
        cust.setZipCode(rs.getString("zipCode"));
        cust.setPhone(rs.getString("phone"));
        cust.setAccount(AccountDAO.getInstance().read(rs.getString("accountid")));
        
        custList.add(cust);
      }
      
    } catch (Exception e){
      e.printStackTrace();
      throw new DataException("An error occurred while reading the customer information from the database.", e);
    } finally{
      if (conn != null){
        ConnectionPool.getInstance().release(conn);
      }
    }
    
    return custList;
    
  } // readByX()
  
  
  
}//class
