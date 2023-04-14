/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxybiddingcumsnipingagent;

import java.util.Scanner;
import ws.soap.customer.CustomerWebService;
import ws.soap.customer.InvalidPremiumRegistrationException_Exception;

/**
 *
 * @author kenne
 */
public class MainApp {
    
    private CustomerWebService port;
    
    // private Cus

    public MainApp(CustomerWebService port) {
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

                        // menuCustomer();

                    } catch (Exception ex) {
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
                break;
            }
        }
    }

    private void doRemoteLogin() throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Proxy Bidding cum Sniping Agent :: Login \n");
        System.out.print("Enter username> ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        String password = scanner.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            // globalCustomerEntity = customerSessionBeanRemote.customerLogin(username, password);
        } else {
            // throw new InvalidLoginCredentialException("Missing login credential!");
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
    
}
