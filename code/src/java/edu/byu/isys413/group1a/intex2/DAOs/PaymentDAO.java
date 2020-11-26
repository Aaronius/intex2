package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.Payment;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;

/**
 * A singleton object that CRUD's Payment objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class PaymentDAO {
  
  /////////////////////////////////////////////
  ///   A singleton object
  
  private static PaymentDAO instance = null;
  
  /** Creates a new instance of PaymentDAO */
  private PaymentDAO() {
  }
  
  /** Retrieves the single instance of this class */
  public static synchronized PaymentDAO getInstance() {
    if (instance == null) {
      instance = new PaymentDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Payment in the database */
  public Payment create() throws DataException {
    Payment payment = new Payment();
    payment.setObjectAlreadyInDB(false);
    payment.setId(GUID.generate());
    Cache.getInstance().put(payment.getId(), payment);
    return payment;
  }//create
  
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Payment from the database */
  public Payment read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (Payment)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Payment from the database */
  synchronized Payment read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (Payment)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Payment where id=?");
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("Payment with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Payment object from a record */
  synchronized Payment readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (Payment)Cache.getInstance().get(rs.getString("id"));
    }
    Payment p = new Payment();
    p.setObjectAlreadyInDB(true);
    p.setId(rs.getString("id"));
    Cache.getInstance().put(p.getId(), p);
    p.setAmount(rs.getFloat("amount"));
    p.setAmtTendered(rs.getFloat("amttendered"));
    p.setChange(rs.getFloat("change"));
    
    return p;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Payment in the database */
  public void save(Payment payment) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(payment, conn);
      conn.commit();
    }catch (Exception e) {
      e.printStackTrace();
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
  
  /** Internal method to update a Payment in the database */
  public void save(Payment payment, Connection conn) throws Exception {
    Cache.getInstance().put(payment.getId(), payment);
    if (payment.isObjectAlreadyInDB()) {
      update(payment, conn);
    }else{
      insert(payment, conn);
    }//if
    
  }//save
  
  /** Saves an existing Payment to the database */
  private void update(Payment payment, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE Payment SET amount=?, amttendered=?, change=? WHERE id=?");
    try {
      stmt.setFloat(1, payment.getAmount());
      stmt.setFloat(2, payment.getAmtTendered());
      stmt.setFloat(3, payment.getChange());
      stmt.setString(4, payment.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Payment into the database */
  private void insert(Payment payment, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Payment (id, amount, amttendered, change) VALUES (?, ?, ?, ?)");
    try {
      stmt.setString(1, payment.getId());
      stmt.setFloat(2, payment.getAmount());
      stmt.setFloat(3, payment.getAmtTendered());
      stmt.setFloat(4, payment.getChange());
      stmt.execute();
      payment.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Payment from the database */
  public void delete(Payment payment) throws DataException {
    delete(payment.getId());
  }
  
  /** Deletes an existing Payment from the database, given its id */
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
  
  /** Internal method to delete an existing Payment from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM Payment where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  
}
