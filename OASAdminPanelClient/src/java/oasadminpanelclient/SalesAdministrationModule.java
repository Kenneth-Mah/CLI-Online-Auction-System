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
import util.exception.AuctionListingNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidStartAndEndDatesException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateAuctionListingException;

/**
 *
 * @author yeowh
 */
public class SalesAdministrationModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private DecimalFormat decimalFormat;
    private SimpleDateFormat simpleDateFormat;
    
    private AuctionListingSessionBeanRemote auctionListingSessionBeanRemote;

    public SalesAdministrationModule() {
        decimalFormat = new DecimalFormat("#0.0000");
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss");
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
                    doViewAuctionListingDetails();
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
        
        while (true) {
            // Give 5 minutes grace period to create the auction listing
            Date currentDateTime = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
            Date startDateTime = new Date();
            Date endDateTime = new Date();
            while (true) {
                System.out.print("Enter Start Date Time (dd/MM/yyyy at HH:mm:ss)> ");
                try {
                    startDateTime = simpleDateFormat.parse(scanner.nextLine().trim());
                    break;
                } catch (ParseException ex) {
                    System.out.println("Invalid date, please try again!\n");
                }
            }
            while (true) {
                System.out.print("Enter End Date Time (dd/MM/yyyy at HH:mm:ss)> ");
                try {
                    endDateTime = simpleDateFormat.parse(scanner.nextLine().trim());
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
            newAuctionListingEntity.setReservePrice(new BigDecimal(input));
        }

        Set<ConstraintViolation<AuctionListingEntity>> constraintViolations = validator.validate(newAuctionListingEntity);

        if (constraintViolations.isEmpty()) {
            try {
                Long newAuctionListingId = auctionListingSessionBeanRemote.createNewAuctionListing(newAuctionListingEntity);
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
    
    private void doViewAuctionListingDetails() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        System.out.println("*** OAS Administration Panel :: Sales Administration :: View Auction Listing Details ***\n");
        System.out.print("Enter Auction Listing Name> ");
        String auctionListingName = scanner.nextLine().trim();

        try {
            AuctionListingEntity auctionListingEntity = auctionListingSessionBeanRemote.retrieveAuctionListingByAuctionListingName(auctionListingName);
            System.out.printf("%18s%26s%34s%34s%20s%20s%11s%31s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price", "Disabled", "Requires Manual Intervention");
            String reservePriceString;
            if (auctionListingEntity.getReservePrice() != null) {
                reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
            } else {
                reservePriceString = "null";
            }
            System.out.printf("%18s%26s%34s%34s%20s%20s%11s%31s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toString(), auctionListingEntity.getEndDateTime().toString(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()), auctionListingEntity.getDisabled().toString(), auctionListingEntity.getRequiresManualIntervention().toString());
            System.out.println("------------------------");
            System.out.println("1: Update Auction Listing");
            System.out.println("2: Delete Auction Listing");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if (response == 1) {
                if (auctionListingEntity.getStartDateTime().compareTo(new Date()) < 0) {
                    System.out.println("This auction listing cannot be modified as its Start Date-time has already passed!");
                } else {
                    doUpdateAuctionListing(auctionListingEntity);
                }
            } else if (response == 2) {
                if (!auctionListingEntity.getDisabled()) {
                    doDeleteAuctionListing(auctionListingEntity);
                } else {
                    System.out.println("This auction listing cannot be removed as it is in use! However, it has already been marked as disabled!");
                }
            }
        } catch (AuctionListingNotFoundException ex) {
            System.out.println("An error has occurred while retrieving auction listing: " + ex.getMessage() + "\n");
        }
    }
    
    private void doUpdateAuctionListing(AuctionListingEntity auctionListingEntity) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Administration Panel :: Sales Administration :: View Auction Listing Details :: Update Auction Listing ***\n");
        
        while (true) {
            // Give 5 minutes grace period to create the auction listing
            Date currentDateTime = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
            Date startDateTime = auctionListingEntity.getStartDateTime();
            Date endDateTime = auctionListingEntity.getEndDateTime();
            while (true) {
                System.out.print("Enter Start Date Time (dd/MM/yyyy at HH:mm:ss) [blank if no change]> ");
                input = scanner.nextLine().trim();
                if (input.length() > 0) {
                    try {
                        startDateTime = simpleDateFormat.parse(input);
                        break;
                    } catch (ParseException ex) {
                        System.out.println("Invalid date, please try again!\n");
                    }
                } else {
                    break;
                }
            }
            while (true) {
                System.out.print("Enter End Date Time (dd/MM/yyyy at HH:mm:ss) [blank if no change]> ");
                input = scanner.nextLine().trim();
                if (input.length() > 0) {
                    try {
                        endDateTime = simpleDateFormat.parse(input);
                        break;
                    } catch (ParseException ex) {
                        System.out.println("Invalid date, please try again!\n");
                    }
                } else {
                    break;
                }
            }
            if (currentDateTime.compareTo(startDateTime) < 0 && startDateTime.compareTo(endDateTime) < 0) {
                auctionListingEntity.setStartDateTime(startDateTime);
                auctionListingEntity.setEndDateTime(endDateTime);
                break;
            } else {
                System.out.println("Invalid dates! Start date and end date must be in the future and start date must be before the end date!");
            }
        }
        
        System.out.print("Enter Reserve Price (blank if none)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            auctionListingEntity.setReservePrice(new BigDecimal(input));
        }

        Set<ConstraintViolation<AuctionListingEntity>> constraintViolations = validator.validate(auctionListingEntity);

        if (constraintViolations.isEmpty()) {
            try {
                auctionListingSessionBeanRemote.updateAuctionListing(auctionListingEntity);
                System.out.println("Auction listing updated successfully!\n");
            } catch (AuctionListingNotFoundException | UpdateAuctionListingException | InvalidStartAndEndDatesException ex) {
                System.out.println("An error has occurred while updating auction listing: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForAuctionListingEntity(constraintViolations);
        }
    }
    
    private void doDeleteAuctionListing(AuctionListingEntity auctionListingEntity) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Administration Panel :: Sales Administration :: View Auction Listing Details :: Delete Auction Listing ***\n");
        System.out.printf("Confirm Delete Auction Listing %s (Auction Listing ID: %d) (Enter 'Y' to Delete)> ", auctionListingEntity.getAuctionListingName(), auctionListingEntity.getAuctionListingId());
        input = scanner.nextLine().trim();

        if (input.equals("Y")) {
            try {
                if (!auctionListingSessionBeanRemote.isAuctionListingInUse(auctionListingEntity.getAuctionListingId())) {
                    auctionListingSessionBeanRemote.deleteAuctionListing(auctionListingEntity.getAuctionListingId());
                    System.out.println("Auction listing deleted successfully!\n");
                } else {
                    auctionListingSessionBeanRemote.deleteAuctionListing(auctionListingEntity.getAuctionListingId());
                    System.out.println("Auction listing is in use and cannot be removed! However, it has been disabled successfully!");
                }
            } catch (AuctionListingNotFoundException ex) {
                System.out.println("An error has occurred while deleting the auction listing: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Auction listing NOT deleted!\n");
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
