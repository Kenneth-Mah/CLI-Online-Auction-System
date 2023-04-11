/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AddressEntity;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AddressNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateAddressException;

/**
 *
 * @author kenne
 */
@Stateless
public class AddressSessionBean implements AddressSessionBeanRemote, AddressSessionBeanLocal {

    @PersistenceContext(unitName = "CrazyBidsJPA-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public AddressSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public Long createNewAddress(AddressEntity newAddressEntity) throws UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<AddressEntity>> constraintViolations = validator.validate(newAddressEntity);
        
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newAddressEntity);
                em.flush();
                
                return newAddressEntity.getAddressId();
            } catch (PersistenceException ex) {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public AddressEntity retrieveAddressByAddressId(Long addressId) throws AddressNotFoundException {
        AddressEntity addressEntity = em.find(AddressEntity.class, addressId);
        
        if (addressEntity != null) {
            return addressEntity;
        } else {
            throw new AddressNotFoundException("Address ID " + addressId + " does not exist!");
        }
    }
    
    // Not adding retrieveAddressByAddressName because addressName is not unique!
    
    @Override
    public Boolean isAddressInUse(Long addressId) throws AddressNotFoundException {
        AddressEntity addressEntity = retrieveAddressByAddressId(addressId);
        
        if (addressEntity.getWonAuctions().size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public void updateAddress(AddressEntity addressEntity) throws AddressNotFoundException, UpdateAddressException, InputDataValidationException {
        if (addressEntity != null && addressEntity.getAddressId()!= null) {
            Set<ConstraintViolation<AddressEntity>> constraintViolations = validator.validate(addressEntity);

            if (constraintViolations.isEmpty()) {
                AddressEntity addressEntityToUpdate = retrieveAddressByAddressId(addressEntity.getAddressId());
                if (isAddressInUse(addressEntityToUpdate.getAddressId())) {
                    throw new UpdateAddressException("Address record to be updated is in use and cannot be updated!");
                } else if (!addressEntityToUpdate.getActive()) {
                    throw new UpdateAddressException("Address record to be updated has been disabled and cannot be updated!");
                } else {
                    addressEntityToUpdate.setAddressName(addressEntity.getAddressName());
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new AddressNotFoundException("Address ID not provided for address to be updated");
        }
    }
    
    // deleteAddress() is in CustomerSessionBean as we have to unbind the relationship before deletion
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<AddressEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
    
}
