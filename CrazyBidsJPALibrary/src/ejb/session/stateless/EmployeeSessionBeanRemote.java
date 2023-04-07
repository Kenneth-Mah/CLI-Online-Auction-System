/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import javax.ejb.Remote;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeowh
 */
@Remote
public interface EmployeeSessionBeanRemote {
    
    public Long createNewEmployee(EmployeeEntity newEmployeeEntity) throws EmployeeUsernameExistException, UnknownPersistenceException, InputDataValidationException;
    
    public EmployeeEntity retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException;
    
    public EmployeeEntity employeeLogin(String username, String password) throws InvalidLoginCredentialException;
    
}
