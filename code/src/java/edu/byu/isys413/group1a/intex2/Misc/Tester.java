package edu.byu.isys413.group1a.intex2.Misc;

import edu.byu.isys413.group1a.intex2.GUIs.GUIReturnsTransaction;

/**
 * A simple tester class for my data layer.
 *
 * @author Group 1A
 */
public class Tester {
    
    public static void main(String args[]) {
        try{
            /**
             * VCRT vcrt = VCRTDAO.getInstance().read("00000109478ac33719870c150010004700cf00c1");
             * //System.out.println(vcrt.getVideoCategory().getCategory());
             *
             * VCRTCV vcrtcv = VCRTCVDAO.getInstance().read("000001094790dec1ba1c9b340010009200980018");
             * //System.out.println(vcrtcv.getCv().getTitle());
             *
             * RentalVideo rv = RentalVideoDAO.getInstance().read("000001094796817effd527550010000b00f20066");
             * System.out.println(rv.getVcrtcv().getVcrt().getVideoCategory().getCategory());
             *
             * GUID guid = new GUID();
             * for (int i=0; i<6; i++) System.out.println(guid.generate());
             **/
            /**
       
      Tx tx = TxDAO.getInstance().create();
      //Customer c = CustomerDAO.getInstance().read("0000010942eca52437fa4012001000ac00e900c6");
      //System.out.println(c.getAccount().getBalance());
       
      tx.setCustomer(CustomerDAO.getInstance().read("0000010942eca52437fa4012001000ac00e900c6"));
      tx.setStore(StoreDAO.getInstance().read("0000010942ed61aeb33d203e001000ac00e900c6"));
      long time = Long.parseLong("1139461619651");
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(time);
      tx.setDate(cal);
      tx.setTax(12);
      tx.setTotal(50);
       
      TxLine txLine1 = TxLineDAO.getInstance().create();
      txLine1.setQuantity(10);
       
        Rental rental = RentalDAO.getInstance().create();
          RentalVideo rentalVideo = RentalVideoDAO.getInstance().read("10000001");
        rental.setRentalVideo(rentalVideo);
        rental.setOutDate(cal);
        rental.setDueDate(cal);
        rental.setInDate(cal);
       
       
      //txLine1.setRevenueSource(RentalDAO.getInstance().read())
      txLine1.setRevenueSource(rental);
       
      tx.addTxLine(txLine1);
       
      TxLine txLine2 = TxLineDAO.getInstance().create();
      txLine2.setQuantity(7);
      txLine2.setRevenueSource(MembershipDAO.getInstance().read("0000010942ed66ceeebfdc44001000ac00e900c6"));
       
      tx.addTxLine(txLine2);
       
       
      //for (Iterator iter = tx.getTxLines().iterator(); iter.hasNext(); ) {
        //TxLine txLine = (TxLine)iter.next();
        //TxLineDAO.getInstance().save(txLine);
      //}
       
      List txLineList = tx.getTxLines();
      int i = 0;
      for (Iterator iter = txLineList.iterator(); iter.hasNext();){
        TxLine txLine = (TxLine)iter.next();
        TxLineDAO.getInstance().save(txLine);
        i++;
      }
       
      //TxDAO.getInstance().save(tx);
       
      /** calendar stuff
             *
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(time);
      System.out.println(cal.get(cal.YEAR));
      System.out.println(System.currentTimeMillis());
             *
             */
            
            
            /**
      MainFrameTransaction m = new MainFrameTransaction();
      m.setVisible(true);
       
    } catch (Exception e){
      e.printStackTrace();
    }*/
            //GUIReturnRental g = new GUIReturnRental();
            //g.setVisible(true);
            
            GUIReturnsTransaction grt = new GUIReturnsTransaction();
            grt.setVisible(true);
            
            //GUIDBMRentalVideoDisplay gdbmrvd = new GUIDBMRentalVideoDisplay();
            //gdbmrvd.setVisible(true);
            
            //GUIDBMStoreDisplay sd = new GUIDBMStoreDisplay();
            //sd.setVisible(true);
          
            //GUIDBMConceptualVideoDisplay cvd = new GUIDBMConceptualVideoDisplay();
            //cvd.setVisible(true);
          
            //GUIDBMProductDisplay pd = new GUIDBMProductDisplay();
            //pd.setVisible(true);
            
            //GUIAcctManagement gam = new GUIAcctManagement();
            //gam.setVisible(true);
            
            //ReturnVideo rv = new ReturnVideo();
            //rv.setVisible(true);
            //ReturnVideo rv = new ReturnVideo();
            //rv.setVisible(true);
        } catch (Exception e){
            e.printStackTrace();
        }
        
        /**
    try {
     
      // clear out the database (you'd never do this in production)
      Connection conn = ConnectionPool.getInstance().get();
      Statement stmt = conn.createStatement();
      stmt.executeUpdate("DELETE FROM customer");
      stmt.executeUpdate("DELETE FROM account");
      stmt.close();
      conn.commit();
      ConnectionPool.getInstance().release(conn);
     
      // create a test account
      Account acct = AccountDAO.getInstance().create();
      acct.setCcName("Conan C. Albrecht");
      acct.setCcNum("1234-5678-9012-3456");
      acct.setCcExpMonth(5);
      acct.setCcExpMonth(2005);
      AccountDAO.getInstance().save(acct);
     
      // test that the exact same object will come out of the cache if asked for
      Account acct2 = AccountDAO.getInstance().read(acct.getId());
      System.out.println("acct==acct2 -> " + (acct == acct2));
     
      // create a customer
      Customer cust = CustomerDAO.getInstance().create();
      cust.setFirstName("Homer");
      cust.setLastName("Simpson");
      cust.setAddress("742 Evergreen Terrace");
      cust.setCity("Springfield");
      cust.setState("Unknown");
      cust.setZipCode("84235");
      cust.setPhone("801-224-0233");
      acct.addCustomer(cust);
      CustomerDAO.getInstance().save(cust);
      AccountDAO.getInstance().save(acct);
     
      // create a second customer
      Customer cust2 = CustomerDAO.getInstance().create();
      cust2.setFirstName("Marge");
      cust2.setLastName("Simpson");
      cust2.setAddress("742 Evergree Terrace");
      cust2.setCity("Unknown");
      cust2.setZipCode("84602");
      cust2.setPhone("801-224-0233");
      acct.addCustomer(cust2);
      CustomerDAO.getInstance().save(cust2);
      acct.setOwner(cust2);
      AccountDAO.getInstance().save(acct);
     
      // get the customers on the account
      System.out.println("Customers on account: " + acct.getCustomers());
      System.out.println("Customer account is: " + cust.getAccount());
     
      // reload everything from scratch to test whether the read methods work
      // this essentially is like restarting the entire system since
      // we're going to clear the cache out
      Cache.getInstance().clear(); // you'd never clear the cache in production (we do so for testing)
      Account acct3 = AccountDAO.getInstance().read(acct.getId());
      System.out.println("Account CC is: " + acct3.getCcName());
      System.out.println("Customers are: " + acct3.getCustomers());
      System.out.println("Acct Owner is: " + acct3.getOwner());
     
    }catch(Exception e) {
      e.printStackTrace();
    }*/
    }//main
}//class
