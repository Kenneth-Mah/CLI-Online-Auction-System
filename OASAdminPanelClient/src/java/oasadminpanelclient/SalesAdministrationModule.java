/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasadminpanelclient;

import ejb.session.stateless.AuctionListingSessionBeanRemote;
import java.util.Scanner;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 *
 * @author yeowh
 */
public class SalesAdministrationModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private AuctionListingSessionBeanRemote auctionListingSessionBeanRemote;

    public SalesAdministrationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public SalesAdministrationModule(AuctionListingSessionBeanRemote auctionListingSessionBeanRemote) {
        this();
        this.auctionListingSessionBeanRemote = auctionListingSessionBeanRemote;
    }
    
    public void menuSalesAdministration() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** OAS Administration Panel :: Sales Administration ***\n");
            System.out.println("1: Create New Auction Listing");
            System.out.println("2: View Auction Listing Details");
            System.out.println("3: View All Auction Listings");
            System.out.println("4: View All Auction Listings with Bids but Below Reserve Price");
            System.out.println("5: Back\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
//                    doCreateNewAuctionListing();
                } else if (response == 2) {
//                    doViewAuctionListingDetails();
                } else if (response == 3) {
//                    doViewAllAuctionListings();
                } else if (response == 4) {
//                    doViewAllAuctionListingsAlt();
                } else if (response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }
    
}
