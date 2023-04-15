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
import entity.AddressEntity;
import entity.AuctionListingEntity;
import entity.BidEntity;
import entity.CreditPackageEntity;
import entity.TransactionEntity;
import entity.CustomerEntity;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AddressNotFoundException;
import util.exception.AuctionListingAlreadyClosedException;
import util.exception.AuctionListingNotFoundException;
import util.exception.CreditPackageNotFoundException;
import util.exception.CustomerNotfoundException;
import util.exception.CustomerUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InsufficientCreditException;
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

    private DecimalFormat decimalFormat;

    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private AddressSessionBeanRemote addressSessionBeanRemote;
    private CreditPackageSessionBeanRemote creditPackageSessionBeanRemote;
    private TransactionSessionBeanRemote transactionSessionBeanRemote;
    private AuctionListingSessionBeanRemote auctionListingSessionBeanRemote;
    private BidSessionBeanRemote bidSessionBeanRemote;

    private CustomerEntity globalCustomerEntity;

    public MainApp() {
        decimalFormat = new DecimalFormat("#0.0000");
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote, AddressSessionBeanRemote addressSessionBeanRemote, CreditPackageSessionBeanRemote creditPackageSessionBeanRemote, TransactionSessionBeanRemote transactionSessionBeanRemote, AuctionListingSessionBeanRemote auctionListingSessionBeanRemote, BidSessionBeanRemote bidSessionBeanRemote) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.addressSessionBeanRemote = addressSessionBeanRemote;
        this.creditPackageSessionBeanRemote = creditPackageSessionBeanRemote;
        this.transactionSessionBeanRemote = transactionSessionBeanRemote;
        this.auctionListingSessionBeanRemote = auctionListingSessionBeanRemote;
        this.bidSessionBeanRemote = bidSessionBeanRemote;
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
                    doBrowseWonAuctionListings();
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
            System.out.println("Enter First Name (blank if no change)> ");
            input = scanner.nextLine().trim();
            if (input.length() > 0) {
                customerEntity.setFirstName(input);
            }

            System.out.println("Enter Last Name (blank if no change)> ");
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
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** OAS Auction Client :: View Credit Transaction History ***\n");

        List<TransactionEntity> transactionEntities = transactionSessionBeanRemote.retrieveAllTransactionsByCustomerId(globalCustomerEntity.getCustomerId());
        Collections.sort(transactionEntities);
        System.out.printf("%14s%21s%31s\n", "Transaction ID", "Transaction Amount", "Time Of Transaction");

        for (TransactionEntity transactionEntity : transactionEntities) {
            System.out.printf("%14s%21s%31s\n", transactionEntity.getTransactionid(), decimalFormat.format(transactionEntity.getTransactionAmount()), transactionEntity.getTimeOfTransaction());
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void doPurchaseCreditPackage() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** OAS Auction Client :: Purchase Credit Package ***\n");

        List<CreditPackageEntity> creditPackageEntities = creditPackageSessionBeanRemote.retrieveAllAvailableCreditPackages();
        System.out.printf("%17s%22s%21s\n", "Credit Package ID", "Credit Package Type", "Credit Price");

        for (CreditPackageEntity creditPackageEntity : creditPackageEntities) {
            System.out.printf("%17s%22s%21s\n", creditPackageEntity.getCreditPackageId(), creditPackageEntity.getCreditPackageType(), decimalFormat.format(creditPackageEntity.getCreditPrice()));
        }

        System.out.print("Enter Credit Package Type To Purchase (blank to exit)> ");
        String creditPackageType = scanner.nextLine().trim();
        if (creditPackageType.length() > 0) {
            try {
                CreditPackageEntity creditPackageEntity = creditPackageSessionBeanRemote.retrieveCreditPackageByCreditPackageType(creditPackageType);
                TransactionEntity newTransactionEntity = new TransactionEntity();
                newTransactionEntity.setTimeOfTransaction(new Date());

                System.out.print("Enter Quantity Of This Credit Package Type To Purchase> ");
                Integer integerInput = scanner.nextInt();

                newTransactionEntity.setQuantity(integerInput);
                newTransactionEntity.setTransactionAmount(creditPackageEntity.getCreditPrice().multiply(BigDecimal.valueOf(integerInput)));
                newTransactionEntity.setCustomer(globalCustomerEntity);
                newTransactionEntity.setCreditPackage(creditPackageEntity);

                try {
                    Long newTransactionId = transactionSessionBeanRemote.createNewTransaction(globalCustomerEntity.getCustomerId(), newTransactionEntity);
                    System.out.println("Credit package purchased successfully!: " + newTransactionId + "\n");
                } catch (CustomerNotfoundException | UnknownPersistenceException ex) {
                    System.out.println("An unknown error has occurred while purchasing the credit package!: " + ex.getMessage() + "\n");
                } catch (InputDataValidationException ex) {
                    System.out.println(ex.getMessage() + "\n");
                }
            } catch (CreditPackageNotFoundException ex) {
                System.out.println("An error has occurred while retrieving credit package: " + ex.getMessage() + "\n");
            }
        }
    }

    private void doBrowseAllAuctionListings() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** OAS Auction Client :: Browse All Auction Listings ***\n");

        List<AuctionListingEntity> autionListingEntities = auctionListingSessionBeanRemote.retrieveAllActiveAuctionListings();
        System.out.printf("%18s%26s%34s%34s%20s%20s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price");

        for (AuctionListingEntity auctionListingEntity : autionListingEntities) {
            String reservePriceString;
            if (auctionListingEntity.getReservePrice() != null) {
                reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
            } else {
                reservePriceString = "null";
            }
            System.out.printf("%18s%26s%34s%34s%20s%20s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toString(), auctionListingEntity.getEndDateTime().toString(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()));
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void doViewAuctionListingDetails() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** OAS Auction Client :: View Auction Listing Details ***\n");
        System.out.print("Enter Auction Listing Name> ");
        String auctionListingName = scanner.nextLine().trim();

        doViewAuctionListingDetails(auctionListingName);
    }

    private void doViewAuctionListingDetails(String auctionListingName) {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        try {
            AuctionListingEntity auctionListingEntity = auctionListingSessionBeanRemote.retrieveAuctionListingByAuctionListingName(auctionListingName);
            System.out.printf("%18s%26s%34s%34s%20s%20s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price");
            String reservePriceString;
            if (auctionListingEntity.getReservePrice() != null) {
                reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
            } else {
                reservePriceString = "null";
            }
            System.out.printf("%18s%26s%34s%34s%20s%20s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toString(), auctionListingEntity.getEndDateTime().toString(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()));
            System.out.println("------------------------");
            System.out.println("1: Place New Bid");
            System.out.println("2: Refresh Auction Listing Bids");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if (response == 1) {
                doPlaceNewBid(auctionListingEntity);
            } else if (response == 2) {
                doViewAuctionListingDetails(auctionListingName);
            }
        } catch (AuctionListingNotFoundException ex) {
            System.out.println("An error has occurred while retrieving auction listing: " + ex.getMessage() + "\n");
        }
    }

    private void doPlaceNewBid(AuctionListingEntity auctionListingEntity) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** OAS Auction Client :: Place New Bid ***\n");
        System.out.print("Enter Bid Price> ");
        BigDecimal bidPrice = scanner.nextBigDecimal();

        BigDecimal minBidIncrement;
        BigDecimal currentHighestBidPrice = auctionListingEntity.getHighestBidPrice();
        if (currentHighestBidPrice.compareTo(new BigDecimal("0.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("0.99")) <= 0) {
            minBidIncrement = new BigDecimal("0.05");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("1.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("4.99")) <= -1) {
            minBidIncrement = new BigDecimal("0.25");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("5.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("24.99")) <= -1) {
            minBidIncrement = new BigDecimal("0.50");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("25.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("99.99")) <= -1) {
            minBidIncrement = new BigDecimal("1.00");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("100.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("249.99")) <= -1) {
            minBidIncrement = new BigDecimal("2.50");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("250.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("499.99")) <= -1) {
            minBidIncrement = new BigDecimal("5.00");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("500.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("999.99")) <= -1) {
            minBidIncrement = new BigDecimal("10.00");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("1000.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("2499.99")) <= -1) {
            minBidIncrement = new BigDecimal("25.00");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("2500.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("4999.99")) <= -1) {
            minBidIncrement = new BigDecimal("50.00");
        } else { // currentHighestBidPrice.compareTo(new BigDecimal("5000.00")) >= 0
            minBidIncrement = new BigDecimal("100.00");
        }

        BigDecimal minBidPrice = currentHighestBidPrice.add(minBidIncrement);
        if (bidPrice.compareTo(minBidPrice) >= 0) {

            BidEntity newBidEntity = new BidEntity(bidPrice, globalCustomerEntity, auctionListingEntity);// might be wrong
            try {
                Long newTransactionId = bidSessionBeanRemote.createNewBid(globalCustomerEntity.getCustomerId(), auctionListingEntity.getAuctionListingId(), newBidEntity);
                System.out.println("New bid placed successfully!: " + newTransactionId + "\n");
            } catch (CustomerNotfoundException | AuctionListingNotFoundException | UnknownPersistenceException ex) {
                System.out.println("An unknown error has occurred while placing a new bid!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            } catch (InsufficientCreditException ex) {
                System.out.println("You do not have enough balancce in your wallet." + ex.getMessage() + "\n");
            } catch (AuctionListingAlreadyClosedException ex) {
                System.out.println("Auction Listing already closed." + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Bid price is too small! The minimum bid price is " + minBidPrice + "\n");
        }
    }

    private void doBrowseWonAuctionListings() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** OAS Auction Client :: Browse Won Auction Listings ***\n");

        try {
            List<AuctionListingEntity> autionListingEntities = customerSessionBeanRemote.retrieveWonAuctionsByCustomerId(globalCustomerEntity.getCustomerId());
            System.out.printf("%18s%26s%34s%34s%20s%20s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price");

            for (AuctionListingEntity auctionListingEntity : autionListingEntities) {
                String reservePriceString;
                if (auctionListingEntity.getReservePrice() != null) {
                    reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
                } else {
                    reservePriceString = "null";
                }
                System.out.printf("%18s%26s%34s%34s%20s%20s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toString(), auctionListingEntity.getEndDateTime().toString(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()));
            }

            System.out.print("To Select Delivery Address For A Won Auction Listing, Enter Auction Listing Name (blank to exit)> ");
            String auctionListingName = scanner.nextLine().trim();

            if (auctionListingName.length() > 0) {
                doSelectDeliveryAddressForWonAuctionListing(auctionListingName);
            }
        } catch (CustomerNotfoundException ex) {
            System.out.println("An error has occurred while retrieving won auction listings: " + ex.getMessage() + "\n");
        }
    }

    private void doSelectDeliveryAddressForWonAuctionListing(String auctionListingName) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** OAS Auction Client :: Select Delivery Address For Won Auction Listing ***\n");

        try {
            AuctionListingEntity auctionListingEntity = auctionListingSessionBeanRemote.retrieveAuctionListingByAuctionListingName(auctionListingName);
            System.out.printf("%18s%26s%34s%34s%20s%20s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price");
            String reservePriceString;
            if (auctionListingEntity.getReservePrice() != null) {
                reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
            } else {
                reservePriceString = "null";
            }
            System.out.printf("%18s%26s%34s%34s%20s%20s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toString(), auctionListingEntity.getEndDateTime().toString(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()));
            System.out.println("------------------------");
            System.out.print("Enter Address ID (zero or negative number to exit)> ");
            Long addressId = scanner.nextLong();
            if (addressId > 0) {
                AddressEntity addressEntity = customerSessionBeanRemote.retrieveAddressByCustomerIdAndAddressId(globalCustomerEntity.getCustomerId(), addressId);
                addressSessionBeanRemote.selectDeliveryAddressForWonAuctionListing(addressEntity.getAddressId(), auctionListingEntity.getAuctionListingId());

                System.out.println("Address set successfully!\n");
            }
        } catch (AuctionListingNotFoundException | CustomerNotfoundException | AddressNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the auction listing: " + ex.getMessage() + "\n");
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
