/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.soap;

import ejb.session.stateless.AuctionListingSessionBeanLocal;
import ejb.session.stateless.BidSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.AddressEntity;
import entity.AuctionListingEntity;
import entity.BidEntity;
import entity.CustomerEntity;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.AuctionListingNotFoundException;
import util.exception.CustomerNotfoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidPremiumRegistrationException;

/**
 *
 * @author kenne
 */
@WebService(serviceName = "CustomerWebService")
@Stateless()
public class CustomerWebService {

    @PersistenceContext(unitName = "CrazyBidsJPA-ejbPU")
    private EntityManager em;
    
    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    private AuctionListingSessionBeanLocal auctionListingSessionBeanLocal;
    @EJB
    private BidSessionBeanLocal bidSessionBeanLocal;
    

    @WebMethod(operationName = "customerPremiumRegistration")
    public void customerPremiumRegistration(@WebParam(name = "username") String username,
                                            @WebParam(name = "password") String password) throws InvalidPremiumRegistrationException {
        customerSessionBeanLocal.customerPremiumRegistration(username, password);
    }

    @WebMethod(operationName = "customerRemoteLogin")
    public CustomerEntity customerRemoteLogin(@WebParam(name = "username") String username,
                                                @WebParam(name = "password") String password) throws InvalidLoginCredentialException {
        CustomerEntity customerEntity = customerSessionBeanLocal.customerRemoteLogin(username, password);
        
        em.detach(customerEntity);
        
        customerEntity.setAddresses(new ArrayList<>());
        customerEntity.setWonAuctions(new ArrayList<>());
        customerEntity.setBids(new ArrayList<>());
        customerEntity.setTransactions(new ArrayList<>());
        
        return customerEntity;
    }
    
    @WebMethod(operationName = "retrieveCustomerByCustomerId")
    public CustomerEntity retrieveCustomerByCustomerId(@WebParam(name = "customerId") Long customerId) throws CustomerNotfoundException {
        CustomerEntity customerEntity = customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);
        
        em.detach(customerEntity);
        
        customerEntity.setAddresses(new ArrayList<>());
        customerEntity.setTransactions(new ArrayList<>());
        customerEntity.setBids(new ArrayList<>());
        
        for (AuctionListingEntity auctionListingEntity : customerEntity.getWonAuctions()) {
            em.detach(auctionListingEntity);
            
            auctionListingEntity.setWinningBid(null);
            auctionListingEntity.setBids(new ArrayList<>());
            
            AddressEntity addressEntity = auctionListingEntity.getAddress();
            if (addressEntity != null) {
                em.detach(addressEntity);

                addressEntity.setWonAuctions(new ArrayList<>());
            }
        }
        
        return customerEntity;
    }
    
    @WebMethod(operationName = "retrieveAuctionListingByAuctionListingName")
    public AuctionListingEntity retrieveAuctionListingByAuctionListingName(@WebParam(name = "auctionListingName") String auctionListingName) throws AuctionListingNotFoundException {
        AuctionListingEntity auctionListingEntity = auctionListingSessionBeanLocal.retrieveAuctionListingByAuctionListingName(auctionListingName);
        
        em.detach(auctionListingEntity);
        
        auctionListingEntity.setWinningBid(null);
        auctionListingEntity.setBids(new ArrayList<>());

        AddressEntity addressEntity = auctionListingEntity.getAddress();
        if (addressEntity != null) {
            em.detach(addressEntity);

            addressEntity.setWonAuctions(new ArrayList<>());
        }
        
        return auctionListingEntity;
    }
    
    @WebMethod(operationName = "retrieveAllActiveAuctionListings")
    public List<AuctionListingEntity> retrieveAllActiveAuctionListings() {
        List<AuctionListingEntity> auctionListingEntities = auctionListingSessionBeanLocal.retrieveAllActiveAuctionListings();
        
        for (AuctionListingEntity auctionListingEntity : auctionListingEntities) {
            em.detach(auctionListingEntity);
            
            auctionListingEntity.setWinningBid(null);
            auctionListingEntity.setBids(new ArrayList<>());
            
            AddressEntity addressEntity = auctionListingEntity.getAddress();
            if (addressEntity != null) {
                em.detach(addressEntity);

                addressEntity.setWonAuctions(new ArrayList<>());
            }
        }
        
        return auctionListingEntities;
    }
    
    @WebMethod(operationName = "createNewSnipingBid")
    public void createNewSnipingBid(@WebParam(name = "newSnipingBidEntity") BidEntity newSnipingBidEntity,
                                    @WebParam(name = "minutesBeforeEndDateTime") Integer minutesBeforeEndDateTime) {
        bidSessionBeanLocal.createNewSnipingBid(newSnipingBidEntity, minutesBeforeEndDateTime);
    }
    
}
