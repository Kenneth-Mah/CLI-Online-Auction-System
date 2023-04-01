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
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CreditPackageNotFoundException;

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
    public List<CreditPackageEntity> retrieveAllAvailableCreditPackage() {
        Query query = em.createQuery("SELECT c FROM CreditPackageEntity c WHERE c.active = TRUE");
        List<CreditPackageEntity> creditPackages = query.getResultList();
        return creditPackages;
    }
    
    @Override
    public CreditPackageEntity retrieveCreditPackageByCreditPackageType(String creditPackageType) throws CreditPackageNotFoundException {
        Query query = em.createQuery("SELECT c FROM CreditPackageEntity c WHERE c.creditPackageType = :inCreditPackageType");
        query.setParameter("inCreditPackageType", creditPackageType);
        
        try {
            return (CreditPackageEntity)query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CreditPackageNotFoundException("Credit Package " + creditPackageType + " does not exist!");
        }
    }
}
