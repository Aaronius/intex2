package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.Tx;
import edu.byu.isys413.group1a.intex2.BOs.TxLine;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.*;

/**
 * A singleton object that CRUD's Customer objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class TxDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static TxDAO instance = null;
  
  /** Creates a new instance of TxDAO */
  private TxDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized TxDAO getInstance() {
    if (instance == null) {
      instance = new TxDAO();
    }
    return instance;
  }//getInstance
  
  
  
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Transaction in the database */
  public Tx create() throws DataException {
    Tx tx = new Tx();
    tx.setObjectAlreadyInDB(false);
    tx.setId(GUID.generate());
    Cache.getInstance().put(tx.getId(), tx);
    Calendar todayDate = Calendar.getInstance();
    tx.setDate(todayDate);
    return tx;
  }//create
  
  ///////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Transaction from the database */
  public Tx read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (Tx)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Transaction from the database */
  public Tx read(String id, Connection conn) throws Exception{
    if (Cache.getInstance().containsKey(id)) {
      return (Tx)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = null;
    if (id.length() == 9){
      stmt = conn.prepareStatement("SELECT * FROM tx where txid=?");
    }else{
      stmt = conn.prepareStatement("SELECT * FROM tx where id=?");
    }
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs, conn);
      }//if
      throw new DataException("Tx with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Transaction object from a record */
  public Tx readRecord(ResultSet rs, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (Tx)Cache.getInstance().get(rs.getString("id"));
    }
    Tx tx = new Tx();
    tx.setObjectAlreadyInDB(true);
    tx.setId(rs.getString("id"));
    Cache.getInstance().put(tx.getId(), tx);
    tx.setCustomer(CustomerDAO.getInstance().read(rs.getString("custid")));
    tx.setStore(StoreDAO.getInstance().read(rs.getString("storeid")));
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(rs.getLong("txdate"));
    tx.setDate(cal);
    tx.setTax(rs.getFloat("tax"));
    tx.setTotal(rs.getFloat("total"));
    tx.setPayment(PaymentDAO.getInstance().read(rs.getString("payid")));
    tx.setTxid(rs.getString("txid"));
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM txline WHERE txid=?");
    try {
      stmt.setString(1, tx.getId());
      ResultSet rs2 = stmt.executeQuery();
      while (rs2.next()) {
        TxLine txline = TxLineDAO.getInstance().readRecord(rs2, conn);
        tx.addTxLine(txline);
      }
    } catch (Exception e){
      e.printStackTrace();
    } finally{
      stmt.close();
    }
    return tx;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Transaction in the database */
  public void save(Tx tx) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(tx, conn);
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
  
  /** Internal method to update a Transaction in the database */
  public void save(Tx tx, Connection conn) throws Exception {
    Cache.getInstance().put(tx.getId(), tx);
    if (tx.isObjectAlreadyInDB()) {
      update(tx, conn);
    }else{
      insert(tx, conn);
    }//if
    for (Iterator iter = tx.getTxLines().iterator(); iter.hasNext(); ) {
      TxLine txLine = (TxLine)iter.next();
      TxLineDAO.getInstance().save(txLine, conn);
    }
  }//save
  
  /** Saves an existing Transaction to the database */
  private void update(Tx tx, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE tx SET custid=?, storeid=?, txdate=?, tax=?, total=?, payid=?, txid=? WHERE id=?");
    try {
      stmt.setString(1, tx.getCustomer() == null ? null : tx.getCustomer().getId());
      stmt.setString(2, tx.getStore() == null ? null : tx.getStore().getId());
      stmt.setLong(3, tx.getDate().getTimeInMillis());
      stmt.setFloat(4, tx.getTax());
      stmt.setFloat(5, tx.getTotal());
      stmt.setString(6, tx.getPayment() == null ? null : tx.getPayment().getId());
      stmt.setString(7, tx.getTxid());
      stmt.setString(8, tx.getTxid());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Transaction into the database */
  private void insert(Tx tx, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO tx (id, custid, storeid, txdate, tax, total, payid, txid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
    try {
      stmt.setString(1, tx.getId());
      stmt.setString(2, tx.getCustomer() == null ? null : tx.getCustomer().getId());
      stmt.setString(3, tx.getStore() == null ? null : tx.getStore().getId());
      stmt.setLong(4, tx.getDate().getTimeInMillis());
      stmt.setFloat(5, tx.getTax());
      stmt.setFloat(6, tx.getTotal());
      stmt.setString(7, tx.getPayment() == null ? null : tx.getPayment().getId());
      stmt.setString(8, getAutoIncrement());
      stmt.execute();
      tx.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Transaction from the database */
  public void delete(Tx tx) throws DataException {
    delete(tx.getId());
  }
  
  /** Deletes an existing Transaction from the database, given its id */
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
  
  /** Internal method to delete an existing Transaction from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM tx where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  //////////////////////////////////////////////////////
  ///   AUTO_INCREMENT methods
  
  /** Get Auto Increment Value */
  public String getAutoIncrement() throws DataException, SQLException{
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt = conn.prepareStatement("SELECT MAX(txid) AS maxnum FROM tx");
    
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
