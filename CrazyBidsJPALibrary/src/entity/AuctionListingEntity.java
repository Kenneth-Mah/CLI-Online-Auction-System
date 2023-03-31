/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author yeowh
 */
@Entity
public class AuctionListingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionId;
    private BigDecimal highestBidPrice;
    private Date startDateTime;
    private Date endDateTime;
    private BigDecimal reservePrice;
    private String auctionName;
    private Boolean active;
    private Boolean requiresManualIntervention;
    
    private BidEntity winningBid;
    
    private AddressEntity address;
    
    private List<BidEntity> bids;

    public AuctionListingEntity() {
        bids = new ArrayList<>();
    }

    public AuctionListingEntity(BigDecimal highestBidPrice, Date startDateTime, Date endDateTime, BigDecimal reservePrice, String auctionName, Boolean active, Boolean requiresManualIntervention, BidEntity winningBid, AddressEntity address) {
        this.highestBidPrice = highestBidPrice;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.reservePrice = reservePrice;
        this.auctionName = auctionName;
        this.active = active;
        this.requiresManualIntervention = requiresManualIntervention;
        this.winningBid = winningBid;
        this.address = address;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (auctionId != null ? auctionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the auctionId fields are not set
        if (!(object instanceof AuctionListingEntity)) {
            return false;
        }
        AuctionListingEntity other = (AuctionListingEntity) object;
        if ((this.auctionId == null && other.auctionId != null) || (this.auctionId != null && !this.auctionId.equals(other.auctionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AuctionListingEntity[ id=" + auctionId + " ]";
    }

    /**
     * @return the highestBidPrice
     */
    public BigDecimal getHighestBidPrice() {
        return highestBidPrice;
    }

    /**
     * @param highestBidPrice the highestBidPrice to set
     */
    public void setHighestBidPrice(BigDecimal highestBidPrice) {
        this.highestBidPrice = highestBidPrice;
    }

    /**
     * @return the startDateTime
     */
    public Date getStartDateTime() {
        return startDateTime;
    }

    /**
     * @param startDateTime the startDateTime to set
     */
    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    /**
     * @return the endDateTime
     */
    public Date getEndDateTime() {
        return endDateTime;
    }

    /**
     * @param endDateTime the endDateTime to set
     */
    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    /**
     * @return the reservePrice
     */
    public BigDecimal getReservePrice() {
        return reservePrice;
    }

    /**
     * @param reservePrice the reservePrice to set
     */
    public void setReservePrice(BigDecimal reservePrice) {
        this.reservePrice = reservePrice;
    }

    /**
     * @return the auctionName
     */
    public String getAuctionName() {
        return auctionName;
    }

    /**
     * @param auctionName the auctionName to set
     */
    public void setAuctionName(String auctionName) {
        this.auctionName = auctionName;
    }

    /**
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return the requiresManualIntervention
     */
    public Boolean getRequiresManualIntervention() {
        return requiresManualIntervention;
    }

    /**
     * @param requiresManualIntervention the requiresManualIntervention to set
     */
    public void setRequiresManualIntervention(Boolean requiresManualIntervention) {
        this.requiresManualIntervention = requiresManualIntervention;
    }

    /**
     * @return the winningBid
     */
    public BidEntity getWinningBid() {
        return winningBid;
    }

    /**
     * @param winningBid the winningBid to set
     */
    public void setWinningBid(BidEntity winningBid) {
        this.winningBid = winningBid;
    }

    /**
     * @return the address
     */
    public AddressEntity getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    /**
     * @return the bids
     */
    public List<BidEntity> getBids() {
        return bids;
    }

    /**
     * @param bids the bids to set
     */
    public void setBids(List<BidEntity> bids) {
        this.bids = bids;
    }
    
}
