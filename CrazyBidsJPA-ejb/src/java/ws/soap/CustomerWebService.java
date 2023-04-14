/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.soap;

import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.AddressEntity;
import entity.AuctionListingEntity;
import entity.CustomerEntity;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
            em.detach(addressEntity);
            
            addressEntity.setWonAuctions(new ArrayList<>());
        }
        
        return customerEntity;
    }
    
}
