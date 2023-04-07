/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasadminpanelclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import entity.EmployeeEntity;
import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author yeowh
 */
public class MainApp {
    
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    
    private EmployeeEntity currentEmployeeEntity;

    public MainApp() {
    }

    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
    }
    
    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to OAS Administration Panel ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");

//                        cashierOperationModule = new CashierOperationModule(productEntitySessionBeanRemote, saleTransactionEntitySessionBeanRemote, checkoutBeanRemote, emailSessionBeanRemote, queueCheckoutNotification, queueCheckoutNotificationFactory, currentStaffEntity);
//                        systemAdministrationModule = new SystemAdministrationModule(staffEntitySessionBeanRemote, productEntitySessionBeanRemote, currentStaffEntity);
//                        menuMain();
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 2) {
                break;
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println("*** OAS Administration Panel :: Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            currentEmployeeEntity = employeeSessionBeanRemote.employeeLogin(username, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
}
