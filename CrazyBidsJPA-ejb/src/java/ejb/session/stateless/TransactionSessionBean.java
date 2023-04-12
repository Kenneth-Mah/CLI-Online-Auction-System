/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import entity.TransactionEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CustomerNotfoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kenne
 */
@Stateless
public class TransactionSessionBean implements TransactionSessionBeanRemote, TransactionSessionBeanLocal {

    @PersistenceContext(unitName = "CrazyBidsJPA-ejbPU")
    private EntityManager em;
    
    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public TransactionSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public Long createNewTransaction(Long customerId, TransactionEntity newTransactionEntity) throws CustomerNotfoundException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<TransactionEntity>> constraintViolations = validator.validate(newTransactionEntity);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newTransactionEntity);
                em.flush();
                
                CustomerEntity customerEntity = customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);
                
                List<TransactionEntity> transactionEntities = customerEntity.getTransactions();
                transactionEntities.add(newTransactionEntity);
                customerEntity.setTransactions(transactionEntities);
                
                BigDecimal customerAvailableBalance = customerEntity.getAvailableBalance();
                
                // Remember, BIDS should store NEGATIVE transactionAmount. Then this code will work logically
                customerAvailableBalance = customerAvailableBalance.add(newTransactionEntity.getTransactionAmount());
                customerEntity.setAvailableBalance(customerAvailableBalance);

                return newTransactionEntity.getTransactionid();
            } catch (PersistenceException ex) {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<TransactionEntity> retrieveAllTransactionsByCustomerId(Long customerId) {
        Query query = em.createQuery("SELECT t FROM TransactionEntity t WHERE t.customer.customerId = :inCustomerId");
        query.setParameter("inCustomerId", customerId);
        
        return query.getResultList();
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<TransactionEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
    
}
