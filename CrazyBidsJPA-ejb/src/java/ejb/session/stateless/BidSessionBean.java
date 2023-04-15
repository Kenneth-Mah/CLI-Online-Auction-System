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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
    public Long createNewBid(Long customerId, Long auctionListingId, BidEntity newBidEntity) throws CustomerNotfoundException, AuctionListingNotFoundException, UnknownPersistenceException, InputDataValidationException, InsufficientCreditException, AuctionListingAlreadyClosedException {
        Set<ConstraintViolation<BidEntity>> constraintViolations = validator.validate(newBidEntity);

        if (constraintViolations.isEmpty()) {
            try {
                CustomerEntity customerEntity = customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);
                AuctionListingEntity auctionListingEntity = auctionListingSessionBeanLocal.retrieveAuctionListingByAuctionListingId(auctionListingId);
                
                if (customerEntity.getAvailableBalance().compareTo(newBidEntity.getBidPrice()) == -1){
                    throw new InsufficientCreditException("You do not have enough balancce in your wallet." + "\n");
                }
                if (Objects.equals(auctionListingEntity.getActive(), "false")){
                    throw new AuctionListingAlreadyClosedException("Auction Listing is closed " + "\n");
                }
                em.persist(newBidEntity);
                em.flush();
                
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

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<BidEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
    
}
