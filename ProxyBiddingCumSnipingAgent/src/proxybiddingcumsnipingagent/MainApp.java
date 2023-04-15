/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxybiddingcumsnipingagent;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;
import ws.soap.customer.AuctionListingEntity;
import ws.soap.customer.AuctionListingNotFoundException_Exception;
import ws.soap.customer.BidEntity;
import ws.soap.customer.BidTypeEnum;
import ws.soap.customer.CustomerEntity;
import ws.soap.customer.CustomerNotfoundException_Exception;
import ws.soap.customer.CustomerWebService;
import ws.soap.customer.InvalidLoginCredentialException_Exception;
import ws.soap.customer.InvalidPremiumRegistrationException_Exception;

/**
 *
 * @author kenne
 */
public class MainApp {
    
    private DecimalFormat decimalFormat;

    private CustomerWebService port;
    
    private CustomerEntity globalCustomerEntity;

    public MainApp() {
        decimalFormat = new DecimalFormat("#0.0000");
    }

    public MainApp(CustomerWebService port) {
        this();
        this.port = port;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to Proxy Bidding cum Sniping Agent ***\n");
            System.out.println("1: Login");
            System.out.println("2: Register");
            System.out.println("3: Exit\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doRemoteLogin();
                        System.out.println("Login successful!\n");

                         menuRemoteCustomer();
                    } catch (InvalidLoginCredentialException_Exception ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    doPremiumRegister();
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 3) {
                System.out.println("Thank you! Hope to see you again!");
                
                // Need to cancel sniping for auction listing here
                
                break;
            }
        }
    }

    private void doRemoteLogin() throws InvalidLoginCredentialException_Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Proxy Bidding cum Sniping Agent :: Login \n");
        System.out.print("Enter username> ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        String password = scanner.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            globalCustomerEntity = port.customerRemoteLogin(username, password);
        } else {
            System.out.println("Missing login credential!\n");
        }
    }

    private void doPremiumRegister() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Proxy Bidding cum Sniping Agent :: Register ***\n");
        System.out.print("Enter Username> ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        String password = scanner.nextLine().trim();

        try {
            port.customerPremiumRegistration(username, password);
            System.out.println("Account registered successfully!\n");
        } catch (InvalidPremiumRegistrationException_Exception ex) {
            System.out.println("An error has occurred while registering an account!: " + ex.getMessage() + "\n");
        }
    }
    
    private void menuRemoteCustomer() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Proxy Bidding cum Sniping Agent ***\n");
            System.out.println("You are login as " + globalCustomerEntity.getFirstName() + " " + globalCustomerEntity.getLastName() + "\n");
            System.out.println("1: Remote View Credit Balance");
            System.out.println("2: Remote View Auction Listing Details");
            System.out.println("3: Remote Browse All Auction Listings");
            System.out.println("4: Remote View Won Auction Listings");
            System.out.println("5: Logout\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doRemoteViewCreditBalance();
                } else if (response == 2) {
                    doRemoteViewAuctionListingDetails();
                } else if (response == 3) {
                    doRemoteBrowseAllAuctionListings();
                } else if (response == 4) {
                    doRemoteViewWonAuctionListings();
                } else if (response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 5) {
                break;
            }
        }
    }
    
    private void doRemoteViewCreditBalance() {
        try {
            globalCustomerEntity = port.retrieveCustomerByCustomerId(globalCustomerEntity.getCustomerId());
            
            System.out.println("*** Proxy Bidding cum Sniping Agent :: Remote View Credit Balance ***\n");
            System.out.println("Credit Balance: " + globalCustomerEntity.getAvailableBalance() + "\n");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (CustomerNotfoundException_Exception ex) {
            System.out.println(ex.getMessage() + "\n");
        }
    }
    
    private void doRemoteViewAuctionListingDetails() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        System.out.println("*** Proxy Bidding cum Sniping Agent :: Remote View Auction Listing Details ***\n");
        System.out.print("Enter Auction Listing Name> ");
        String auctionListingName = scanner.nextLine().trim();
        
        try {
            AuctionListingEntity auctionListingEntity = port.retrieveAuctionListingByAuctionListingName(auctionListingName);
            System.out.printf("%18s%26s%34s%34s%20s%20s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price");
            String reservePriceString;
            if (auctionListingEntity.getReservePrice() != null) {
                reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
            } else {
                reservePriceString = "null";
            }
            System.out.printf("%18s%26s%34s%34s%20s%20s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toGregorianCalendar().getTime(), auctionListingEntity.getEndDateTime().toGregorianCalendar().getTime(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()));
            System.out.println("------------------------");
            System.out.println("1: Configure Proxy Bidding For Auction Listing");
            System.out.println("2: Configure Sniping For Auction Listing");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if (response == 1) {
                doConfigureProxyBiddingForAuctionListing(auctionListingEntity);
            } else if (response == 2) {
                doConfigureSnipingForAuctionListing(auctionListingEntity);
            }
        } catch (AuctionListingNotFoundException_Exception ex) {
            System.out.println("An error has occurred while retrieving auction listing: " + ex.getMessage() + "\n");
        }
    }
    
    private void doConfigureProxyBiddingForAuctionListing(AuctionListingEntity auctionListingEntity) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Proxy Bidding cum Sniping Agent :: Configure Proxy Bidding For Auction Listing ***\n");
        System.out.print("Enter Maximum Bid Price> ");
        BigDecimal maxBidPrice = scanner.nextBigDecimal();
        
        
    }
    
    private void doConfigureSnipingForAuctionListing(AuctionListingEntity auctionListingEntity) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Proxy Bidding cum Sniping Agent :: Configure Sniping For Auction Listing ***\n");
        System.out.print("Enter Time Before Listing End Date-time To Place A One-time Highest Bid (in minutes)> ");
        Integer minutesBeforeEndDateTime = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Maximum Bid Price> ");
        BigDecimal maxBidPrice = scanner.nextBigDecimal();
        
        BidEntity newSnipingBidEntity = new BidEntity();
        newSnipingBidEntity.setBidPrice(maxBidPrice);
        newSnipingBidEntity.setBidTypeEnum(BidTypeEnum.SNIPINGBIDREFERENCE);
        newSnipingBidEntity.setCustomer(globalCustomerEntity);
        newSnipingBidEntity.setAuctionListing(auctionListingEntity);
        
        port.createNewSnipingBid(newSnipingBidEntity, minutesBeforeEndDateTime);
    }
    
    private void doRemoteBrowseAllAuctionListings() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** Proxy Bidding cum Sniping Agent :: Remote Browse All Auction Listings ***\n");
        
        List<AuctionListingEntity> autionListingEntities = port.retrieveAllActiveAuctionListings();
        System.out.printf("%18s%26s%34s%34s%20s%20s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price");
        
        for (AuctionListingEntity auctionListingEntity : autionListingEntities) {
            String reservePriceString;
            if (auctionListingEntity.getReservePrice() != null) {
                reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
            } else {
                reservePriceString = "null";
            }
            System.out.printf("%18s%26s%34s%34s%20s%20s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toGregorianCalendar().getTime(), auctionListingEntity.getEndDateTime().toGregorianCalendar().getTime(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()));
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doRemoteViewWonAuctionListings() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** Proxy Bidding cum Sniping Agent :: Remote View Won Auction Listings ***\n");
        
        try {
            globalCustomerEntity = port.retrieveCustomerByCustomerId(globalCustomerEntity.getCustomerId());
            List<AuctionListingEntity> autionListingEntities = globalCustomerEntity.getWonAuctions();
            System.out.printf("%18s%26s%34s%34s%20s%20s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price");

            for (AuctionListingEntity auctionListingEntity : autionListingEntities) {
                String reservePriceString;
                if (auctionListingEntity.getReservePrice() != null) {
                    reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
                } else {
                    reservePriceString = "null";
                }
                System.out.printf("%18s%26s%34s%34s%20s%20s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toGregorianCalendar().getTime(), auctionListingEntity.getEndDateTime().toGregorianCalendar().getTime(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()));
            }
            
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (CustomerNotfoundException_Exception ex) {
            System.out.println("An error has occurred while retrieving won auction listings: " + ex.getMessage() + "\n");
        }
    }

}
