/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasadminpanelclient;

import ejb.session.stateless.CreditPackageSessionBeanRemote;
import entity.CreditPackageEntity;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreditPackageNotFoundException;
import util.exception.CreditPackageTypeExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateCreditPackageException;

/**
 *
 * @author yeowh
 */
public class FinanceAdministrationModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private DecimalFormat decimalFormat;
    
    private CreditPackageSessionBeanRemote creditPackageSessionBeanRemote;

    public FinanceAdministrationModule() {
        decimalFormat = new DecimalFormat("#0.0000");
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public FinanceAdministrationModule(CreditPackageSessionBeanRemote creditPackageSessionBeanRemote) {
        this();
        this.creditPackageSessionBeanRemote = creditPackageSessionBeanRemote;
    }
    
    public void menuFinanceAdministration() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** OAS Administration Panel :: Finance Administration ***\n");
            System.out.println("1: Create New Credit Package");
            System.out.println("2: View Credit Package Details");
            System.out.println("3: View All Credit Packages");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doCreateNewCreditPackage();
                } else if (response == 2) {
                    doViewCreditPackageDetails();
                } else if (response == 3) {
                    doViewAllCreditPackages();
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
    
    private void doCreateNewCreditPackage() {
        Scanner scanner = new Scanner(System.in);
        CreditPackageEntity newCreditPackageEntity = new CreditPackageEntity();

        System.out.println("*** OAS Administration Panel :: Finance Administration :: Create New Credit Package ***\n");
        System.out.print("Enter Credit Package Type> ");
        newCreditPackageEntity.setCreditPackageType(scanner.nextLine().trim());
        System.out.print("Enter Credit Price> ");
        newCreditPackageEntity.setCreditPrice(new BigDecimal(scanner.nextLine().trim()));

        Set<ConstraintViolation<CreditPackageEntity>> constraintViolations = validator.validate(newCreditPackageEntity);

        if (constraintViolations.isEmpty()) {
            try {
                Long newCreditPackageId = creditPackageSessionBeanRemote.createNewCreditPackage(newCreditPackageEntity);
                System.out.println("New credit package created successfully!: " + newCreditPackageId + "\n");
            } catch (CreditPackageTypeExistException ex) {
                System.out.println("An error has occurred while creating the new credit package!: The credit package type already exist\n");
            } catch (UnknownPersistenceException ex) {
                System.out.println("An unknown error has occurred while creating the new credit package!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForCreditPackageEntity(constraintViolations);
        }
    }
    
    private void doViewCreditPackageDetails() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        System.out.println("*** OAS Administration Panel :: Finance Administration :: View Credit Package Details ***\n");
        System.out.print("Enter Credit Package Type> ");
        String creditPackageType = scanner.nextLine().trim();

        try {
            CreditPackageEntity creditPackageEntity = creditPackageSessionBeanRemote.retrieveCreditPackageByCreditPackageType(creditPackageType);
            System.out.printf("%17s%22s%21s%9s\n", "Credit Package ID", "Credit Package Type", "Credit Price", "Active");
            System.out.printf("%17s%22s%21s%9s\n", creditPackageEntity.getCreditPackageId(), creditPackageEntity.getCreditPackageType(), decimalFormat.format(creditPackageEntity.getCreditPrice()), creditPackageEntity.getActive().toString());
            System.out.println("------------------------");
            System.out.println("1: Update Credit Package");
            System.out.println("2: Delete Credit Package");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if (response == 1) {
                if (!creditPackageSessionBeanRemote.isCreditPackageInUse(creditPackageEntity.getCreditPackageId())) {
                    doUpdateCreditPackage(creditPackageEntity);
                } else {
                    System.out.println("This credit package is in use and cannot be modified!");
                }
            } else if (response == 2) {
                if (creditPackageEntity.getActive()) {
                    doDeleteCreditPackage(creditPackageEntity);
                } else {
                    System.out.println("This credit package cannot be removed as it is in use! However, it has already been marked as disabled!");
                }
            }
        } catch (CreditPackageNotFoundException ex) {
            System.out.println("An error has occurred while retrieving credit package: " + ex.getMessage() + "\n");
        }
    }
    
    private void doUpdateCreditPackage(CreditPackageEntity creditPackageEntity) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Administration Panel :: Finance Administration :: View Credit Package Details :: Update Credit Package ***\n");
        System.out.print("Enter Credit Price (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            creditPackageEntity.setCreditPrice(new BigDecimal(input));
        }

        Set<ConstraintViolation<CreditPackageEntity>> constraintViolations = validator.validate(creditPackageEntity);

        if (constraintViolations.isEmpty()) {
            try {
                creditPackageSessionBeanRemote.updateCreditPackage(creditPackageEntity);
                System.out.println("Credit package updated successfully!\n");
            } catch (CreditPackageNotFoundException | UpdateCreditPackageException ex) {
                System.out.println("An error has occurred while updating credit package: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForCreditPackageEntity(constraintViolations);
        }
    }
    
    private void doDeleteCreditPackage(CreditPackageEntity creditPackageEntity) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Administration Panel :: Finance Administration :: View Credit Package Details :: Delete Credit Package ***\n");
        System.out.printf("Confirm Delete Credit Package %s (Credit Package ID: %d) (Enter 'Y' to Delete)> ", creditPackageEntity.getCreditPackageType(), creditPackageEntity.getCreditPackageId());
        input = scanner.nextLine().trim();

        if (input.equals("Y")) {
            try {
                if (!creditPackageSessionBeanRemote.isCreditPackageInUse(creditPackageEntity.getCreditPackageId())) {
                    creditPackageSessionBeanRemote.deleteCreditPackage(creditPackageEntity.getCreditPackageId());
                    System.out.println("Credit package deleted successfully!\n");
                } else {
                    creditPackageSessionBeanRemote.deleteCreditPackage(creditPackageEntity.getCreditPackageId());
                    System.out.println("Credit package is in use and cannot be removed! However, it has been disabled successfully!");
                }
            } catch (CreditPackageNotFoundException ex) {
                System.out.println("An error has occurred while deleting the credit package: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Credit package NOT deleted!\n");
        }
    }
    
    private void doViewAllCreditPackages() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** OAS Administration Panel :: Finance Administration :: View All Credit Packages ***\n");

        List<CreditPackageEntity> creditPackageEntities = creditPackageSessionBeanRemote.retrieveAllCreditPackages();
        System.out.printf("%17s%22s%21s%9s\n", "Credit Package ID", "Credit Package Type", "Credit Price", "Active");
        
        for (CreditPackageEntity creditPackageEntity : creditPackageEntities) {
            System.out.printf("%17s%22s%21s%9s\n", creditPackageEntity.getCreditPackageId(), creditPackageEntity.getCreditPackageType(), decimalFormat.format(creditPackageEntity.getCreditPrice()), creditPackageEntity.getActive().toString());
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void showInputDataValidationErrorsForCreditPackageEntity(Set<ConstraintViolation<CreditPackageEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
}
