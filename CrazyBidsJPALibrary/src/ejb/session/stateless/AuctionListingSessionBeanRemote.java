/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AuctionListingEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AuctionListingNameExistException;
import util.exception.AuctionListingNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidStartAndEndDatesException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateAuctionListingException;

/**
 *
 * @author kenne
 */
@Remote
public interface AuctionListingSessionBeanRemote {
    
    public Long createNewAuctionListing(AuctionListingEntity newAuctionListingEntity) throws AuctionListingNameExistException, UnknownPersistenceException, InputDataValidationException, InvalidStartAndEndDatesException;
    
    public List<AuctionListingEntity> retrieveAllAuctionListings();
    
    public AuctionListingEntity retrieveAuctionListingByAuctionListingId(Long auctionListingId) throws AuctionListingNotFoundException;
    
    public AuctionListingEntity retrieveAuctionListingByAuctionListingName(String auctionListingName) throws AuctionListingNotFoundException;
    
    public Boolean isAuctionListingInUse(Long auctionListingId) throws AuctionListingNotFoundException;
    
    public void updateAuctionListing(AuctionListingEntity auctionListingEntity) throws AuctionListingNotFoundException, InputDataValidationException, UpdateAuctionListingException, InvalidStartAndEndDatesException;
    
    public void deleteAuctionListing(Long auctionListingId) throws AuctionListingNotFoundException;
    
    public List<AuctionListingEntity> retrieveAllAvailableAuctionListing() throws AuctionListingNotFoundException;
    
}
