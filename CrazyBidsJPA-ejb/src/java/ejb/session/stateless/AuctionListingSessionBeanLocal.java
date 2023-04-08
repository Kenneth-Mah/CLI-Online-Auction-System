/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AuctionListingEntity;
import javax.ejb.Local;
import util.exception.AuctionListingNameExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kenne
 */
@Local
public interface AuctionListingSessionBeanLocal {

    public Long createNewAuctionListing(AuctionListingEntity newAuctionListingEntity) throws AuctionListingNameExistException, UnknownPersistenceException, InputDataValidationException;
    
}
