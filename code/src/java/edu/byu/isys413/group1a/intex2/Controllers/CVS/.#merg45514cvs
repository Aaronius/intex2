package edu.byu.isys413.group1a.intex2.Controllers;

import edu.byu.isys413.group1a.intex2.BOs.Product;
import edu.byu.isys413.group1a.intex2.DAOs.ProductDAO;
import edu.byu.isys413.group1a.intex2.GUIs.GUIDBMProductDisplay;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import java.sql.Connection;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

/**
 * This class controls database maintenance for products.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class DBMProductController {
  
  private GUIDBMProductDisplay display;
  private List<Product> productList;
  private final String[] COLUMNS = {"SKU","Product Type"}; //Column names
  private DefaultTableModel model = null;
  
  /**
   * Creates a new instance of class
   */
  public DBMProductController(GUIDBMProductDisplay display) {
    this.display = display;
  }
  
  /**
   * Creates and returns table model
   */
  public DefaultTableModel getDBModel() throws Exception{
    setProductList(ProductDAO.getInstance().getAll());
    
    model = new DefaultTableModel(COLUMNS, 0);
    for (Product product: productList) {
      String[] rowArray = new String[2];
      rowArray[0] = product.getSku();
      if (product.getType().equals("s")){
        rowArray[1] = "Sale Video";
      } else {
        rowArray[1] = "Refreshment";
      }
      model.addRow(rowArray);
    }
    
    return model;
  }
  
  /**
   * Returns product list
   */
  public List<Product> getProductList() {
    return productList;
  }
  
  /**
   * Sets product list
   */
  public void setProductList(List<Product> productList) {
    this.productList = productList;
  }
  
  /**
   * Builds and returns model for "type" combo box
   */
  public DefaultComboBoxModel getTypeModel(){
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    model.addElement("Sale Video");
    model.addElement("Refreshment");
    return model;
  }
  
  /**
   * Returns the index of the given product
   */
  public int getTypeModelIndex(Product product, DefaultComboBoxModel model){
    for (int i=0; i<model.getSize(); i++){
      if(product.getType().equals(model.getElementAt(i))){
        return i;
      }
    }
    return 0;
  }
  
  /**
   * Saves a new product
   */
  public void saveProduct(String sku, Object objType) throws Exception{
    
    Product product = ProductDAO.getInstance().create();
    saveProduct(product, sku, objType);
  }
  
  /**
   * Saves an existing product
   */
  public void saveProduct(Product product, String sku, Object objType) throws Exception{
    
    Connection conn = ConnectionPool.getInstance().get();
    
    // Parameters are objects straight from the combo box model.
    // They are first cast as a ListBoxItem, then the inner object is pulled from them,
    // and finally they are cast as what they really are.
    String type = objType.toString();
    if (type.equals("Sale Video")){
      type = "s";
    } else {
      type = "r";
    }
    
    product.setSku(sku);
    product.setType(type);
    ProductDAO.getInstance().save(product, conn);
    conn.commit();
    
    // Refresh table model
    // Note: we could have just changed/inserted respective rows, but other rows
    // may have been effected in the meantime. It's safer if we just reload.
    display.setDisplayTableModel();
    model.fireTableDataChanged();
  }
  
}
