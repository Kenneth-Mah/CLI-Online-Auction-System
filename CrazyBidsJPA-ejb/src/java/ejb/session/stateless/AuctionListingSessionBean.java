/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AuctionListingEntity;
import java.util.Date;
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
import util.exception.AuctionListingNameExistException;
import util.exception.AuctionListingNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidStartAndEndDatesException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kenne
 */
@Stateless
public class AuctionListingSessionBean implements AuctionListingSessionBeanRemote, AuctionListingSessionBeanLocal {

    @PersistenceContext(unitName = "CrazyBidsJPA-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public AuctionListingSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public Long createNewAuctionListing(AuctionListingEntity newAuctionListingEntity, Date currentDateTime) throws AuctionListingNameExistException, UnknownPersistenceException, InputDataValidationException, InvalidStartAndEndDatesException {
        Date startDateTime = newAuctionListingEntity.getStartDateTime();
        Date endDateTime = newAuctionListingEntity.getEndDateTime();
        if ((startDateTime != null && endDateTime != null) && (currentDateTime.compareTo(startDateTime) >= 0 || startDateTime.compareTo(endDateTime) >= 0)) {
            throw new InvalidStartAndEndDatesException("Start Datetime and End Datetime must be in the future and Start Datetime must be before the End Datetime");
        }

        Set<ConstraintViolation<AuctionListingEntity>> constraintViolations = validator.validate(newAuctionListingEntity);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newAuctionListingEntity);
                em.flush();

                return newAuctionListingEntity.getAuctionListingId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new AuctionListingNameExistException();
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
    public List<AuctionListingEntity> retrieveAllAuctionListings() {
        Query query = em.createQuery("SELECT al FROM AuctionListingEntity al");

        return query.getResultList();
    }

    @Override
    public AuctionListingEntity retrieveAuctionListingByAuctionListingId(Long auctionListingId) throws AuctionListingNotFoundException {
        AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, auctionListingId);

        if (auctionListingEntity != null) {
            return auctionListingEntity;
        } else {
            throw new AuctionListingNotFoundException("Auction Listing ID " + auctionListingId + " does not exist!");
        }
    }

    @Override
    public AuctionListingEntity retrieveAuctionListingByAuctionListingName(String auctionListingName) throws AuctionListingNotFoundException {
        Query query = em.createQuery("SELECT al FROM AuctionListingEntity al WHERE al.auctionListingName = :inAuctionListingName");
        query.setParameter("inAuctionListingName", auctionListingName);

        try {
            return (AuctionListingEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new AuctionListingNotFoundException("Auction Listing " + auctionListingName + " does not exist!");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<AuctionListingEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
