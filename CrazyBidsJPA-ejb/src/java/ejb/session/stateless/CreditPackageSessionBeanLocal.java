/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CreditPackageEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreditPackageNotFoundException;
import util.exception.CreditPackageTypeExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeowh
 */
@Local
public interface CreditPackageSessionBeanLocal {
    
    public Long createNewCreditPackage(CreditPackageEntity newCreditPackageEntity) throws CreditPackageTypeExistException, UnknownPersistenceException, InputDataValidationException;

    public List<CreditPackageEntity> retrieveAllCreditPackages();
    
    public List<CreditPackageEntity> retrieveAllAvailableCreditPackages();

    public CreditPackageEntity retrieveCreditPackageByCreditPackageType(String creditPackageType) throws CreditPackageNotFoundException;
    
}
