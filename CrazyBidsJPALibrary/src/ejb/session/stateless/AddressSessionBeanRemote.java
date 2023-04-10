/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AddressEntity;
import javax.ejb.Remote;
import util.exception.AddressNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kenne
 */
@Remote
public interface AddressSessionBeanRemote {
    
    public Long createNewAddress(AddressEntity newAddressEntity) throws UnknownPersistenceException, InputDataValidationException;
    
    public AddressEntity retrieveAddressByAddressId(Long addressId) throws AddressNotFoundException;
    
}
