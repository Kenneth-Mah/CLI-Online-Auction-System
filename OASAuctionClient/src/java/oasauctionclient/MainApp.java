/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasauctionclient;

import ejb.session.stateless.AddressSessionBeanRemote;
import ejb.session.stateless.CreditPackageSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import entity.AddressEntity;
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
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
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
//                    doViewAddressDetails();
                } else if (response == 5) {
//                    doViewAllAddresses();
                } else if (response == 6) {
                    doViewCreditBalance();
                } else if (response == 7) {
                    doViewCreditTransactionHistory();
                } else if (response == 8) {
                    doPurchaseCreditPackage();
                } else if (response == 9) {
//                    doBrowseAllAuctionListings();
                } else if (response == 10) {
//                    doViewAuctionListingDetails();
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
                
                globalCustomerEntity = customerSessionBeanRemote.addAddressToCustomer(newAddressId, globalCustomerEntity.getCustomerId());
            } catch (UnknownPersistenceException ex) {
                System.out.println("An unknown error has occurred while creating the new address!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException | CustomerNotfoundException | AddressNotFoundException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForAddressEntity(constraintViolations);
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
        for(TransactionEntity transactionEntity : transactionEntities){
            
            System.out.println("Trasaction for " + transactionEntity.getTimeOfTransaction());
            System.out.println("Transaction ID: " + transactionEntity.getTransactionid() + "\nAmount: " + transactionEntity.getTransactionAmount());
            
            //To check which type it is , we  need to to check if credit Pacakage/Bid entity is NOT NULL;
            if (transactionEntity.getBid() != null){
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
        
        for (CreditPackageEntity creditPackage:creditPackageEntities){
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
