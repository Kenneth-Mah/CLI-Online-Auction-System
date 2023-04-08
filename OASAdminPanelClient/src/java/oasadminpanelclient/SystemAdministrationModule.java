/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasadminpanelclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import entity.EmployeeEntity;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.EmployeeTypeEnum;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateEmployeeException;

/**
 *
 * @author yeowh
 */
public class SystemAdministrationModule {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    private EmployeeSessionBeanRemote employeeSessionBeanRemote;

    public SystemAdministrationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public SystemAdministrationModule(EmployeeSessionBeanRemote employeeSessionBeanRemote) {
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
    }

    public void menuSystemAdministration() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** OAS Administration Panel :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View Employee Details");
            System.out.println("3: View All Employees");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doCreateNewEmployee();
                } else if (response == 2) {
                    doViewEmployeeDetails();
                } else if (response == 3) {
                    doViewAllEmployees();
                } else if (response == 4) {
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

    private void doCreateNewEmployee() {
        Scanner scanner = new Scanner(System.in);
        EmployeeEntity newEmployeeEntity = new EmployeeEntity();

        System.out.println("*** OAS Administration Panel :: System Administration :: Create New Employee ***\n");
        System.out.print("Enter First Name> ");
        newEmployeeEntity.setFirstName(scanner.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newEmployeeEntity.setLastName(scanner.nextLine().trim());

        while (true) {
            System.out.print("Select Employee Type (1: Admin, 2: Finance, 3: Sales)> ");
            Integer employeeTypeInt = scanner.nextInt();

            if (employeeTypeInt >= 1 && employeeTypeInt <= 3) {
                newEmployeeEntity.setEmployeeTypeEnum(EmployeeTypeEnum.values()[employeeTypeInt - 1]);
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }

        scanner.nextLine();
        System.out.print("Enter Username> ");
        newEmployeeEntity.setUsername(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newEmployeeEntity.setPassword(scanner.nextLine().trim());

        Set<ConstraintViolation<EmployeeEntity>> constraintViolations = validator.validate(newEmployeeEntity);

        if (constraintViolations.isEmpty()) {
            try {
                Long newEmployeeId = employeeSessionBeanRemote.createNewEmployee(newEmployeeEntity);
                System.out.println("New employee created successfully!: " + newEmployeeId + "\n");
            } catch (EmployeeUsernameExistException ex) {
                System.out.println("An error has occurred while creating the new employee!: The user name already exist\n");
            } catch (UnknownPersistenceException ex) {
                System.out.println("An unknown error has occurred while creating the new employee!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForEmployeeEntity(constraintViolations);
        }
    }

    private void doViewEmployeeDetails() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        System.out.println("*** OAS Administration Panel :: System Administration :: View Employee Details ***\n");
        System.out.print("Enter Employee Username> ");
        String employeeUsername = scanner.nextLine().trim();

        try {
            EmployeeEntity employeeEntity = employeeSessionBeanRemote.retrieveEmployeeByUsername(employeeUsername);
            System.out.printf("%11s%20s%20s%16s%20s%20s\n", "Employee ID", "First Name", "Last Name", "Employee Type", "Username", "Password");
            System.out.printf("%11s%20s%20s%16s%20s%20s\n", employeeEntity.getEmployeeId().toString(), employeeEntity.getFirstName(), employeeEntity.getLastName(), employeeEntity.getEmployeeTypeEnum().toString(), employeeEntity.getUsername(), employeeEntity.getPassword());
            System.out.println("------------------------");
            System.out.println("1: Update Employee");
            System.out.println("2: Delete Employee");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if (response == 1) {
                doUpdateEmployee(employeeEntity);
            } else if (response == 2) {
                doDeleteEmployee(employeeEntity);
            }
        } catch (EmployeeNotFoundException ex) {
            System.out.println("An error has occurred while retrieving employee: " + ex.getMessage() + "\n");
        }
    }

    private void doUpdateEmployee(EmployeeEntity employeeEntity) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Administration Panel :: System Administration :: View Employee Details :: Update Employee ***\n");
        System.out.print("Enter First Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            employeeEntity.setFirstName(input);
        }

        System.out.print("Enter Last Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            employeeEntity.setLastName(input);
        }

        while (true) {
            System.out.print("Select Employee Type (0: No Change, 1: Admin, 2: Finance, 3: Sales)> ");
            Integer employeeTypeInt = scanner.nextInt();

            if (employeeTypeInt >= 1 && employeeTypeInt <= 3) {
                employeeEntity.setEmployeeTypeEnum(EmployeeTypeEnum.values()[employeeTypeInt - 1]);
                break;
            } else if (employeeTypeInt == 0) {
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }

        Set<ConstraintViolation<EmployeeEntity>> constraintViolations = validator.validate(employeeEntity);

        if (constraintViolations.isEmpty()) {
            try {
                employeeSessionBeanRemote.updateEmployee(employeeEntity);
                System.out.println("Employee updated successfully!\n");
            } catch (EmployeeNotFoundException | UpdateEmployeeException ex) {
                System.out.println("An error has occurred while updating employee: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForEmployeeEntity(constraintViolations);
        }
    }

    private void doDeleteEmployee(EmployeeEntity employeeEntity) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Administration Panel :: System Administration :: View Employee Details :: Delete Employee ***\n");
        System.out.printf("Confirm Delete Employee %s %s (Employee ID: %d) (Enter 'Y' to Delete)> ", employeeEntity.getFirstName(), employeeEntity.getLastName(), employeeEntity.getEmployeeId());
        input = scanner.nextLine().trim();

        if (input.equals("Y")) {
            try {
                employeeSessionBeanRemote.deleteEmployee(employeeEntity.getEmployeeId());
                System.out.println("Employee deleted successfully!\n");
            } catch (EmployeeNotFoundException ex) {
                System.out.println("An error has occurred while deleting the employee: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Employee NOT deleted!\n");
        }
    }

    private void doViewAllEmployees() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** OAS Administration Panel :: System Administration :: View All Employees ***\n");

        List<EmployeeEntity> employeeEntities = employeeSessionBeanRemote.retrieveAllEmployees();
        System.out.printf("%11s%20s%20s%16s%20s%20s\n", "Employee ID", "First Name", "Last Name", "Employee Type", "Username", "Password");

        for (EmployeeEntity employeeEntity : employeeEntities) {
            System.out.printf("%11s%20s%20s%16s%20s%20s\n", employeeEntity.getEmployeeId().toString(), employeeEntity.getFirstName(), employeeEntity.getLastName(), employeeEntity.getEmployeeTypeEnum().toString(), employeeEntity.getUsername(), employeeEntity.getPassword());
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void showInputDataValidationErrorsForEmployeeEntity(Set<ConstraintViolation<EmployeeEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

}
