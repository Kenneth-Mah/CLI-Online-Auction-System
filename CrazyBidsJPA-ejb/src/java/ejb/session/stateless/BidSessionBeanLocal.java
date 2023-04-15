/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BidEntity;
import java.math.BigDecimal;
import javax.ejb.Local;
import util.exception.AuctionListingNotFoundException;
import util.exception.CustomerNotfoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kenne
 */
@Local
public interface BidSessionBeanLocal {
    
    public BigDecimal getMinNextBidPrice(Long auctionListingId) throws AuctionListingNotFoundException;

    public Long createNewBid(Long customerId, Long auctionListingId, BidEntity newBidEntity) throws CustomerNotfoundException, AuctionListingNotFoundException, UnknownPersistenceException, InputDataValidationException;
    
}
