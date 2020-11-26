
package edu.byu.isys413.group1a.intex2.Controllers;

import edu.byu.isys413.group1a.intex2.BOs.Product;
import edu.byu.isys413.group1a.intex2.BOs.Store;
import edu.byu.isys413.group1a.intex2.BOs.StoreProduct;
import edu.byu.isys413.group1a.intex2.DAOs.ProductDAO;
import edu.byu.isys413.group1a.intex2.DAOs.StoreDAO;
import edu.byu.isys413.group1a.intex2.DAOs.StoreProductDAO;
import edu.byu.isys413.group1a.intex2.GUIs.GUIDBMStoreProductDisplay;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import java.sql.Connection;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

/**
 * This class controls database maintenance for store product.
 *
 * @author Aaron Hardy, Section 1A, me@aaronhardy.com
 */
public class DBMStoreProductController {
  
  private GUIDBMStoreProductDisplay display;
  private List<StoreProduct> storeProductList;
  private final String[] COLUMNS = {"Store","Product","QOH","Reorder Point","Reorder Quantity"}; //Column names
  private DefaultTableModel model = null;
  
  /**
   * Inner class for a list box item which will later be placed into a combo box model.
   */
  class ListBoxItem {
    private String title = null;
    private Object obj = null;
    
    /** Creates a new instance of inner class */
    public ListBoxItem(Object obj, String title) {
      this.title = title;
      this.obj = obj;
    }
    
    /** Returns title */
    public String toString() {
      return title;
    }
    
    /** Returns object */
    public Object getObject(){
      return obj;
    }
  }
  
  /**
   * Creates instance of class
   */
  public DBMStoreProductController(GUIDBMStoreProductDisplay display) {
    this.display = display;
  }
  
  /**
   * Creates and returns database model
   */
  public DefaultTableModel getDBModel() throws Exception{
    setStoreProductList(StoreProductDAO.getInstance().getAll());
    
    model = new DefaultTableModel(COLUMNS, 0);
    for (StoreProduct sp: storeProductList) {
      String[] rowArray = new String[5];
      rowArray[0] = sp.getStore().getName();
      rowArray[1] = sp.getProd().getDescription();
      rowArray[2] = sp.getQtyOnHand() + "";
      rowArray[3] = sp.getReorderPoint() + "";
      rowArray[4] = sp.getReorderQTY() + "";
      model.addRow(rowArray);
    }
    
    return model;
  }
  
  /**
   * Returns store product list
   */
  public List<StoreProduct> getStoreProductList() {
    return storeProductList;
  }
  
  /**
   * Sets store product list
   */
  public void setStoreProductList(List<StoreProduct> storeProductList) {
    this.storeProductList = storeProductList;
  }  
  
  /**
   * Builds and returns model for "store" combo box
   */
  public DefaultComboBoxModel getStoreModel() throws Exception{
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    List<Store> stores = StoreDAO.getInstance().getAll();
    for (Store store: stores){
      model.addElement(new ListBoxItem(store, store.getName()));
    }
    return model;
  }
  
  /**
   * Builds and returns model for "product" combo box
   */
  public DefaultComboBoxModel getProductModel() throws Exception{
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    List<Product> products = ProductDAO.getInstance().getAll();
    for (Product product: products){
      model.addElement(new ListBoxItem(product, product.getDescription()));
    }
    return model;
  }
  
  /**
   * Returns index for given object in given model
   */
  public int getModelIndex(Object objectElementBeingModified, ComboBoxModel model){
    for (int i=0; i<model.getSize(); i++){
      if(objectElementBeingModified == ((ListBoxItem)model.getElementAt(i)).getObject()){
        return i;
      }
    }
    return 0;
  }
  
  /**
   * Saves new store product
   */
  public void saveStoreProduct(Object objStore, Object objProduct,
          String qoh, String reorderPoint, String reorderQuantity) throws Exception{
    
    StoreProduct sp = StoreProductDAO.getInstance().create();
    saveStoreProduct(sp, objStore, objProduct, qoh, reorderPoint, reorderQuantity);
  }

  /**
   * Saves existing store product
   */
  public void saveStoreProduct(StoreProduct sp, Object objStore, Object objProduct,
          String qoh, String reorderPoint, String reorderQuantity) throws Exception{
    
    Connection conn = ConnectionPool.getInstance().get();
    
    // Parameters are objects straight from the combo box model.
    // They are first cast as a ListBoxItem, then the inner object is pulled from them,
    // and finally they are cast as what they really are.
    Store store = (Store)((ListBoxItem)objStore).getObject();
    Product product = (Product)((ListBoxItem)objProduct).getObject();
    
    sp.setStore(store);
    sp.setProd(product);
    sp.setQtyOnHand(Integer.parseInt(qoh));
    sp.setReorderPoint(Integer.parseInt(reorderPoint));
    sp.setReorderQTY(Integer.parseInt(reorderQuantity));
    
    StoreProductDAO.getInstance().save(sp, conn);
    conn.commit();
    
    // Refresh table model
    // Note: we could have just changed/inserted respective rows, but other rows
    // may have been effected in the meantime. It's safer if we just reload.
    display.setDisplayTableModel();
    model.fireTableDataChanged();
  }
  
}
