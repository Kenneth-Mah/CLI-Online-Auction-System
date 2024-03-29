/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasauctionclient;

import ejb.session.stateless.AddressSessionBeanRemote;
import ejb.session.stateless.AuctionListingSessionBeanRemote;
import ejb.session.stateless.BidSessionBeanRemote;
import ejb.session.stateless.CreditPackageSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.TransactionSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author yeowh
 */
public class Main {
    
    @EJB
    private static CustomerSessionBeanRemote customerSessionBeanRemote;
    @EJB
    private static AddressSessionBeanRemote addressSessionBeanRemote;
    @EJB
    private static CreditPackageSessionBeanRemote creditPackageSessionBeanRemote;
    @EJB
    private static TransactionSessionBeanRemote transactionSessionBeanRemote;
    @EJB
    private static AuctionListingSessionBeanRemote auctionListingSessionBeanRemote;
    @EJB
    private static BidSessionBeanRemote bidSessionBeanRemote;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MainApp mainApp = new MainApp(customerSessionBeanRemote, addressSessionBeanRemote, creditPackageSessionBeanRemote, transactionSessionBeanRemote, auctionListingSessionBeanRemote, bidSessionBeanRemote);
        mainApp.runApp();
    }
    
}
