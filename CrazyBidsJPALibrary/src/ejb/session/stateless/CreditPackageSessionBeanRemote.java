/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CreditPackageEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CreditPackageNotFoundException;
import util.exception.CreditPackageTypeExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeowh
 */
@Remote
public interface CreditPackageSessionBeanRemote {
    
    public CreditPackageEntity createNewCreditPackage(CreditPackageEntity newCreditPackageEntity) throws CreditPackageTypeExistException, UnknownPersistenceException, InputDataValidationException;

    public List<CreditPackageEntity> retrieveAllAvailableCreditPackage();
    
    public CreditPackageEntity retrieveCreditPackageByCreditPackageType(String creditPackageType) throws CreditPackageNotFoundException;
    
}
