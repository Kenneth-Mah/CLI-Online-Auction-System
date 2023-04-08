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
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeowh
 */
public class SystemAdministrationModule {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    private EmployeeSessionBeanRemote employeeSessionBeanRemote;

    private EmployeeEntity currentEmployeeEntity;

    public SystemAdministrationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public SystemAdministrationModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, EmployeeEntity currentEmployeeEntity) {
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.currentEmployeeEntity = currentEmployeeEntity;
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
                Long newStaffId = employeeSessionBeanRemote.createNewEmployee(newEmployeeEntity);
                System.out.println("New staff created successfully!: " + newStaffId + "\n");
            } catch (EmployeeUsernameExistException ex) {
                System.out.println("An error has occurred while creating the new staff!: The user name already exist\n");
            } catch (UnknownPersistenceException ex) {
                System.out.println("An unknown error has occurred while creating the new staff!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForEmployeeEntity(constraintViolations);
        }
    }

    private void doViewEmployeeDetails() {

    }

    private void doViewAllEmployees() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** POS System :: System Administration :: View All Staffs ***\n");

        List<EmployeeEntity> employeeEntities = employeeSessionBeanRemote.retrieveAllEmployees();
        System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Staff ID", "First Name", "Last Name", "Access Right", "Username", "Password");

        for (EmployeeEntity employeeEntity : employeeEntities) {
            System.out.printf("%8s%20s%20s%15s%20s%20s\n", employeeEntity.getEmployeeId().toString(), employeeEntity.getFirstName(), employeeEntity.getLastName(), employeeEntity.getEmployeeTypeEnum().toString(), employeeEntity.getUsername(), employeeEntity.getPassword());
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
