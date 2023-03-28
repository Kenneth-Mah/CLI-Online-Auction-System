/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author yeowh
 */
@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean {

    @PersistenceContext(unitName = "CrazyBidsJPA-ejbPU")
    private EntityManager em;
    
    @PostConstruct
    public void postConstruct(){
        
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
