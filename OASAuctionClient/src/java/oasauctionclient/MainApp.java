/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasauctionclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import entity.CreditTransactionEntity;
import entity.CustomerEntity;
import java.math.BigDecimal;
import java.util.Scanner;
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
            System.out.println("4: Purchase Credit Package");
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
                    //viewTransHist();
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** View Credit Transaction History ***\n");
        //System.out.println(customerSessionBeanRemote.getTransHist()); //must make it print line by line per transaction
    }
}
