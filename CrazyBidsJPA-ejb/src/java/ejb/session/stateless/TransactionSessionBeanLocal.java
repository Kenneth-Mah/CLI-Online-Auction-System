/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.TransactionEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.CustomerNotfoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kenne
 */
@Local
public interface TransactionSessionBeanLocal {

    public Long createNewTransaction(Long customerId, TransactionEntity newTransactionEntity) throws CustomerNotfoundException, UnknownPersistenceException, InputDataValidationException;

    public List<TransactionEntity> retrieveAllTransactionsByCustomerId(Long customerId);
    
}
