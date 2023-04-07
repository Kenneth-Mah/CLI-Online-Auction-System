/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CreditPackageEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreditPackageNotFoundException;
import util.exception.CreditPackageTypeExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeowh
 */
@Stateless
public class CreditPackageSessionBean implements CreditPackageSessionBeanRemote, CreditPackageSessionBeanLocal {

    @PersistenceContext(unitName = "CrazyBidsJPA-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CreditPackageSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewCreditPackage(CreditPackageEntity newCreditPackageEntity) throws CreditPackageTypeExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<CreditPackageEntity>> constraintViolations = validator.validate(newCreditPackageEntity);
        
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newCreditPackageEntity);
                em.flush();
                
                return newCreditPackageEntity.getCreditPackageId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CreditPackageTypeExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
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
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CreditPackageEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
    
}
