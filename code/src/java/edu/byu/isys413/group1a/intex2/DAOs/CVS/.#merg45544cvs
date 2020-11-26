package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.Product;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A singleton object that CRUD's Product objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class ProductDAO  {
  
  /////////////////////////////////////////////
  ///   A singleton object
  
  private static ProductDAO instance = null;
  
  /** Creates a new instance of ProductDAO */
  private ProductDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized ProductDAO getInstance() {
    if (instance == null) {
      instance = new ProductDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Product in the database */
  public Product create() throws DataException {
    Product p = new Product();
    p.setObjectAlreadyInDB(false);
    p.setId(GUID.generate());
    Cache.getInstance().put(p.getId(), p);
    return p;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Product from the database */
  public Product read(String id) throws DataException {
    //if (Cache.getInstance().containsKey(id)) {
    //  return (Product)Cache.getInstance().get(id);
    //}
    Connection conn = ConnectionPool.getInstance().get();
    try {
      return read(id, conn);
    }catch (Exception e) {
      throw new DataException(e.getMessage(), e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }
  
  /** Internal method to read an existing Product from the database */
  synchronized Product read(String id, Connection conn) throws Exception {
    //if (Cache.getInstance().containsKey(id)) {
    //  return (Product)Cache.getInstance().get(id);
    //}
    
    PreparedStatement stmt;
    if (id.length() == 12){
      stmt = conn.prepareStatement("SELECT * FROM product WHERE sku=?");
    } else {
      stmt = conn.prepareStatement("SELECT * FROM product WHERE id=?");
    }
    
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("Product with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Product object from a record */
  synchronized Product readRecord(ResultSet rs) throws Exception {
    //if (Cache.getInstance().containsKey(rs.getString("id"))) {
    //  return (Product)Cache.getInstance().get(rs.getString("id"));
    //}
    Product prod = new Product();
    prod.setObjectAlreadyInDB(true);
    prod.setId(rs.getString("id"));
    //Cache.getInstance().put(prod.getId(), prod);
    prod.setSku(rs.getString("sku"));
    prod.setType(rs.getString("type"));
    if (prod.getType().equals("r")){
      prod.setSubProduct(RefreshmentDAO.getInstance().read(rs.getString("sku")));
    }
    if (prod.getType().equals("s")){
      prod.setSubProduct(SaleVideoDAO.getInstance().read(rs.getString("sku")));
    }
    prod.setAmount(prod.getSubProduct().getAmount());
    prod.setDescription(prod.getSubProduct().getDescription());
    return prod;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Product in the database */
  public void save(Product p) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(p, conn);
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
  
  /** Internal method to update a Product in the database */
  public void save(Product p, Connection conn) throws Exception {
    Cache.getInstance().put(p.getId(), p);
    if (p.isObjectAlreadyInDB()) {
      update(p, conn);
    }else{
      insert(p, conn);
    }//if
    
  }//save
  
  /** Saves an existing Product to the database */
  private void update(Product p, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE Product SET sku=?, type=? WHERE id=?");
    try {
      stmt.setString(1, p.getSku());
      stmt.setString(2, p.getType());
      stmt.setString(3, p.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Product into the database */
  private void insert(Product p, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Product (id, sku, type) VALUES (?, ?, ?)");
    try {
      stmt.setString(1, p.getId());
      stmt.setString(2, p.getSku());
      stmt.setString(3, p.getType());
      stmt.execute();
      p.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Product from the database */
  public void delete(Product p) throws DataException {
    delete(p.getId());
  }
  
  /** Deletes an existing Product from the database, given its id */
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
  
  /** Internal method to delete an existing Product from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM Product where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  /** Method for getting all Products */
  public List getAll() throws Exception{
    List<Product> list = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM product");
    ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
      list.add(readRecord(rs));
    }
    
    return list;    
  }
  
}//class
