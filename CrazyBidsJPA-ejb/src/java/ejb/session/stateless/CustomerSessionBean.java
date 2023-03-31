/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CreditTransactionEntity;
import entity.CustomerEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CustomerAlreadyExistException;
import util.exception.PasswordOrUsernameWrong;

/**
 *
 * @author yeowh
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "CrazyBidsJPA-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public Long createNewCustomer(CustomerEntity customer) {
        em.persist(customer);
        em.flush();
        return customer.getCustomerId();
    }

    @Override
    public CustomerEntity verifyRegisteration(String username, String password) throws CustomerAlreadyExistException {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.username = :username AND c.password = :password");
        query.setParameter("username", username);
        query.setParameter("password", password);
        try {
            CustomerEntity customer = (CustomerEntity) query.getSingleResult();

            if (!customer.getUsername().equals(username)) {
                em.persist(customer);
                em.flush();
                return customer;
            } else {
                throw new CustomerAlreadyExistException("customer already exist!");
            }
        } catch (CustomerAlreadyExistException ex) {
            throw new CustomerAlreadyExistException("Customer already exist!");
        }
    }
    
    @Override
    public void doUpdate(String firstName, String lastName, String username, String password, String email, String contactNumber){
        CustomerEntity customer = em.find(CustomerEntity.class, username);
        customer.setContactNumber(contactNumber);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setUsername(username);
        customer.setPassword(password);
        customer.setEmail(email);
    }

    @Override
    public CustomerEntity verifyCustomerCredential(String username, String password) throws PasswordOrUsernameWrong {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.username = :username AND c.password = :password");
        query.setParameter("username", username);
        query.setParameter("password", password);
        try {
            CustomerEntity customer = (CustomerEntity) query.getSingleResult();;
            if (customer.getUsername().equals(username)
                    && customer.getPassword().equals(password)) {
                return customer;
            } else {
                throw new PasswordOrUsernameWrong("Customer does not exist!");
            }
        } catch (PasswordOrUsernameWrong ex) {
            throw new PasswordOrUsernameWrong("Customer does not exist!");
        }
    }
    
    public CreditTransactionEntity getCreditTransactionHist(String accountNumber){
        try {
            CreditTransactionEntity creditTransactionHist = retrieveDepositAccountByAccountNumber(accountNumber);

            HashMap<String, BigDecimal> availableBalances = new HashMap<>();

            availableBalances.put("availableBalance", depositAccount.getAvailableBalance());

            return availableBalances;

        } catch (DepositAccountNotFoundException ex) {
            throw ex;
        }
    }

}
