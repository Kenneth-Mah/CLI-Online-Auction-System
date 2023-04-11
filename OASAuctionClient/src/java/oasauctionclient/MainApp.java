/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasauctionclient;

import ejb.session.stateless.AddressSessionBeanRemote;
import ejb.session.stateless.AuctionListingSessionBeanRemote;
import ejb.session.stateless.CreditPackageSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.TransactionSessionBeanRemote;
import entity.AddressEntity;
import entity.AuctionListingEntity;
import entity.BidEntity;
import entity.CreditPackageEntity;
import entity.TransactionEntity;
import entity.CustomerEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AddressNotFoundException;
import util.exception.CustomerNotfoundException;
import util.exception.CustomerUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidBidException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateAddressException;
import util.exception.UpdateCustomerException;

/**
 *
 * @author yeowh
 */
public class MainApp {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private AddressSessionBeanRemote addressSessionBeanRemote;
    private CreditPackageSessionBeanRemote creditPackageSessionBeanRemote;
    private AuctionListingSessionBeanRemote auctionListingSessionBeanRemote;

    private CustomerEntity globalCustomerEntity;

    public MainApp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote, AddressSessionBeanRemote addressSessionBeanRemote, CreditPackageSessionBeanRemote creditPackageSessionBeanRemote) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.addressSessionBeanRemote = addressSessionBeanRemote;
        this.creditPackageSessionBeanRemote = creditPackageSessionBeanRemote;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to OAS Auction Client ***\n");
            System.out.println("1: Login");
            System.out.println("2: Register");
            System.out.println("3: Exit\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");

                        menuCustomer();

                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    doRegister();
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 3) {
                System.out.println("Thank you! Hope to see you again!");
                break;
            }
        }
    }

    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** OAS Auction Client :: Login \n");
        System.out.print("Enter username> ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        String password = scanner.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            globalCustomerEntity = customerSessionBeanRemote.customerLogin(username, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    private void doRegister() {
        Scanner scanner = new Scanner(System.in);
        CustomerEntity newCustomerEntity = new CustomerEntity();

        System.out.println("*** OAS Auction Client :: Register ***\n");
        System.out.print("Enter First Name> ");
        newCustomerEntity.setFirstName(scanner.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newCustomerEntity.setLastName(scanner.nextLine().trim());
        System.out.print("Enter Username> ");
        newCustomerEntity.setUsername(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newCustomerEntity.setPassword(scanner.nextLine().trim());

        Set<ConstraintViolation<CustomerEntity>> constraintViolations = validator.validate(newCustomerEntity);

        if (constraintViolations.isEmpty()) {
            try {
                Long newCustomerId = customerSessionBeanRemote.createNewCustomer(newCustomerEntity);
                System.out.println("New account created successfully!: " + newCustomerId + "\n");
            } catch (CustomerUsernameExistException ex) {
                System.out.println("An error has occurred while creating an account!: The user name already exist\n");
            } catch (UnknownPersistenceException ex) {
                System.out.println("An unknown error has occurred while creating an account!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForCustomerEntity(constraintViolations);
        }
    }

    private void menuCustomer() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** OAS Auction Client ***\n");
            System.out.println("You are login as " + globalCustomerEntity.getFirstName() + " " + globalCustomerEntity.getLastName() + "\n");
            System.out.println("1: View Profile");
            System.out.println("2: Update Profile");
            System.out.println("3: Create Address");
            System.out.println("4: View Address Details");
            System.out.println("5: View All Addresses");
            System.out.println("6: View Credit Balance");
            System.out.println("7: View Credit Transaction History");
            System.out.println("8: Purchase Credit Package");
            System.out.println("9: Browse All Auction Listings");
            System.out.println("10: View Auction Listing Details");
            System.out.println("11: Browse Won Auction Listings");
            System.out.println("12: Logout\n");
            response = 0;

            while (response < 1 || response > 12) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doViewProfile();
                } else if (response == 2) {
                    doUpdateProfile();
                } else if (response == 3) {
                    doCreateAddress();
                } else if (response == 4) {
                    doViewAddressDetails();
                } else if (response == 5) {
                    doViewAllAddresses();
                } else if (response == 6) {
                    doViewCreditBalance();
                } else if (response == 7) {
                    doViewCreditTransactionHistory();
                } else if (response == 8) {
                    doPurchaseCreditPackage();
                } else if (response == 9) {
                    doBrowseAllAuctionListings();
                } else if (response == 10) {
                    doViewAuctionListingDetails();
                } else if (response == 11) {
//                    doBrowseWonAuctionListings();
                } else if (response == 12) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 12) {
                break;
            }
        }
    }

    private void doViewProfile() {
        try {
            CustomerEntity customerEntity = customerSessionBeanRemote.retrieveCustomerByCustomerId(globalCustomerEntity.getCustomerId());

            System.out.println("*** OAS Auction Client :: View Profile ***\n");
            System.out.println("First Name: " + customerEntity.getFirstName());
            System.out.println("Last Name: " + customerEntity.getLastName() + "\n");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (CustomerNotfoundException ex) {
            System.out.println(ex.getMessage() + "\n");
        }
    }

    private void doUpdateProfile() {
        Scanner scanner = new Scanner(System.in);
        String input;

        try {
            CustomerEntity customerEntity = customerSessionBeanRemote.retrieveCustomerByCustomerId(globalCustomerEntity.getCustomerId());

            System.out.println("*** OAS Auction Client :: Update Profile ***\n");
            System.out.println("Enter First Name (blank if no change)>");
            input = scanner.nextLine().trim();
            if (input.length() > 0) {
                customerEntity.setFirstName(input);
            }

            System.out.println("Enter Last Name (blank if no change)>");
            input = scanner.nextLine().trim();
            if (input.length() > 0) {
                customerEntity.setLastName(input);
            }

            Set<ConstraintViolation<CustomerEntity>> constraintViolations = validator.validate(customerEntity);

            if (constraintViolations.isEmpty()) {
                try {
                    customerSessionBeanRemote.updateCustomer(customerEntity);
                    System.out.println("Customer profile updated successfully!\n");

                    globalCustomerEntity = customerSessionBeanRemote.retrieveCustomerByCustomerId(customerEntity.getCustomerId());
                } catch (CustomerNotfoundException | UpdateCustomerException ex) {
                    System.out.println("An error has occurred while updating customer profile: " + ex.getMessage() + "\n");
                } catch (InputDataValidationException ex) {
                    System.out.println(ex.getMessage() + "\n");
                }
            } else {
                showInputDataValidationErrorsForCustomerEntity(constraintViolations);
            }
        } catch (CustomerNotfoundException ex) {
            System.out.println(ex.getMessage() + "\n");
        }
    }

    private void doCreateAddress() {
        Scanner scanner = new Scanner(System.in);
        AddressEntity newAddressEntity = new AddressEntity();

        System.out.println("*** OAS Auction Client :: Create New Address ***\n");
        System.out.print("Enter Address Name> ");
        newAddressEntity.setAddressName(scanner.nextLine().trim());

        Set<ConstraintViolation<AddressEntity>> constraintViolations = validator.validate(newAddressEntity);

        if (constraintViolations.isEmpty()) {
            try {
                Long newAddressId = addressSessionBeanRemote.createNewAddress(newAddressEntity);
                System.out.println("New address created successfully!: " + newAddressId + "\n");

                globalCustomerEntity = customerSessionBeanRemote.addAddressToCustomer(globalCustomerEntity.getCustomerId(), newAddressId);
            } catch (UnknownPersistenceException ex) {
                System.out.println("An unknown error has occurred while creating the new address!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException | CustomerNotfoundException | AddressNotFoundException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForAddressEntity(constraintViolations);
        }
    }

    private void doViewAddressDetails() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        System.out.println("*** OAS Auction Client :: View Address Details ***\n");
        System.out.print("Enter Address ID> ");
        Long addressID = scanner.nextLong();
        scanner.nextLine();

        try {
            AddressEntity addressEntity = customerSessionBeanRemote.retrieveAddressByCustomerIdAndAddressId(globalCustomerEntity.getCustomerId(), addressID);
            System.out.printf("%10s%35s%9s\n", "Address ID", "Address Name", "Active");
            System.out.printf("%10s%35s%9s\n", addressEntity.getAddressId(), addressEntity.getAddressName(), addressEntity.getActive());
            System.out.println("------------------------");
            System.out.println("1: Update Address");
            System.out.println("2: Delete Address");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if (response == 1) {
                if (!addressEntity.getActive()) {
                    System.out.println("This address has been disabled and cannot be modified!");
                } else if (addressSessionBeanRemote.isAddressInUse(addressEntity.getAddressId())) {
                    System.out.println("This address is in use and cannot be modified!");
                } else {
                    doUpdateAddress(addressEntity);
                }
            } else if (response == 2) {
                if (addressEntity.getActive()) {
                    doDeleteAddress(addressEntity);
                } else {
                    System.out.println("This address cannot be removed as it is in use! However, it has already been marked as disabled!");
                }
            }
        } catch (CustomerNotfoundException | AddressNotFoundException ex) {
            System.out.println("An error has occurred while retrieving address: " + ex.getMessage() + "\n");
        }
    }

    private void doUpdateAddress(AddressEntity addressEntity) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Auction Client :: View Address Details :: Update Address ***\n");
        System.out.print("Enter Address Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            addressEntity.setAddressName(input);
        }

        Set<ConstraintViolation<AddressEntity>> constraintViolations = validator.validate(addressEntity);

        if (constraintViolations.isEmpty()) {
            try {
                addressSessionBeanRemote.updateAddress(addressEntity);
                System.out.println("Address updated successfully!\n");
            } catch (AddressNotFoundException | UpdateAddressException ex) {
                System.out.println("An error has occurred while updating address: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForAddressEntity(constraintViolations);
        }
    }

    private void doDeleteAddress(AddressEntity addressEntity) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Auction Client :: View Address Details :: Delete Address ***\n");
        System.out.printf("Confirm Delete Address %s (Address ID: %d) (Enter 'Y' to Delete)> ", addressEntity.getAddressName(), addressEntity.getAddressId());
        input = scanner.nextLine().trim();

        if (input.equals("Y")) {
            try {
                if (!addressSessionBeanRemote.isAddressInUse(addressEntity.getAddressId())) {
                    customerSessionBeanRemote.deleteCustomerAddress(globalCustomerEntity.getCustomerId(), addressEntity.getAddressId());
                    System.out.println("Address deleted successfully!\n");
                } else {
                    customerSessionBeanRemote.deleteCustomerAddress(globalCustomerEntity.getCustomerId(), addressEntity.getAddressId());
                    System.out.println("Address is in use and cannot be removed! However, it has been disabled successfully!");
                }
            } catch (CustomerNotfoundException | AddressNotFoundException ex) {
                System.out.println("An error has occurred while deleting the address: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Address NOT deleted!\n");
        }
    }

    private void doViewAllAddresses() {
        try {
            List<AddressEntity> addressEntities = customerSessionBeanRemote.retrieveAllAddressesByCustomerId(globalCustomerEntity.getCustomerId());

            Scanner scanner = new Scanner(System.in);
            System.out.println("*** OAS Auction Client :: View All Addresses ***\n");
            System.out.printf("%10s%35s%9s\n", "Address ID", "Address Name", "Active");

            for (AddressEntity addressEntity : addressEntities) {
                System.out.printf("%10s%35s%9s\n", addressEntity.getAddressId(), addressEntity.getAddressName(), addressEntity.getActive());
            }

            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (CustomerNotfoundException ex) {
            System.out.println(ex.getMessage() + "\n");
        }
    }

    private void doViewCreditBalance() {
        try {
            globalCustomerEntity = customerSessionBeanRemote.retrieveCustomerByCustomerId(globalCustomerEntity.getCustomerId());

            System.out.println("*** OAS Auction Client :: View Credit Balance ***\n");
            System.out.println("Credit Balance: " + globalCustomerEntity.getAvailableBalance() + "\n");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (CustomerNotfoundException ex) {
            System.out.println(ex.getMessage() + "\n");
        }
    }

    private void doViewCreditTransactionHistory() {
        System.out.println("*** OAS Auction Client :: View Credit Transaction History ***\n");

        List<TransactionEntity> transactionEntities = globalCustomerEntity.getTransactions();
        //System.out.println(customerSessionBeanRemote.getTransHist()); //must make it print line by line per transaction
        for (TransactionEntity transactionEntity : transactionEntities) {

            System.out.println("Trasaction for " + transactionEntity.getTimeOfTransaction());
            System.out.println("Transaction ID: " + transactionEntity.getTransactionid() + "\nAmount: " + transactionEntity.getTransactionAmount());

            //To check which type it is , we  need to to check if credit Pacakage/Bid entity is NOT NULL;
            if (transactionEntity.getBid() != null) {
                if (transactionEntity.getTransactionAmount().compareTo(BigDecimal.ZERO) < 0) { // NEGATIVE MEANS CUSTOMER SPEAND MONEY TO BID
                    System.out.println("Transaction Type [BID]: " + transactionEntity.getTransactionAmount());
                } else {// possitive == refund
                    System.out.println("Transaction Type [REFUND]: " + transactionEntity.getTransactionAmount());
                }
            } else {
                System.out.println("Transaction Type [PURCHASE CREDIT PACKAGE]: " + transactionEntity.getTransactionAmount());
                System.out.println("Price: " + transactionEntity.getCreditPackage().getCreditPrice() + " per unit");
            }
        }
    }

    private void doPurchaseCreditPackage() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Current available credit package ***\n");
        List<CreditPackageEntity> creditPackageEntities = creditPackageSessionBeanRemote.retrieveAllAvailableCreditPackages();

        for (CreditPackageEntity creditPackage : creditPackageEntities) {
            System.out.println("Credit Packages for " + creditPackage.getCreditPackageType() + " type, credit price: " + creditPackage.getCreditPrice());
        }

        System.out.println("Choose type of credit package to purchase: ");
        System.out.println("or type 'EXIT' to exit");

        String reply = scanner.nextLine().trim().toUpperCase();
//        if (!reply.equals("EXIT")) {
//        Query query = em.createQuery("SELECT c FROM CreditPackageEntity c WHERE c.creditPackageType = :type");
//        query.setParameter("type", reply);
//        TransactionEntity purchaseCredittransaction = new TransactionEntity(credit);
//        }
    }

    private void doBrowseAllAuctionListings() {
        System.out.println("*** Browse All Available Auction Listings***");
        List<AuctionListingEntity> autionListingEntities = auctionListingSessionBeanRemote.retrieveAllAvailableAuctionListing();
        for (AuctionListingEntity auctionListing : autionListingEntities) {
            System.out.println("Auction Listing for " + auctionListing.getAuctionListingName() + ", credit price: " + auctionListing.getHighestBidPrice());
        }
        System.out.println("These are the available Auction. To view more detail, select '10'");
    }

    private void doViewAuctionListingDetails(){
        Scanner scanner = new Scanner(System.in);

        while (true) {

            doBrowseAllAuctionListings();
            System.out.println("*** View Auction Listing detail ***");
            System.out.println("*** Please key in Available Auction Name ***");

            String reply = scanner.nextLine().trim();
            AuctionListingEntity autionListingEntities = auctionListingSessionBeanRemote.retrieveAuctionListingViaName(reply);
            System.out.println("Name" + autionListingEntities.getAuctionListingName());
            System.out.println("Start Date and Time" + autionListingEntities.getStartDateTime());
            System.out.println("End Date and Time" + autionListingEntities.getEndDateTime());
            System.out.println("Highest Bidder" + autionListingEntities.getHighestBidPrice());

            System.out.println("\nWould you like to: ");
            System.out.println("1: Place Bid");
            System.out.println("2: Refresh Auction Listing Bids");
            System.out.println("3: Back");

            int response = scanner.nextInt();

            while (response < 1 || response > 3) {

                if (response == 1) {
                    System.out.println("Place your bid");
                    BigDecimal bidPrice = scanner.nextBigDecimal();
                    BigDecimal min = new BigDecimal(0.00);
                    BigDecimal highestBid = autionListingEntities.getHighestBidPrice();
                    if (highestBid.compareTo(new BigDecimal(0.01)) == 1 && highestBid.compareTo(new BigDecimal(0.99)) == -1) {
                        min = new BigDecimal(0.05);
                    } else if (highestBid.compareTo(new BigDecimal(1.00)) == 1 && highestBid.compareTo(new BigDecimal(4.99)) == -1) {
                        min = new BigDecimal(0.25);
                    } else if (highestBid.compareTo(new BigDecimal(5.00)) == 1 && highestBid.compareTo(new BigDecimal(24.99)) == -1) {
                        min = new BigDecimal(0.50);
                    } else if (highestBid.compareTo(new BigDecimal(25.00)) == 1 && highestBid.compareTo(new BigDecimal(99.99)) == -1) {
                        min = new BigDecimal(1.00);
                    } else if (highestBid.compareTo(new BigDecimal(100.00)) == 1 && highestBid.compareTo(new BigDecimal(249.99)) == -1) {
                        min = new BigDecimal(2.50);
                    } else if (highestBid.compareTo(new BigDecimal(250.00)) == 1 && highestBid.compareTo(new BigDecimal(499.99)) == -1) {
                        min = new BigDecimal(5.00);
                    } else if (highestBid.compareTo(new BigDecimal(500.00)) == 1 && highestBid.compareTo(new BigDecimal(999.99)) == -1) {
                        min = new BigDecimal(10.00);
                    } else if (highestBid.compareTo(new BigDecimal(1000.00)) == 1 && highestBid.compareTo(new BigDecimal(2499.99)) == -1) {
                        min = new BigDecimal(25.00);
                    } else if (highestBid.compareTo(new BigDecimal(2500.00)) == 1 && highestBid.compareTo(new BigDecimal(4999.99)) == -1) {
                        min = new BigDecimal(50.00);
                    } else if (highestBid.compareTo(new BigDecimal(5000.00)) == 1) {
                        min = new BigDecimal(100.00);
                    }

                    if (bidPrice.compareTo(autionListingEntities.getHighestBidPrice()) == 1 && !(bidPrice.compareTo(min) == -1)) {
                        BidEntity bid = new BidEntity(bidPrice, globalCustomerEntity, autionListingEntities);// might be wrong
                        System.out.println("new bid created");
                    } else {
                        System.out.println("Bid Price is not higher than the current highest bid, highest bidder price " + autionListingEntities.getHighestBidPrice() + "\n or Price increment is not the right! \nRefer to price bidding increment table");
                    }
                } else if (response == 2) {
                    autionListingEntities = auctionListingSessionBeanRemote.retrieveAuctionListingViaName(reply);
                    System.out.println("Name" + autionListingEntities.getAuctionListingName());
                    System.out.println("Start Date and Time" + autionListingEntities.getStartDateTime());
                    System.out.println("End Date and Time" + autionListingEntities.getEndDateTime());
                    System.out.println("Highest Bidder" + autionListingEntities.getHighestBidPrice());
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 3) {
                break;
            }
        }
    }

    private void showInputDataValidationErrorsForCustomerEntity(Set<ConstraintViolation<CustomerEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

    private void showInputDataValidationErrorsForAddressEntity(Set<ConstraintViolation<AddressEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
