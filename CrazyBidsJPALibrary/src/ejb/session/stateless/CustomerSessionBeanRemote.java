/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import javax.ejb.Remote;
import util.exception.CustomerAlreadyExistException;
import util.exception.PasswordOrUsernameWrong;

/**
 *
 * @author yeowh
 */
@Remote
public interface CustomerSessionBeanRemote {
    
    public Long createNewCustomer(CustomerEntity customer);
    
    public CustomerEntity verifyCustomerCredential(String username, String password) throws PasswordOrUsernameWrong;
    
    public CustomerEntity verifyRegisteration(String username, String password) throws CustomerAlreadyExistException;
    
    public void doUpdate(String firstName, String lastName, String username, String password, String email, String contactNumber);
}
