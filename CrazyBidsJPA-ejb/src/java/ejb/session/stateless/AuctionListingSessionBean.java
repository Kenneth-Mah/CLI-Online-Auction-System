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
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
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
import util.exception.UpdateAuctionListingException;

/**
 *
 * @author kenne
 */
@Stateless
public class AuctionListingSessionBean implements AuctionListingSessionBeanRemote, AuctionListingSessionBeanLocal {

    @PersistenceContext(unitName = "CrazyBidsJPA-ejbPU")
    private EntityManager em;

    @Resource
    TimerService timerService;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public AuctionListingSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public Long createNewAuctionListing(AuctionListingEntity newAuctionListingEntity) throws AuctionListingNameExistException, UnknownPersistenceException, InputDataValidationException, InvalidStartAndEndDatesException {
        Date currentDateTime = new Date(System.currentTimeMillis());
        Date startDateTime = newAuctionListingEntity.getStartDateTime();
        Date endDateTime = newAuctionListingEntity.getEndDateTime();
        if ((startDateTime != null && endDateTime != null) && 
                (currentDateTime.compareTo(startDateTime) >= 0 || startDateTime.compareTo(endDateTime) >= 0)) {
            throw new InvalidStartAndEndDatesException("Start Date-time and End Date-time must be in the future and Start Date-time must be before the End Date-time");
        }

        Set<ConstraintViolation<AuctionListingEntity>> constraintViolations = validator.validate(newAuctionListingEntity);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newAuctionListingEntity);
                em.flush();
                
                TimerHandle timerHandle = makeTimer(newAuctionListingEntity, startDateTime);
                newAuctionListingEntity.setTimerHandle(timerHandle);

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

    @Override
    public Boolean isAuctionListingInUse(Long auctionListingId) throws AuctionListingNotFoundException {
        AuctionListingEntity auctionListingEntity = retrieveAuctionListingByAuctionListingId(auctionListingId);

        if (auctionListingEntity.getBids().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateAuctionListing(AuctionListingEntity auctionListingEntity) throws AuctionListingNotFoundException, InputDataValidationException, UpdateAuctionListingException, InvalidStartAndEndDatesException {
        if (auctionListingEntity != null && auctionListingEntity.getAuctionListingId() != null) {
            Set<ConstraintViolation<AuctionListingEntity>> constraintViolations = validator.validate(auctionListingEntity);

            if (constraintViolations.isEmpty()) {
                AuctionListingEntity auctionListingEntityToUpdate = retrieveAuctionListingByAuctionListingId(auctionListingEntity.getAuctionListingId());

                if (auctionListingEntityToUpdate.getAuctionListingName().equals(auctionListingEntity.getAuctionListingName())) {
                    Date currentDateTime = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
                    if (currentDateTime.compareTo(auctionListingEntityToUpdate.getStartDateTime()) < 0) {
                        if (currentDateTime.compareTo(auctionListingEntity.getStartDateTime()) < 0 && auctionListingEntity.getStartDateTime().compareTo(auctionListingEntity.getEndDateTime()) < 0) {
                            auctionListingEntityToUpdate.setStartDateTime(auctionListingEntity.getStartDateTime());
                            auctionListingEntityToUpdate.setEndDateTime(auctionListingEntity.getEndDateTime());
                            auctionListingEntityToUpdate.setReservePrice(auctionListingEntity.getReservePrice());
                            
                            TimerHandle timerhandle = auctionListingEntityToUpdate.getTimerHandle();
                            Timer timer = timerhandle.getTimer();
                            timer.cancel();
                            
                            TimerHandle newTimerHandle = makeTimer(auctionListingEntityToUpdate, auctionListingEntityToUpdate.getStartDateTime());
                            auctionListingEntityToUpdate.setTimerHandle(newTimerHandle);
                        } else {
                            throw new InvalidStartAndEndDatesException("Start Datetime and End Datetime must be in the future and Start Datetime must be before the End Datetime");
                        }
                    } else {
                        throw new UpdateAuctionListingException("Auction listing record to be updated has already started and cannot be updated!");
                    }
                } else {
                    throw new UpdateAuctionListingException("Auction Listing Name of auction listing record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new AuctionListingNotFoundException("Auction Listing ID not provided for auction listing to be updated");
        }
    }

    // NOT COMPLETE!!!
    @Override
    public void deleteAuctionListing(Long auctionListingId) throws AuctionListingNotFoundException {
        if (!isAuctionListingInUse(auctionListingId)) {
            AuctionListingEntity auctionListingEntityToRemove = retrieveAuctionListingByAuctionListingId(auctionListingId);
            
            TimerHandle timerHandle = auctionListingEntityToRemove.getTimerHandle();
            Timer timer = timerHandle.getTimer();
            timer.cancel();

            em.remove(auctionListingEntityToRemove);
        } else {
            AuctionListingEntity auctionListingEntityToDisable = retrieveAuctionListingByAuctionListingId(auctionListingId);
            
            TimerHandle timerHandle = auctionListingEntityToDisable.getTimerHandle();
            Timer timer = timerHandle.getTimer();
            timer.cancel();

            // HERE NEED TO REFUND THE HIGHEST BID'S CREDITS TO THE RESPECTIVE CUSTOMER
            auctionListingEntityToDisable.setTimerHandle(null);
            auctionListingEntityToDisable.setActive(false);
            auctionListingEntityToDisable.setDisabled(true);
        }
    }

    @Override
    public List<AuctionListingEntity> retrieveAllActiveAuctionListing() {
        Query query = em.createQuery("SELECT al FROM AuctionListingEntity al WHERE al.active = TRUE");

        return (List<AuctionListingEntity>) query.getResultList();
    }

    @Override
    public TimerHandle makeTimer(AuctionListingEntity auctionListing, Date expiration) {
        Timer timer = timerService.createTimer(expiration, auctionListing);
        TimerHandle timerHandle = timer.getHandle();
        return timerHandle;
    }

    @Timeout
    @Override
    public void timeout(Timer timer) {
        AuctionListingEntity auctionListing = (AuctionListingEntity) timer.getInfo();
        
        AuctionListingEntity auctionListingToUpdate = em.find(AuctionListingEntity.class, auctionListing.getAuctionListingId());
        Boolean active = auctionListingToUpdate.getActive();
        
        if (!active) {
            auctionListingToUpdate.setActive(true);
            TimerHandle timerHandle = makeTimer(auctionListingToUpdate, auctionListingToUpdate.getEndDateTime());
            auctionListingToUpdate.setTimerHandle(timerHandle);
        } else {
            auctionListingToUpdate.setActive(false);
            auctionListingToUpdate.setTimerHandle(null);
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
