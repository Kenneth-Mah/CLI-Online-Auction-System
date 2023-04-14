/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.soap;

import ejb.session.stateless.CustomerSessionBeanLocal;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.InvalidPremiumRegistrationException;

/**
 *
 * @author kenne
 */
@WebService(serviceName = "CustomerWebService")
@Stateless()
public class CustomerWebService {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "customerPremiumRegistration")
    public void customerPremiumRegistration(@WebParam(name = "username") String username,
                                            @WebParam(name = "password") String password) throws InvalidPremiumRegistrationException {
        customerSessionBeanLocal.customerPremiumRegistration(username, password);
    }
}
