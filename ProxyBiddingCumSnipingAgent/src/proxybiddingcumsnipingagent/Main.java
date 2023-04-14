/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxybiddingcumsnipingagent;

import ws.soap.customer.CustomerWebService;
import ws.soap.customer.CustomerWebService_Service;

/**
 *
 * @author kenne
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        CustomerWebService_Service service = new CustomerWebService_Service();
        CustomerWebService port = service.getCustomerWebServicePort();
        
        MainApp mainApp = new MainApp(port);
        mainApp.runApp();
    }
    
}
