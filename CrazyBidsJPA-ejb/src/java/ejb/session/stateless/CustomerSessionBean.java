/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AddressEntity;
import entity.CustomerEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "CrazyBidsJPA-ejbPU")
    private EntityManager em;
    
    @EJB
    private AddressSessionBeanLocal addressSessionBeanLocal;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CustomerSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewCustomer(CustomerEntity newCustomerEntity) throws CustomerUsernameExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<CustomerEntity>> constraintViolations = validator.validate(newCustomerEntity);
        
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newCustomerEntity);
                em.flush();
                
                return newCustomerEntity.getCustomerId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CustomerUsernameExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public CustomerEntity retrieveCustomerByCustomerId(Long customerId) throws CustomerNotfoundException {
        CustomerEntity customerEntity = em.find(CustomerEntity.class, customerId);

        if (customerEntity != null) {
            return customerEntity;
        } else {
            throw new CustomerNotfoundException("Customer ID " + customerId + " does not exist!");
        }
    }
    
    @Override
    public CustomerEntity retrieveCustomerByUsername(String username) throws CustomerNotfoundException {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try {
            return (CustomerEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CustomerNotfoundException("Employee Username " + username + " does not exist!");
        }
    }

    @Override
    public CustomerEntity customerLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            CustomerEntity customerEntity = retrieveCustomerByUsername(username);

            if (customerEntity.getPassword().equals(password)) {
                return customerEntity;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (CustomerNotfoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
    
    @Override
    public void updateCustomer(CustomerEntity customerEntity) throws CustomerNotfoundException, UpdateCustomerException, InputDataValidationException {
        if (customerEntity != null && customerEntity.getCustomerId()!= null) {
            Set<ConstraintViolation<CustomerEntity>> constraintViolations = validator.validate(customerEntity);

            if (constraintViolations.isEmpty()) {
                CustomerEntity customerEntityToUpdate = retrieveCustomerByCustomerId(customerEntity.getCustomerId());

                if (customerEntityToUpdate.getUsername().equals(customerEntity.getUsername())) {
                    customerEntityToUpdate.setFirstName(customerEntity.getFirstName());
                    customerEntityToUpdate.setLastName(customerEntity.getLastName());
                    // Username and password are deliberately NOT updated to demonstrate that client is not allowed to update account credential through this business method
                } else {
                    throw new UpdateCustomerException ("Username of customer record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new CustomerNotfoundException("Customer ID not provided for customer to be updated");
        }
    }
    
    @Override
    public CustomerEntity addAddressToCustomer(Long customerId, Long addressId) throws CustomerNotfoundException, AddressNotFoundException {
        CustomerEntity customerEntity = retrieveCustomerByCustomerId(customerId);
        AddressEntity addressEntity = addressSessionBeanLocal.retrieveAddressByAddressId(addressId);
        
        List<AddressEntity> addressEntities = customerEntity.getAddresses();
        addressEntities.add(addressEntity);
        customerEntity.setAddresses(addressEntities);
        return customerEntity;
    }
    
    @Override
    public AddressEntity retrieveAddressByCustomerIdAndAddressId(Long customerId, Long addressId) throws CustomerNotfoundException, AddressNotFoundException {
        CustomerEntity customerEntity = retrieveCustomerByCustomerId(customerId);
        AddressEntity addressEntity = addressSessionBeanLocal.retrieveAddressByAddressId(addressId);
        
        if (customerEntity.getAddresses().contains(addressEntity)) {
            return addressEntity;
        } else {
            throw new AddressNotFoundException("Address ID " + addressId + " does not exist!");
        }
    }
    
    @Override
    public List<AddressEntity> retrieveAllAddressesByCustomerId(Long customerId) throws CustomerNotfoundException {
        CustomerEntity customerEntity = retrieveCustomerByCustomerId(customerId);
        List<AddressEntity> addressEntities = customerEntity.getAddresses();
        addressEntities.size();
        return addressEntities;
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CustomerEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
    
}
