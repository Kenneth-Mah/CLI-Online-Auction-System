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
import util.exception.CustomerNotfoundException;
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
                    doViewAllAuctionListingsWithBidsButBelowReservePrice();
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
            Date currentDateTime = new Date();
            Date startDateTime;
            Date endDateTime;
            while (true) {
                System.out.print("Enter Start Date-time (dd/MM/yyyy at HH:mm:ss)> ");
                try {
                    startDateTime = simpleDateFormat.parse(scanner.nextLine().trim());
                    break;
                } catch (ParseException ex) {
                    System.out.println("Invalid date, please try again!\n");
                }
            }
            while (true) {
                System.out.print("Enter End Date-time (dd/MM/yyyy at HH:mm:ss)> ");
                try {
                    endDateTime = simpleDateFormat.parse(scanner.nextLine().trim());
                    break;
                } catch (ParseException ex) {
                    System.out.println("Invalid date, please try again!\n");
                }
            }
            if (currentDateTime.compareTo(startDateTime) < 0 && 
                    startDateTime.compareTo(endDateTime) < 0) {
                newAuctionListingEntity.setStartDateTime(startDateTime);
                newAuctionListingEntity.setEndDateTime(endDateTime);
                break;
            } else {
                System.out.println("Invalid dates! Start date-time and end date-time must be in the future and start date must be must be before the end date-time!");
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
            System.out.printf("%18s%26s%34s%34s%20s%20s%9s%11s%31s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price", "Active", "Disabled", "Requires Manual Intervention");
            String reservePriceString;
            if (auctionListingEntity.getReservePrice() != null) {
                reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
            } else {
                reservePriceString = "null";
            }
            System.out.printf("%18s%26s%34s%34s%20s%20s%9s%11s%31s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toString(), auctionListingEntity.getEndDateTime().toString(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()), auctionListingEntity.getActive().toString(), auctionListingEntity.getDisabled().toString(), auctionListingEntity.getRequiresManualIntervention().toString());
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
            Date currentDateTime = new Date();
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
            if (currentDateTime.compareTo(startDateTime) < 0 && 
                    startDateTime.compareTo(endDateTime) < 0) {
                auctionListingEntity.setStartDateTime(startDateTime);
                auctionListingEntity.setEndDateTime(endDateTime);
                break;
            } else {
                System.out.println("Invalid dates! Start date-time and end date-time must be in the future and start date-time must be must be before the end date-time!");
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
            } catch (AuctionListingNotFoundException | CustomerNotfoundException | UnknownPersistenceException | InputDataValidationException ex) {
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
        System.out.printf("%18s%26s%34s%34s%20s%20s%9s%11s%31s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price", "Active", "Disabled", "Requires Manual Intervention");

        for (AuctionListingEntity auctionListingEntity : auctionListingEntities) {
            String reservePriceString;
            if (auctionListingEntity.getReservePrice() != null) {
                reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
            } else {
                reservePriceString = "null";
            }
            System.out.printf("%18s%26s%34s%34s%20s%20s%9s%11s%31s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toString(), auctionListingEntity.getEndDateTime().toString(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()), auctionListingEntity.getActive().toString(), auctionListingEntity.getDisabled().toString(), auctionListingEntity.getRequiresManualIntervention().toString());
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doViewAllAuctionListingsWithBidsButBelowReservePrice() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** OAS Administration Panel :: Sales Administration :: View All Auction Listings With Bids But Below Reserve Price ***\n");

        List<AuctionListingEntity> auctionListingEntities = auctionListingSessionBeanRemote.retrieveAllAuctionListingsRequiringManualIntervention();
        System.out.printf("%18s%26s%34s%34s%20s%20s%9s%11s%31s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price", "Active", "Disabled", "Requires Manual Intervention");

        for (AuctionListingEntity auctionListingEntity : auctionListingEntities) {
            String reservePriceString;
            if (auctionListingEntity.getReservePrice() != null) {
                reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
            } else {
                reservePriceString = "null";
            }
            System.out.printf("%18s%26s%34s%34s%20s%20s%9s%11s%31s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toString(), auctionListingEntity.getEndDateTime().toString(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()), auctionListingEntity.getActive().toString(), auctionListingEntity.getDisabled().toString(), auctionListingEntity.getRequiresManualIntervention().toString());
        }

        System.out.print("To Assign Winning Bid For Listing With Bids But Below Reserve Price, Enter Auction Listing Name (blank to exit)> ");
        String auctionListingName = scanner.nextLine().trim();

        if (auctionListingName.length() > 0) {
            doAssignWinningBidForListingsWithBidsButBelowReservePrice(auctionListingName);
        }
    }
    
    private void doAssignWinningBidForListingsWithBidsButBelowReservePrice(String auctionListingName) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Administration Panel :: Sales Administration :: Assign Winning Bid For Listing With Bids But Below Reserve Price ***\n");
        
        try {
            AuctionListingEntity auctionListingEntity = auctionListingSessionBeanRemote.retrieveAuctionListingByAuctionListingName(auctionListingName);
            System.out.printf("%18s%26s%34s%34s%20s%20s\n", "Auction Listing ID", "Auction Listing Name", "Start Date-time", "End Date-time", "Reserve Price", "Highest Bid Price");
            String reservePriceString;
            if (auctionListingEntity.getReservePrice() != null) {
                reservePriceString = decimalFormat.format(auctionListingEntity.getReservePrice());
            } else {
                reservePriceString = "null";
            }
            System.out.printf("%18s%26s%34s%34s%20s%20s\n", auctionListingEntity.getAuctionListingId().toString(), auctionListingEntity.getAuctionListingName(), auctionListingEntity.getStartDateTime().toString(), auctionListingEntity.getEndDateTime().toString(), reservePriceString, decimalFormat.format(auctionListingEntity.getHighestBidPrice()));
            System.out.println("------------------------");
            System.out.printf("Confirm Assign Winning Bid For Auction Listing %s (Auction Listing ID: %d) (Enter 'Y' to Assign Winning Bid, 'N' to Assign No Winning Bid)> ", auctionListingEntity.getAuctionListingName(), auctionListingEntity.getAuctionListingId());
            input = scanner.nextLine().trim();

            if (input.equals("Y")) {
                try {
                    auctionListingSessionBeanRemote.manuallyAssignTheHighestBidAsWinningBid(auctionListingEntity.getAuctionListingId());
                } catch (AuctionListingNotFoundException ex) {
                    System.out.println("An error has occurred while assigning the highest bid as winning bid: " + ex.getMessage() + "\n");
                }
            } else if (input.equals("N")) {
                try {
                    auctionListingSessionBeanRemote.manuallyMarkTheAuctionListingAsHavingNoWinningBid(auctionListingEntity.getAuctionListingId());
                } catch (AuctionListingNotFoundException | CustomerNotfoundException | UnknownPersistenceException | InputDataValidationException ex) {
                    System.out.println("An error has occurred while marking the auction listing as having no winning bid: " + ex.getMessage() + "\n");
                }
            } else {
                System.out.println("Address NOT deleted!\n");
            }
        } catch (AuctionListingNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the auction listing: " + ex.getMessage() + "\n");
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
