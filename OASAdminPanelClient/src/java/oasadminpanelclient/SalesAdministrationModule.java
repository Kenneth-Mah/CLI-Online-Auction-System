/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasadminpanelclient;

import ejb.session.stateless.AuctionListingSessionBeanRemote;
import entity.AuctionListingEntity;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AuctionListingNameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidStartAndEndDatesException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeowh
 */
public class SalesAdministrationModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private DecimalFormat decimalFormat;
    
    private AuctionListingSessionBeanRemote auctionListingSessionBeanRemote;

    public SalesAdministrationModule() {
        decimalFormat = new DecimalFormat("#0.0000");
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
                    doViewAllAuctionListings();
                } else if (response == 4) {
//                    doViewAllAuctionListingsWithBidsButBelowReservePrice();
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
        // Give 5 minutes grace period to create the auction listing
        Date currentDateTime = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
        while (true) {
            Date startDateTime = new Date();
            Date endDateTime = new Date();
            while (true) {
                System.out.print("Enter Start Date Time (dd/MM/yyyy at HH:mm:ss)> ");
                try {
                    startDateTime = sdf.parse(scanner.nextLine().trim());
                    break;
                } catch (ParseException ex) {
                    System.out.println("Invalid date, please try again!\n");
                }
            }
            while (true) {
                System.out.print("Enter End Date Time (dd/MM/yyyy at HH:mm:ss)> ");
                try {
                    endDateTime = sdf.parse(scanner.nextLine().trim());
                    break;
                } catch (ParseException ex) {
                    System.out.println("Invalid date, please try again!\n");
                }
            }
            if (currentDateTime.compareTo(startDateTime) < 0 && startDateTime.compareTo(endDateTime) < 0) {
                newAuctionListingEntity.setStartDateTime(startDateTime);
                newAuctionListingEntity.setEndDateTime(endDateTime);
                break;
            } else {
                System.out.println("Invalid dates! Start date and end date must be in the future and start date must be before the end date!");
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
                Long newAuctionListingId = auctionListingSessionBeanRemote.createNewAuctionListing(newAuctionListingEntity, currentDateTime);
                System.out.println("New auction listing created successfully!: " + newAuctionListingId + "\n");
            } catch (InvalidStartAndEndDatesException ex) {
                System.out.println("An error has occurred while creating the new auction listing!: " + ex.getMessage() + "\n");
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
    
    private void doViewAllAuctionListings() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** OAS Administration Panel :: Sales Administration :: View All Auction Listings ***\n");

        List<AuctionListingEntity> auctionListingEntities = auctionListingSessionBeanRemote.retrieveAllAuctionListings();
        System.out.printf("%18s%26s%34s%34s%20s%20s%11s%31s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price", "Disabled", "Requires Manual Intervention");

        for (AuctionListingEntity auctionListingEntity : auctionListingEntities) {
            String reservePriceString;
            if (auctionListingEntity.getReservePrice() != null) {
                reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
            } else {
                reservePriceString = "null";
            }
            System.out.printf("%18s%26s%34s%34s%20s%20s%11s%31s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toString(), auctionListingEntity.getEndDateTime().toString(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()), auctionListingEntity.getDisabled().toString(), auctionListingEntity.getRequiresManualIntervention().toString());
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void showInputDataValidationErrorsForAuctionListingEntity(Set<ConstraintViolation<AuctionListingEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
}
