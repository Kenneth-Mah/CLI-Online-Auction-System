/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AuctionListingEntity;
import entity.BidEntity;
import entity.CustomerEntity;
import entity.TransactionEntity;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AuctionListingAlreadyClosedException;
import util.exception.AuctionListingNotFoundException;
import util.exception.CustomerNotfoundException;
import util.exception.InputDataValidationException;
import util.exception.InsufficientCreditException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kenne
 */
@Stateless
public class BidSessionBean implements BidSessionBeanRemote, BidSessionBeanLocal {

    @PersistenceContext(unitName = "CrazyBidsJPA-ejbPU")
    private EntityManager em;
    
    @Resource
    TimerService timerService;
    
    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    private AuctionListingSessionBeanLocal auctionListingSessionBeanLocal;
    @EJB
    private TransactionSessionBeanLocal transactionSessionBeanLocal;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public BidSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public BigDecimal getMinNextBidPrice(Long auctionListingId) throws AuctionListingNotFoundException {
        AuctionListingEntity auctionListingEntity = auctionListingSessionBeanLocal.retrieveAuctionListingByAuctionListingId(auctionListingId);
        
        BigDecimal minBidIncrement;
        BigDecimal currentHighestBidPrice = auctionListingEntity.getHighestBidPrice();
        if (currentHighestBidPrice.compareTo(new BigDecimal("0.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("0.99")) <= 0) {
            minBidIncrement = new BigDecimal("0.05");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("1.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("4.99")) <= -1) {
            minBidIncrement = new BigDecimal("0.25");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("5.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("24.99")) <= -1) {
            minBidIncrement = new BigDecimal("0.50");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("25.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("99.99")) <= -1) {
            minBidIncrement = new BigDecimal("1.00");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("100.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("249.99")) <= -1) {
            minBidIncrement = new BigDecimal("2.50");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("250.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("499.99")) <= -1) {
            minBidIncrement = new BigDecimal("5.00");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("500.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("999.99")) <= -1) {
            minBidIncrement = new BigDecimal("10.00");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("1000.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("2499.99")) <= -1) {
            minBidIncrement = new BigDecimal("25.00");
        } else if (currentHighestBidPrice.compareTo(new BigDecimal("2500.00")) >= 0 && currentHighestBidPrice.compareTo(new BigDecimal("4999.99")) <= -1) {
            minBidIncrement = new BigDecimal("50.00");
        } else { // currentHighestBidPrice.compareTo(new BigDecimal("5000.00")) >= 0
            minBidIncrement = new BigDecimal("100.00");
        }

        BigDecimal minNextBidPrice = currentHighestBidPrice.add(minBidIncrement);
        return minNextBidPrice;
    }
    
    @Override
    public Long createNewBid(Long customerId, Long auctionListingId, BidEntity newBidEntity) throws CustomerNotfoundException, AuctionListingNotFoundException, UnknownPersistenceException, InputDataValidationException, InsufficientCreditException, AuctionListingAlreadyClosedException {
        Set<ConstraintViolation<BidEntity>> constraintViolations = validator.validate(newBidEntity);

        if (constraintViolations.isEmpty()) {
            try {
                CustomerEntity customerEntity = customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);
                AuctionListingEntity auctionListingEntity = auctionListingSessionBeanLocal.retrieveAuctionListingByAuctionListingId(auctionListingId);

                if (customerEntity.getAvailableBalance().compareTo(newBidEntity.getBidPrice()) == -1) {
                    throw new InsufficientCreditException("Insufficient credit balance");
                }
                if (!auctionListingEntity.getActive()) {
                    throw new AuctionListingAlreadyClosedException("Bid failed, Auction Listing has already closed");
                }
                em.persist(newBidEntity);

                List<BidEntity> customerBidEntities = customerEntity.getBids();
                customerBidEntities.add(newBidEntity);
                customerEntity.setBids(customerBidEntities);

                List<BidEntity> auctionListingBidEntities = auctionListingEntity.getBids();
                Collections.sort(auctionListingBidEntities);

                // Refunding the previous highest bid's credits to the respective customer!
                if (auctionListingBidEntities.size() > 0) {
                    BidEntity previousHighestBidEntity = auctionListingBidEntities.get(auctionListingBidEntities.size() - 1);
                    CustomerEntity previousHighestBidCustomerEntity = previousHighestBidEntity.getCustomer();

                    TransactionEntity newRefundTransactionEntity = new TransactionEntity();
                    newRefundTransactionEntity.setTimeOfTransaction(new Date());
                    // Refunding a bid has a positive transaction amount
                    newRefundTransactionEntity.setTransactionAmount(previousHighestBidEntity.getBidPrice());
                    newRefundTransactionEntity.setCustomer(previousHighestBidCustomerEntity);
                    newRefundTransactionEntity.setBid(previousHighestBidEntity);

                    transactionSessionBeanLocal.createNewTransaction(previousHighestBidCustomerEntity.getCustomerId(), newRefundTransactionEntity);
                }

                auctionListingBidEntities.add(newBidEntity);
                auctionListingEntity.setBids(auctionListingBidEntities);
                auctionListingEntity.setHighestBidPrice(newBidEntity.getBidPrice());

                TransactionEntity newTransactionEntity = new TransactionEntity();
                newTransactionEntity.setTimeOfTransaction(new Date());
                // Placing a bid has a negative transaction amount
                newTransactionEntity.setTransactionAmount(newBidEntity.getBidPrice().negate());
                newTransactionEntity.setCustomer(customerEntity);
                newTransactionEntity.setBid(newBidEntity);

                Long newTransactionId = transactionSessionBeanLocal.createNewTransaction(customerId, newTransactionEntity);

                return newTransactionId;
            } catch (PersistenceException ex) {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public Long createSmallestNewBid(Long customerId, Long auctionListingId) throws AuctionListingNotFoundException, CustomerNotfoundException, UnknownPersistenceException, InputDataValidationException, InsufficientCreditException, AuctionListingAlreadyClosedException {
        BigDecimal minNextBidPrice = getMinNextBidPrice(auctionListingId);
        CustomerEntity customerEntity = customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);
        AuctionListingEntity auctionListingEntity = auctionListingSessionBeanLocal.retrieveAuctionListingByAuctionListingId(auctionListingId);
        BidEntity bidEntity = new BidEntity(minNextBidPrice, customerEntity, auctionListingEntity);
        
        return createNewBid(customerId, auctionListingId, bidEntity);
    }
    
    @Override
    public void createNewProxyBid(BidEntity newProxyBidEntity) {
        timerService.createTimer(30 * 1000, 30 * 1000, newProxyBidEntity);
    }
    
    @Override
    public void createNewSnipingBid(BidEntity newSnipingBidEntity, Integer minutesBeforeEndDateTime) {
        AuctionListingEntity auctionListingEntity = newSnipingBidEntity.getAuctionListing();
        Date endDateTime = auctionListingEntity.getEndDateTime();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDateTime);
        calendar.add(Calendar.MINUTE, -minutesBeforeEndDateTime);
        
        Date snipingDateTime = calendar.getTime();
        
        timerService.createTimer(snipingDateTime, newSnipingBidEntity);
    }
    
    @Timeout
    public void timeout(Timer timer) {
        BidEntity bidEntity = (BidEntity) timer.getInfo();
        BigDecimal maxBidPrice = bidEntity.getBidPrice();
        
        CustomerEntity customerEntity = bidEntity.getCustomer();
        
        AuctionListingEntity referenceAuctionListingEntity = bidEntity.getAuctionListing();
        AuctionListingEntity updatedAuctionListingEntity = em.find(AuctionListingEntity.class, referenceAuctionListingEntity.getAuctionListingId());
        
        if (updatedAuctionListingEntity != null) {
            if (updatedAuctionListingEntity.getActive()) {
                List<BidEntity> auctionListingBidEntities = updatedAuctionListingEntity.getBids();
                BidEntity highestBidEntity = Collections.max(auctionListingBidEntities);
                
                CustomerEntity highestBidCustomerEntity = highestBidEntity.getCustomer();
                try {
                    BigDecimal nextHighestBidPrice = getMinNextBidPrice(updatedAuctionListingEntity.getAuctionListingId());

                    if (customerEntity.getCustomerId().compareTo(highestBidCustomerEntity.getCustomerId()) != 0 && maxBidPrice.compareTo(nextHighestBidPrice) == 1) {
                        // If (thisCustomerEntity != highestBidCustomerEntity) && (maxBidPrice > nextHighestBidPrice)
                        try {
                            createSmallestNewBid(customerEntity.getCustomerId(), updatedAuctionListingEntity.getAuctionListingId());
                        } catch (AuctionListingAlreadyClosedException | AuctionListingNotFoundException | CustomerNotfoundException | InputDataValidationException | InsufficientCreditException | UnknownPersistenceException ex) {
                            System.out.println(ex.getMessage() + "\n");
                        }
                    }
                } catch (AuctionListingNotFoundException ex) {
                    System.out.println(ex.getMessage() + "\n");
                }
            }
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<BidEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
