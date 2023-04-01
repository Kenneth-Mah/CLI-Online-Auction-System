/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasauctionclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import entity.CreditPackageEntity;
import entity.TransactionEntity;
import entity.CustomerEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import javax.management.Query;
import util.exception.CustomerAlreadyExistException;
import util.exception.InvalidCredentialException;
import util.exception.PasswordOrUsernameWrong;

/**
 *
 * @author yeowh
 */
public class MainApp {

    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private CustomerEntity customer;

    public MainApp() {
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to CrazyBids.com ***\n");
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

                    } catch (PasswordOrUsernameWrong | InvalidCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    try {
                        doRegisteration();
                        System.out.println("Details registered! You can now login! \n");

                    } catch (CustomerAlreadyExistException ex) {
                        System.out.println("Customer Already Exist: " + ex.getMessage() + "\n");
                    }
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

    private void doLogin() throws PasswordOrUsernameWrong, InvalidCredentialException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** CrazyBids.com ***\n Login \n");
        System.out.print("Enter username> ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        String password = scanner.nextLine().trim();

        if (username.length() > 12 && password.length() < 32
                && password.length() > 5 && password.length() > 32) {
            customer = customerSessionBeanRemote.verifyCustomerCredential(username, password);
        } else {
            throw new InvalidCredentialException("Invalid Credential!");
        }
    }

    public void menuCustomer() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome " + customer.getFirstName() + " " + customer.getLastName() + " ***");
            System.out.println("1: Enquire Available Balance");
            System.out.println("2: Update Customer Profile");
            System.out.println("3: View Credit Transaction History");
            System.out.println("4: View Credit Package"); // not yet done
            System.out.println("5: Browse All Auction Listings"); 
            System.out.println("6: Browse Won Auction Listings");
            System.out.println("7: Back");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    this.customer.getAvailableBalance();
                } else if (response == 2) {
                    doUpdateProfile();
                } else if (response == 3) {
                    viewTransHist();
                } else if (response == 4) {
                    //purchaseCreditPacks();
                } else if (response == 5) {
                    //availAuctionListings();
                } else if (response == 6) {
                    //wonAuctionListings();
                } else if (response == 7) {
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

    public void doRegisteration() throws CustomerAlreadyExistException {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** Register ***\n");
        System.out.println("Enter First Name >");
        String firstName = scanner.nextLine().trim();
        System.out.println("Enter Last Name >");
        String lastName = scanner.nextLine().trim();
        System.out.println("Enter username >");
        String username = scanner.nextLine().trim();
        System.out.println("Enter password >");
        String password = scanner.nextLine().trim();
        System.out.println("Enter email >");
        String email = scanner.nextLine().trim();
        System.out.println("Enter Contact number >");
        String contactNumber = scanner.nextLine().trim();
        customer = customerSessionBeanRemote.verifyRegisteration(username, password);
    }

    public void doUpdateProfile() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** Update Profile ***\n");
        System.out.println("Enter First Name >");
        String firstName = scanner.nextLine().trim();
        System.out.println("Enter Last Name >");
        String lastName = scanner.nextLine().trim();
        System.out.println("Enter username >");
        String username = scanner.nextLine().trim();
        System.out.println("Enter password >");
        String password = scanner.nextLine().trim();
        System.out.println("Enter email >");
        String email = scanner.nextLine().trim();
        System.out.println("Enter Contact number >");
        String contactNumber = scanner.nextLine().trim(); ///To implement other stuff like number etc
        customerSessionBeanRemote.doUpdate(firstName, lastName, username, password, email, contactNumber);
    }

    public void viewTransHist() {
        System.out.println("*** View Credit Transaction History ***\n");
        
        List<TransactionEntity> customerTransactionHist = this.customer.getTransactions();
        //System.out.println(customerSessionBeanRemote.getTransHist()); //must make it print line by line per transaction
        for(TransactionEntity transactionhist : customerTransactionHist){
            
            System.out.println("Trasaction for " + transactionhist.getTimeOfTransaction());
            System.out.println("Transaction ID: " + transactionhist.getTransactionid() + "\nAmount: " + transactionhist.getTransactionAmount());
            
            //To check which type it is , we  need to to check if credit Pacakage/Bid entity is NOT NULL;
            if (transactionhist.getBid() != null){
                if (transactionhist.getTransactionAmount().compareTo(BigDecimal.ZERO) < 0) { // NEGATIVE MEANS CUSTOMER SPEAND MONEY TO BID
                    System.out.println("Transaction Type [BID]: " + transactionhist.getTransactionAmount());
                } else {// possitive == refund
                    System.out.println("Transaction Type [REFUND]: " + transactionhist.getTransactionAmount());
                }
            } else {
                System.out.println("Transaction Type [PURCHASE CREDIT PACKAGE]: " + transactionhist.getTransactionAmount());
                System.out.println("Price: " + transactionhist.getCreditPackage().getCreditPrice() + " per unit");
            }
        }
    }

    public void purchaseCreditPacks() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Current available credit package ***\n");
        List<CreditPackageEntity> creditPackages = customerSessionBeanRemote.retrieveAllAvailableCreditPacakage();
        
        for (CreditPackageEntity creditPackage:creditPackages){
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
    
}
