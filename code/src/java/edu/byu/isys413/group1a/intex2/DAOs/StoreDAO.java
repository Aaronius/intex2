package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.Store;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A singleton object that CRUD's Customer objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class StoreDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static StoreDAO instance = null;
  
  /** Creates a new instance of StoreDAO */
  private StoreDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized StoreDAO getInstance() {
    if (instance == null) {
      instance = new StoreDAO();
    }
    return instance;
  }//getInstance
  
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Store in the database */
  public Store create() throws DataException {
    Store s = new Store();
    s.setObjectAlreadyInDB(false);
    s.setId(GUID.generate());
    Cache.getInstance().put(s.getId(), s);
    return s;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Store from the database */
  public Store read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (Store)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Store from the database */
  synchronized Store read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (Store)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM store WHERE id=?");
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("Store with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Store object from a record */
  synchronized Store readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (Store)Cache.getInstance().get(rs.getString("id"));
    }
    Store store = new Store();
    store.setObjectAlreadyInDB(true);
    store.setId(rs.getString("id"));
    store.setName(rs.getString("cityname"));
    store.setPayMinFeeAmt(rs.getFloat("minfeeamount"));
    store.setAddress(rs.getString("address"));
    store.setState(rs.getString("state"));
    store.setZipCode(rs.getString("zipcode"));
    store.setPhone(rs.getString("phone"));
    store.setTaxRate(rs.getFloat("taxrate"));
    Cache.getInstance().put(store.getId(), store);
    return store;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Store in the database */
  public void save(Store s) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(s, conn);
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
  
  /** Internal method to update a Store in the database */
  public void save(Store s, Connection conn) throws Exception {
    Cache.getInstance().put(s.getId(), s);
    if (s.isObjectAlreadyInDB()) {
      update(s, conn);
    }else{
      insert(s, conn);
    }//if
    
  }//save
  
  /** Saves an existing Store to the database */
  private void update(Store s, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE Store SET cityname=?, minfeeamount=?, address=?, state=?, zipcode=?, phone=?, taxrate=? WHERE id=?");
    try {
      stmt.setString(1, s.getName());
      stmt.setFloat(2, s.getPayMinFeeAmt());
      stmt.setString(3, s.getAddress());
      stmt.setString(4, s.getState());
      stmt.setString(5, s.getZipCode());
      stmt.setString(6, s.getPhone());
      stmt.setFloat(7, s.getTaxRate());
      stmt.setString(8, s.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Store into the database */
  private void insert(Store s, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Store (id, cityname, minfeeamount, address, state, zipcode, phone, taxrate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
    try {
      stmt.setString(1, s.getId());
      stmt.setString(2, s.getName());
      stmt.setFloat(3, s.getPayMinFeeAmt());
      stmt.setString(4, s.getAddress());
      stmt.setString(5, s.getState());
      stmt.setString(6, s.getZipCode());
      stmt.setString(7, s.getPhone());
      stmt.setFloat(8, s.getTaxRate());
      stmt.execute();
      s.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Store from the database */
  public void delete(Store s) throws DataException {
    delete(s.getId());
  }
  
  /** Deletes an existing Store from the database, given its id */
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
  
  /** Internal method to delete an existing Store from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM Store where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  /** Method for getting all Stores */
  public List getAll() throws Exception{
    List<Store> list = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM store");
    ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
      list.add(readRecord(rs));
    }
    
    return list;
  }
  
}//class
