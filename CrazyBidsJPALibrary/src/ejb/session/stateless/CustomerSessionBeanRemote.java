/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AddressEntity;
import entity.CustomerEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AddressNotFoundException;
import util.exception.CustomerNotfoundException;
import util.exception.CustomerUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateCustomerException;

/**
 *
 * @author yeowh
 */
@Remote
public interface CustomerSessionBeanRemote {
    
    public Long createNewCustomer(CustomerEntity customer) throws CustomerUsernameExistException, UnknownPersistenceException, InputDataValidationException;
    
    public CustomerEntity retrieveCustomerByCustomerId(Long customerId) throws CustomerNotfoundException;
    
    public CustomerEntity retrieveCustomerByUsername(String username) throws CustomerNotfoundException;
    
    public CustomerEntity customerLogin(String username, String password) throws InvalidLoginCredentialException;
    
    public void updateCustomer(CustomerEntity customerEntity) throws CustomerNotfoundException, UpdateCustomerException, InputDataValidationException;
    
    public CustomerEntity addAddressToCustomer(Long customerId, Long addressId) throws CustomerNotfoundException, AddressNotFoundException;
    
    public List<AddressEntity> retrieveAllAddressesByCustomerId(Long customerId) throws CustomerNotfoundException;
    
}
