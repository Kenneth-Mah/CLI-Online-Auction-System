/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AddressEntity;
import javax.ejb.Local;
import util.exception.AddressNotFoundException;
import util.exception.AuctionListingNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateAddressException;

/**
 *
 * @author kenne
 */
@Local
public interface AddressSessionBeanLocal {

    public Long createNewAddress(AddressEntity newAddressEntity) throws UnknownPersistenceException, InputDataValidationException;

    public AddressEntity retrieveAddressByAddressId(Long addressId) throws AddressNotFoundException;

    public Boolean isAddressInUse(Long addressId) throws AddressNotFoundException;

    public void updateAddress(AddressEntity addressEntity) throws AddressNotFoundException, UpdateAddressException, InputDataValidationException;

    public void selectDeliveryAddressForWonAuctionListing(Long addressId, Long auctionListingId) throws AddressNotFoundException, AuctionListingNotFoundException;
    
}
