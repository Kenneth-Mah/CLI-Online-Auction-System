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
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
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
import util.exception.AuctionListingNotFoundException;
import util.exception.CustomerNotfoundException;
import util.exception.InputDataValidationException;
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
    public Long createNewBid(Long customerId, Long auctionListingId, BidEntity newBidEntity) throws CustomerNotfoundException, AuctionListingNotFoundException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<BidEntity>> constraintViolations = validator.validate(newBidEntity);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newBidEntity);
                em.flush();
                
                CustomerEntity customerEntity = customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);
                AuctionListingEntity auctionListingEntity = auctionListingSessionBeanLocal.retrieveAuctionListingByAuctionListingId(auctionListingId);
                
                List<BidEntity> customerBidEntities = customerEntity.getBids();
                customerBidEntities.add(newBidEntity);
                customerEntity.setBids(customerBidEntities);
                
                PriorityQueue<BidEntity> auctionListingBidEntities = auctionListingEntity.getBids();
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
                
                // Need to refund the previous highest bid's credits to the respective customer!
                // Refunding a bid has a positive transaction amount

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
