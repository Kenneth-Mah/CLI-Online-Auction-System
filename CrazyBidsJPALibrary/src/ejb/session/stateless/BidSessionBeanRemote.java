/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BidEntity;
import java.math.BigDecimal;
import javax.ejb.Remote;
import util.exception.AuctionListingAlreadyClosedException;
import util.exception.AuctionListingNotFoundException;
import util.exception.CustomerNotfoundException;
import util.exception.InputDataValidationException;
import util.exception.InsufficientCreditException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kenne
 */
@Remote
public interface BidSessionBeanRemote {
    
    public BigDecimal getMinNextBidPrice(Long auctionListingId) throws AuctionListingNotFoundException;
    
    public Long createNewBid(Long customerId, Long auctionListingId, BidEntity newBidEntity) throws CustomerNotfoundException, AuctionListingNotFoundException, UnknownPersistenceException, InputDataValidationException, InsufficientCreditException, AuctionListingAlreadyClosedException;
    
    public Long createSmallestNewBid(Long customerId, Long auctionListingId) throws AuctionListingNotFoundException, CustomerNotfoundException, UnknownPersistenceException, InputDataValidationException, InsufficientCreditException, AuctionListingAlreadyClosedException;

    public void createNewSnipingBid(BidEntity newSnipingBidEntity, Integer minutesBeforeEndDateTime);
    
}