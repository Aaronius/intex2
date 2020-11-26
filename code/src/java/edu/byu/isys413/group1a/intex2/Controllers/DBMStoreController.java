package edu.byu.isys413.group1a.intex2.Controllers;

import edu.byu.isys413.group1a.intex2.BOs.Store;
import edu.byu.isys413.group1a.intex2.DAOs.StoreDAO;
import edu.byu.isys413.group1a.intex2.GUIs.GUIDBMStoreDisplay;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import java.sql.Connection;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * This class controls database maintenance for store.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class DBMStoreController {
  
  private GUIDBMStoreDisplay display;
  private List<Store> storeList;
  private final String[] COLUMNS = {"Name","Minimum Fee Amount"}; //Column names
  private DefaultTableModel model = null;
  
  /**
   * Creates a new instance of class
   */
  public DBMStoreController(GUIDBMStoreDisplay display) {
    this.display = display;
  }
  
  /**
   * Creates and returns table model
   */
  public DefaultTableModel getDBModel() throws Exception{
    setStoreList(StoreDAO.getInstance().getAll());
    
    model = new DefaultTableModel(COLUMNS, 0);
    for (Store store: storeList) {
      String[] rowArray = new String[2];
      rowArray[0] = store.getName();
      rowArray[1] = store.getPayMinFeeAmt() + "";
      model.addRow(rowArray);
    }
    
    return model;
  }
  
  /**
   * Returns store list
   */
  public List<Store> getStoreList() {
    return storeList;
  }
  
  /**
   * Sets store list
   */
  public void setStoreList(List<Store> storeList) {
    this.storeList = storeList;
  }
  
  /**
   * Saves new store
   */
  public void saveStore(String name, String minFeeAmount) throws Exception{
    
    Store store = StoreDAO.getInstance().create();
    saveStore(store, name, minFeeAmount);
  }
  
  /**
   * Saves existing store
   */
  public void saveStore(Store store, String name, String minFeeAmount) throws Exception{
    Connection conn = ConnectionPool.getInstance().get();
    
    store.setName(name);
    store.setPayMinFeeAmt(Float.parseFloat(minFeeAmount));
    
    StoreDAO.getInstance().save(store, conn);
    conn.commit();
    
    // Refresh table model
    // Note: we could have just changed/inserted respective rows, but other rows
    // may have been effected in the meantime. It's safer if we just reload.
    display.setDisplayTableModel();
    model.fireTableDataChanged();
  }
  
}
