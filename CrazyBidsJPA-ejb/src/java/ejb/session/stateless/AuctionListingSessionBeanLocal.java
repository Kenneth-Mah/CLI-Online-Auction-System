/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AuctionListingEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.AuctionListingNameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidStartAndEndDatesException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kenne
 */
@Local
public interface AuctionListingSessionBeanLocal {

    public Long createNewAuctionListing(AuctionListingEntity newAuctionListingEntity, Date currentDateTime) throws AuctionListingNameExistException, UnknownPersistenceException, InputDataValidationException, InvalidStartAndEndDatesException;

    public List<AuctionListingEntity> retrieveAllAuctionListings();
    
}
