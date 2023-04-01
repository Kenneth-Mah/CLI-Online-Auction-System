/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CreditPackageEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author yeowh
 */
@Stateless
public class CreditPackageSessionBean implements CreditPackageSessionBeanRemote, CreditPackageSessionBeanLocal {

    @PersistenceContext(unitName = "CrazyBidsJPA-ejbPU")
    private EntityManager em;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public List<CreditPackageEntity> retrieveAllAvailableCreditPacakage() {
        Query query = em.createQuery("SELECT c FROM CreditPackageEntity c WHERE c.active = true");
        List<CreditPackageEntity> creditPackages = query.getResultList();
        return creditPackages;
    }
    
    public void findCreditPackage(){
        
    }
}
