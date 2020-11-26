package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.StoreProduct;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.*;

/**
 * A singleton object that CRUD's StoreProduct objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class StoreProductDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static StoreProductDAO instance = null;
  
  /** Creates a new instance of StoreProductDAO */
  private StoreProductDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized StoreProductDAO getInstance() {
    if (instance == null) {
      instance = new StoreProductDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Store Product in the database */
  public StoreProduct create() throws DataException {
    StoreProduct sp = new StoreProduct();
    sp.setObjectAlreadyInDB(false);
    sp.setId(GUID.generate());
    Cache.getInstance().put(sp.getId(), sp);
    return sp;
  }//create
  
  ///////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Store Product from the database */
  public StoreProduct read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (StoreProduct)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Store Product from the database */
  StoreProduct read(String id, Connection conn) throws Exception{
    if (Cache.getInstance().containsKey(id)) {
      return (StoreProduct)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM StoreProduct where id=?");
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs, conn);
      }//if
      throw new DataException("StoreProduct with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to read an existing Store Product from the database */
  public StoreProduct readByForeign(String storeId, String productId) throws Exception{
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM storeproduct WHERE storeid=? and prodid=?");
    try{
      stmt.setString(1, storeId);
      stmt.setString(2, productId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs,conn);
      }//if
      throw new DataException("A store product with store ID '" + storeId + "' and product ID '"+ productId +"' not found.");
    }finally{
      stmt.close();
      ConnectionPool.getInstance().release(conn);
    }
  }//read
  
  /** Internal method to create a Store Product object from a record */
  StoreProduct readRecord(ResultSet rs, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (StoreProduct)Cache.getInstance().get(rs.getString("id"));
    }
    StoreProduct sp = new StoreProduct();
    sp.setObjectAlreadyInDB(true);
    sp.setId(rs.getString("id"));
    Cache.getInstance().put(sp.getId(), sp);
    sp.setStore(StoreDAO.getInstance().read(rs.getString("storeid")));
    sp.setProd(ProductDAO.getInstance().read(rs.getString("prodid")));
    sp.setQtyOnHand(rs.getInt("qoh"));
    sp.setReorderPoint(rs.getInt("reorderpt"));
    sp.setReorderQTY(rs.getInt("reorderqty"));
    return sp;
  }//readRecord
  
  /** Internal method to read an existing Store Product from the database */
  public StoreProduct readByForeign(String storeId, String prodId, Connection conn) throws Exception{
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM StoreProduct where storeid=? and prodid=?");
    try{
      stmt.setString(1, storeId);
      stmt.setString(2, prodId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs, conn);
      }//if
      throw new DataException("StoreProduct with Store Id '" + storeId + "' and Product Id '"+ prodId +"' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Store Product in the database */
  public void save(StoreProduct sp) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(sp, conn);
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
  
  /** Internal method to update a Store Product in the database */
  public void save(StoreProduct sp, Connection conn) throws Exception {
    Cache.getInstance().put(sp.getId(), sp);
    if (sp.isObjectAlreadyInDB()) {
      update(sp, conn);
    }else{
      insert(sp, conn);
    }//if
    
  }//save
  
  /** Saves an existing Store Product to the database */
  private void update(StoreProduct sp, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE StoreProduct SET storeid=?, prodid=?, qoh=?, reorderpt=?, reorderqty=? WHERE id=?");
    try {
      stmt.setString(1, sp.getStore() == null ? null : sp.getStore().getId());
      stmt.setString(2, sp.getProd() == null ? null : sp.getProd().getId());
      stmt.setInt(3, sp.getQtyOnHand());
      stmt.setInt(4, sp.getReorderPoint());
      stmt.setInt(5, sp.getReorderQTY());
      stmt.setString(6, sp.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Store Product into the database */
  private void insert(StoreProduct sp, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO StoreProduct (id, storeid, prodid, qoh, reorderpt, reorderqty) VALUES (?, ?, ?, ?, ?, ?)");
    try {
      stmt.setString(1, sp.getId());
      stmt.setString(2, sp.getStore() == null ? null : sp.getStore().getId());
      stmt.setString(3, sp.getProd() == null ? null : sp.getProd().getId());
      stmt.setInt(4, sp.getQtyOnHand());
      stmt.setInt(5, sp.getReorderPoint());
      stmt.setInt(6, sp.getReorderQTY());
      stmt.execute();
      sp.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Store Product from the database */
  public void delete(StoreProduct sp) throws DataException {
    delete(sp.getId());
  }
  
  /** Deletes an existing Store Product from the database, given its id */
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
  
  /** Internal method to delete an existing Store Product from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM StoreProduct where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  /** Method for getting all Stores */
  public List getAll() throws Exception{
    List<StoreProduct> list = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM storeproduct");
    ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
      list.add(readRecord(rs,conn));
    }
    
    return list;
  }
  
}//class
