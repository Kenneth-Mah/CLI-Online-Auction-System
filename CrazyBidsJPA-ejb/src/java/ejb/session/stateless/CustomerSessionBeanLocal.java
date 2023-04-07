/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import javax.ejb.Local;
import util.exception.CustomerNotfoundException;
import util.exception.CustomerUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeowh
 */
@Local
public interface CustomerSessionBeanLocal {

    public Long createNewCustomer(CustomerEntity customer) throws CustomerUsernameExistException, UnknownPersistenceException, InputDataValidationException;

    public CustomerEntity retrieveCustomerByUsername(String username) throws CustomerNotfoundException;
    
    public CustomerEntity customerLogin(String username, String password) throws InvalidLoginCredentialException;

    public CustomerEntity verifyRegisteration(String username, String password) throws CustomerUsernameExistException;

    public void doUpdate(String firstName, String lastName, String username, String password);
    
}
