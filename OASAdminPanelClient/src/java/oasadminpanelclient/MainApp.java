/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasadminpanelclient;

import ejb.session.stateless.CreditPackageSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import entity.EmployeeEntity;
import java.util.Scanner;
import util.enumeration.EmployeeTypeEnum;
import util.exception.EmployeeNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UpdateEmployeeException;

/**
 *
 * @author yeowh
 */
public class MainApp {
    
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private CreditPackageSessionBeanRemote creditPackageSessionBeanRemote;
    
    private SystemAdministrationModule systemAdministrationModule;
    private FinanceAdministrationModule financeAdministrationModule;
    private SalesAdministrationModule salesAdministrationModule;
    
    private EmployeeEntity currentEmployeeEntity;

    public MainApp() {
    }

    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote, CreditPackageSessionBeanRemote creditPackageSessionBeanRemote) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.creditPackageSessionBeanRemote = creditPackageSessionBeanRemote;
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

                        systemAdministrationModule = new SystemAdministrationModule(employeeSessionBeanRemote);
                        financeAdministrationModule = new FinanceAdministrationModule(creditPackageSessionBeanRemote);
//                        salesAdministrationModule = new SalesAdministrationModule();
                        menuMain();
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
    
    private void menuMain() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** OAS Administration Panel ***\n");
            System.out.println("You are login as " + currentEmployeeEntity.getFirstName() + " " + currentEmployeeEntity.getLastName() + " with " + currentEmployeeEntity.getEmployeeTypeEnum().toString() + " rights\n");
            System.out.println("1: Access Module");
            System.out.println("2: Change Password");
            System.out.println("3: Logout\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    if (currentEmployeeEntity.getEmployeeTypeEnum() == EmployeeTypeEnum.ADMIN) {
                        systemAdministrationModule.menuSystemAdministration();
                    } else if (currentEmployeeEntity.getEmployeeTypeEnum() == EmployeeTypeEnum.FINANCE) {
                        financeAdministrationModule.menuFinanceAdministration();
                    } else if (currentEmployeeEntity.getEmployeeTypeEnum() == EmployeeTypeEnum.SALES) {
//                        salesAdministrationModule.menuSalesAdministration();
                    }
                } else if (response == 2) {
                    doChangePassword(currentEmployeeEntity);
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
    
    private void doChangePassword(EmployeeEntity referenceEmployeeEntity) {
        Scanner scanner = new Scanner(System.in);
        String oldPassword = "";
        String newPassword1 = "";
        String newPassword2 = "";

        System.out.println("*** OAS Administration Panel :: Change Password ***\n");
        System.out.print("Enter old password> ");
        oldPassword = scanner.nextLine().trim();
        System.out.print("Enter new password> ");
        newPassword1 = scanner.nextLine().trim();
        System.out.print("Enter new password again> ");
        newPassword2 = scanner.nextLine().trim();

        if (currentEmployeeEntity.getPassword().equals(oldPassword) && newPassword1.length() > 0 && newPassword1.equals(newPassword2)) {
            try {
                referenceEmployeeEntity.setPassword(newPassword1);
                currentEmployeeEntity = employeeSessionBeanRemote.changePassword(referenceEmployeeEntity);
                
                System.out.println("Password changed successfully!\n");
            } catch (EmployeeNotFoundException | UpdateEmployeeException | InputDataValidationException ex) {
                System.out.println("An error has occurred while updating product: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Wrong old password or invalid new password!");
        }
    }
    
}
