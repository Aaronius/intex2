package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.RentalVideo;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.*;

/**
 * A singleton object that CRUD's RentalVideo objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class RentalVideoDAO  {
  
  /////////////////////////////////////////////
  ///   A singleton object
  
  private static RentalVideoDAO instance = null;
  
  /** Creates a new instance of RentalVideoDAO */
  private RentalVideoDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized RentalVideoDAO getInstance() {
    if (instance == null) {
      instance = new RentalVideoDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Rental Video in the database */
  public RentalVideo create() throws DataException {
    RentalVideo rv = new RentalVideo();
    rv.setObjectAlreadyInDB(false);
    rv.setId(GUID.generate());
    Cache.getInstance().put(rv.getId(), rv);
    return rv;
  }//create
  
  
  ///////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Rental Video from the database */
  public RentalVideo read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (RentalVideo)Cache.getInstance().get(id);
    }
    Connection conn = ConnectionPool.getInstance().get();
    try {
      return read(id, conn);
    }catch (Exception e) {
      throw new DataException(e.getMessage(), e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }
  
  /** Internal method to read an existing Rental Video from the database */
  RentalVideo read(String id, Connection conn) throws Exception{
    if (Cache.getInstance().containsKey(id)) {
      return (RentalVideo)Cache.getInstance().get(id);
    }
    
    PreparedStatement stmt;
    if (id.length() == 8){
      stmt = conn.prepareStatement("SELECT * FROM rentalvideo WHERE serialnum=?");
    } else {
      stmt = conn.prepareStatement("SELECT * FROM rentalvideo WHERE id=?");
    }
    
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("RentalVideo with id/serial number '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to read an existing Rental Video from the database using  */
  public List<RentalVideo> readAvailableVideosByVCRTCVId(String vcrtcvid) throws Exception{
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM rentalvideo WHERE vcrtcvid=? AND status='in' AND reservetime < ?");
    
    try{
      stmt.setString(1, vcrtcvid);
      stmt.setLong(2, Calendar.getInstance().getTimeInMillis() - 3600000);
      ResultSet rs = stmt.executeQuery();
      List<RentalVideo> rv= new ArrayList();
      while(rs.next()) {
        rv.add(readRecord(rs));
      }
      return rv;
      //throw new DataException("Rental with vcrtcvid '" + vcrtcvid + "' not found.");
    }catch (Exception e) {
      throw new DataException(e.getMessage(), e);
    }finally{
      stmt.close();
      ConnectionPool.getInstance().release(conn);
    }
  }//read
  
  
  /** Internal method to create a Rental Video object from a record */
  RentalVideo readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (RentalVideo)Cache.getInstance().get(rs.getString("id"));
    }
    RentalVideo rv = new RentalVideo();
    rv.setObjectAlreadyInDB(true);
    rv.setId(rs.getString("id"));
    Cache.getInstance().put(rv.getId(), rv);
    rv.setStore(StoreDAO.getInstance().read(rs.getString("storeid")));
    rv.setVcrtcv(VCRTCVDAO.getInstance().read(rs.getString("vcrtcvid")));
    rv.setSerialNum(rs.getString("serialnum"));
    rv.setStatus(rs.getString("status"));
    Calendar reserveTime = Calendar.getInstance();
    reserveTime.setTimeInMillis(rs.getLong("reservetime"));
    rv.setReserveTime(reserveTime);
    if (!(rs.getString("reserveacct").equals(""))){
      rv.setReserveAcct(AccountDAO.getInstance().read(rs.getString("reserveacct")));
    }
    return rv;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Rental Video in the database */
  public void save(RentalVideo rv) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(rv, conn);
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
  
  /** Internal method to update a Rental Video in the database */
  public void save(RentalVideo rv, Connection conn) throws Exception {
    Cache.getInstance().put(rv.getId(), rv);
    if (rv.isObjectAlreadyInDB()) {
      update(rv, conn);
    }else{
      insert(rv, conn);
    }//if
    
  }//save
  
  /** Saves an existing Rental Video to the database */
  private void update(RentalVideo rv, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE RentalVideo SET storeid=?, vcrtcvid=?, serialnum=?, status=?, reservetime=?, reserveacct=? WHERE id=?");
    try {
      stmt.setString(1, rv.getStore() == null ? null : rv.getStore().getId());
      stmt.setString(2, rv.getVcrtcv() == null ? null : rv.getVcrtcv().getId());
      stmt.setString(3, rv.getSerialNum());
      stmt.setString(4, rv.getStatus());
      stmt.setLong(5, rv.getReserveTime() == null ? 0 : rv.getReserveTime().getTimeInMillis());
      stmt.setString(6, rv.getReserveAcct() == null ? "" : rv.getReserveAcct().getId());
      stmt.setString(7, rv.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Rental Video into the database */
  private void insert(RentalVideo rv, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO RentalVideo (id, storeid, vcrtcvid, serialnum, status, reservetime, reserveacct) VALUES (?, ?, ?, ?, ?, ?, ?)");
    try {
      stmt.setString(1, rv.getId());
      stmt.setString(2, rv.getStore() == null ? null : rv.getStore().getId());
      stmt.setString(3, rv.getVcrtcv() == null ? null : rv.getVcrtcv().getId());
      stmt.setString(4, rv.getSerialNum());
      stmt.setString(5, rv.getStatus());
      stmt.setLong(6, rv.getReserveTime() == null ? 0 : rv.getReserveTime().getTimeInMillis());
      stmt.setString(7, rv.getReserveAcct() == null ? "" : rv.getReserveAcct().getId());
      stmt.execute();
      rv.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Rental Video from the database */
  public void delete(RentalVideo rv) throws DataException {
    delete(rv.getId());
  }
  
  /** Deletes an existing Rental Video from the database, given its id */
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
  
  /** Internal method to delete an existing Rental Video from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM RentalVideo where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  /** Method for getting all Rental Videos */
  public List getAll() throws Exception{
    List<RentalVideo> list = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM rentalvideo");
    ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
      list.add(readRecord(rs));
    }
    
    return list;
  }
  
  /** Method for getting all Rental Videos */
  public int getNumberReservedByAccount(String accountId) throws Exception{
    List<RentalVideo> list = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    
    PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS recordcount FROM rentalvideo WHERE reservetime > ? AND reserveacct = ?");
    stmt.setLong(1, Calendar.getInstance().getTimeInMillis() - 3600000);
    stmt.setString(2, accountId);
    ResultSet rs = stmt.executeQuery();
    
    rs.next();
    return rs.getInt("recordcount");
  }
  
}//class
