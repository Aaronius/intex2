package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.Rental;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

/**
 * A singleton object that CRUD's Rental objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class RentalDAO  {
  
  /////////////////////////////////////////////
  ///   A singleton object
  
  private static RentalDAO instance = null;
  
  /** Creates a new instance of RentalDAO */
  private RentalDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized RentalDAO getInstance() {
    if (instance == null) {
      instance = new RentalDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Rental in the database */
  public Rental create() throws DataException {
    Rental rental = new Rental();
    rental.setObjectAlreadyInDB(false);
    rental.setId(GUID.generate());
    Cache.getInstance().put(rental.getId(), rental);
    return rental;
  }//create
  
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Rental from the database */
  public Rental read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (Rental)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Rental from the database */
  synchronized Rental read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (Rental)Cache.getInstance().get(id);
    }
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM rental WHERE id=?");
    
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("Rental with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to read an existing Rental from the database */
  public Rental readByRentalVideoId(String rvid) throws Exception{
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM rental WHERE rvid=? AND indate=0");
    
    try{
      stmt.setString(1, rvid);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("Rental with rvid '" + rvid + "' not found.");
    }catch (Exception e) {
      throw new DataException("An error occurred while reading the business object information.", e);
    }finally{
      stmt.close();
      ConnectionPool.getInstance().release(conn);
    }
  }//read
  
  /** Internal method to read an existing Rental from the database */
  public List<Rental> readRentals() throws Exception{
    List<Rental> rentals = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM rental WHERE indate=0");
    try {
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        Rental rental = readRecord(rs);
        rentals.add(rental);
      }
    } catch (Exception e){
      throw new DataException("An error occurred while reading the business object information.", e);
    }finally{
      stmt.close();
      ConnectionPool.getInstance().release(conn);
    }
    return rentals;
  }//read
  
  /** Internal method to read an existing Rental from the database */
  public List<Rental> readMonthOverdueRentals() throws Exception{
    List<Rental> rentals = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM rental WHERE duedate < ?");
    
    try {
      
      Calendar monthAgo = Calendar.getInstance();
      monthAgo.add(Calendar.MONTH,-1);
      stmt.setLong(1, monthAgo.getTimeInMillis());
      ResultSet rs = stmt.executeQuery();
      
      while (rs.next()) {
        rentals.add(readRecord(rs));
      }
      
      return rentals;
      
    } catch (Exception e){
      throw new DataException("An error occurred while reading the business object information.", e);
    }finally{
      stmt.close();
      ConnectionPool.getInstance().release(conn);
    }
  }//read
  
  /** Internal method to create a Rental object from a record */
  synchronized Rental readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (Rental)Cache.getInstance().get(rs.getString("id"));
    }
    Rental sv = new Rental();
    sv.setObjectAlreadyInDB(true);
    sv.setId(rs.getString("id"));
    Cache.getInstance().put(sv.getId(), sv);
    sv.setRentalVideo(RentalVideoDAO.getInstance().read(rs.getString("rvid")));
    Calendar outDate = Calendar.getInstance();
    outDate.setTimeInMillis(rs.getLong("outdate"));
    sv.setOutDate(outDate);
    Calendar dueDate = Calendar.getInstance();
    dueDate.setTimeInMillis(rs.getLong("duedate"));
    sv.setDueDate(dueDate);
    Calendar inDate = Calendar.getInstance();
    inDate.setTimeInMillis(rs.getLong("indate"));
    sv.setInDate(inDate);
    
    return sv;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Rental in the database */
  public void save(Rental rental) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(rental, conn);
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
  
  /** Internal method to update a Rental in the database */
  public void save(Rental rental, Connection conn) throws Exception {
    Cache.getInstance().put(rental.getId(), rental);
    if (rental.isObjectAlreadyInDB()) {
      update(rental, conn);
    }else{
      insert(rental, conn);
    }//if
    
  }//save
  
  /** Saves an existing Rental to the database */
  private void update(Rental rental, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE Rental SET rvid=?, outdate=?, indate=?, duedate=? WHERE id=?");
    try {
      stmt.setString(1, rental.getRentalVideo() == null ? null : rental.getRentalVideo().getId());
      stmt.setLong(2, rental.getOutDate().getTimeInMillis());
      stmt.setLong(3, rental.getInDate() == null ? 0 : rental.getInDate().getTimeInMillis());
      stmt.setLong(4, rental.getDueDate().getTimeInMillis());
      stmt.setString(5, rental.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Rental into the database */
  private void insert(Rental rental, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Rental (id, rvid, outdate, indate, duedate) VALUES (?, ?, ?, ?, ?)");
    try {
      stmt.setString(1, rental.getId());
      stmt.setString(2, rental.getRentalVideo() == null ? null : rental.getRentalVideo().getId());
      stmt.setLong(3, rental.getOutDate().getTimeInMillis());
      stmt.setLong(4, rental.getInDate() == null ? 0 : rental.getInDate().getTimeInMillis());
      stmt.setLong(5, rental.getDueDate().getTimeInMillis());
      stmt.execute();
      rental.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Rental from the database */
  public void delete(Rental rental) throws DataException {
    delete(rental.getId());
  }
  
  /** Deletes an existing Rental from the database, given its id */
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
  
  /** Internal method to delete an existing Rental from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM Rental where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
}//class
