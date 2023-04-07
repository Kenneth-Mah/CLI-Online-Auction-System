/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.EmployeeEntity;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.enumeration.EmployeeTypeEnum;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeowh
 */
@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean {
    
    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    public DataInitializationSessionBean() {
    }
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @PostConstruct
    public void postConstruct(){
        try {
            employeeSessionBeanLocal.retrieveEmployeeByUsername("manager");
        } catch (EmployeeNotFoundException ex) {
            initializeData();
        }
    }
    
    private void initializeData() {
        try {
            employeeSessionBeanLocal.createNewEmployee(new EmployeeEntity("Default", "Manager", "manager", "password", EmployeeTypeEnum.ADMIN));
        } catch (EmployeeUsernameExistException | UnknownPersistenceException | InputDataValidationException ex) {
            ex.printStackTrace();
        }
    }
    
}
