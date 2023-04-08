/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasadminpanelclient;

import ejb.session.stateless.AuctionListingSessionBeanRemote;
import entity.AuctionListingEntity;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AuctionListingNameExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeowh
 */
public class SalesAdministrationModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private AuctionListingSessionBeanRemote auctionListingSessionBeanRemote;

    public SalesAdministrationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public SalesAdministrationModule(AuctionListingSessionBeanRemote auctionListingSessionBeanRemote) {
        this();
        this.auctionListingSessionBeanRemote = auctionListingSessionBeanRemote;
    }
    
    public void menuSalesAdministration() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** OAS Administration Panel :: Sales Administration ***\n");
            System.out.println("1: Create New Auction Listing");
            System.out.println("2: View Auction Listing Details");
            System.out.println("3: View All Auction Listings");
            System.out.println("4: View All Auction Listings with Bids but Below Reserve Price");
            System.out.println("5: Back\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doCreateNewAuctionListing();
                } else if (response == 2) {
//                    doViewAuctionListingDetails();
                } else if (response == 3) {
//                    doViewAllAuctionListings();
                } else if (response == 4) {
//                    doViewAllAuctionListingsAlt();
                } else if (response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 5) {
                break;
            }
        }
    }
    
    private void doCreateNewAuctionListing() {
        Scanner scanner = new Scanner(System.in);
        AuctionListingEntity newAuctionListingEntity = new AuctionListingEntity();

        System.out.println("*** OAS Administration Panel :: Sales Administration :: Create New Auction Listing ***\n");
        System.out.print("Enter Auction Listing Name> ");
        newAuctionListingEntity.setAuctionListingName(scanner.nextLine().trim());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss");
        while (true) {
            System.out.print("Enter Start Date Time (dd/MM/yyyy at HH:mm:ss)> ");
            try {
                newAuctionListingEntity.setStartDateTime(sdf.parse(scanner.nextLine().trim()));
                break;
            } catch (ParseException ex) {
                System.out.println("Invalid date, please try again!\n");
            }
        }
        while (true) {
            System.out.print("Enter End Date Time (dd/MM/yyyy at HH:mm:ss)> ");
            try {
                newAuctionListingEntity.setEndDateTime(sdf.parse(scanner.nextLine().trim()));
                break;
            } catch (ParseException ex) {
                System.out.println("Invalid date, please try again!\n");
            }
        }
        
        System.out.print("Enter Reserve Price (blank if none)> ");
        String input = scanner.nextLine().trim();
        if (input.length() > 0) {
            newAuctionListingEntity.setReservePrice(new BigDecimal(scanner.nextLine().trim()));
        }

        Set<ConstraintViolation<AuctionListingEntity>> constraintViolations = validator.validate(newAuctionListingEntity);

        if (constraintViolations.isEmpty()) {
            try {
                Long newAuctionListingId = auctionListingSessionBeanRemote.createNewAuctionListing(newAuctionListingEntity);
                System.out.println("New auction listing created successfully!: " + newAuctionListingId + "\n");
            } catch (AuctionListingNameExistException ex) {
                System.out.println("An error has occurred while creating the new auction listing!: The auction listing name already exist\n");
            } catch (UnknownPersistenceException ex) {
                System.out.println("An unknown error has occurred while creating the new auction listing!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForAuctionListingEntity(constraintViolations);
        }
    }
    
    private void showInputDataValidationErrorsForAuctionListingEntity(Set<ConstraintViolation<AuctionListingEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
}
