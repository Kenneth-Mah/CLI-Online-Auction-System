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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author yeowh
 */
@Entity
public class AuctionListingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionListingId;
    @Column(nullable = false, unique = true, length = 32)
    @NotNull
    @Size(min = 1, max = 32)
    private String auctionListingName;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    // NOTE: startDateTime cannot be @Future if you want to facilitate Update Auction Listing
    // Have to check that startDateTime is after currentDateTime upon creation
    // Have to check that startDateTime is after currentDateTime in order to change startDateTime
    private Date startDateTime;
    @Temporal(TemporalType.TIMESTAMP)
    @Future
    @Column(nullable = false)
    @NotNull
    private Date endDateTime;
    @Column(precision = 18, scale = 4)
    @DecimalMin("0.0000")
    @Digits(integer = 14, fraction = 4)
    // NOTE: reservePrice can be null!
    private BigDecimal reservePrice;
    @Column(nullable = false, precision = 18, scale = 4)
    @NotNull
    @DecimalMin("0.0000")
    @Digits(integer = 14, fraction = 4)
    private BigDecimal highestBidPrice;
    @Column(nullable = false)
    @NotNull
    private Boolean disabled;
    @Column(nullable = false)
    @NotNull
    private Boolean requiresManualIntervention;
    
    @OneToOne
    // NOTE: winningBid can be null!
    private BidEntity winningBid;
    @ManyToOne
    // NOTE: address can be null!
    private AddressEntity address;
    @OneToMany(mappedBy = "auctionListing")
    // NOTE: bids can be null!
    private List<BidEntity> bids;

    public AuctionListingEntity() {
        this.highestBidPrice = new BigDecimal("0.0000");
        this.disabled = false;
        this.requiresManualIntervention = false;
        this.bids = new ArrayList<>();
    }

    public AuctionListingEntity(String auctionListingName, Date startDateTime, Date endDateTime, BigDecimal reservePrice, BigDecimal highestBidPrice, Boolean disabled, Boolean requiresManualIntervention, BidEntity winningBid, AddressEntity address, List<BidEntity> bids) {
        this.auctionListingName = auctionListingName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.reservePrice = reservePrice;
        this.highestBidPrice = highestBidPrice;
        this.disabled = disabled;
        this.requiresManualIntervention = requiresManualIntervention;
        this.winningBid = winningBid;
        this.address = address;
        this.bids = bids;
    }

    public Long getAuctionListingId() {
        return auctionListingId;
    }

    public void setAuctionListingId(Long auctionListingId) {
        this.auctionListingId = auctionListingId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (auctionListingId != null ? auctionListingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the auctionListingId fields are not set
        if (!(object instanceof AuctionListingEntity)) {
            return false;
        }
        AuctionListingEntity other = (AuctionListingEntity) object;
        if ((this.auctionListingId == null && other.auctionListingId != null) || (this.auctionListingId != null && !this.auctionListingId.equals(other.auctionListingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AuctionListingEntity[ id=" + auctionListingId + " ]";
    }
    
    /**
     * @return the auctionListingName
     */
    public String getAuctionListingName() {
        return auctionListingName;
    }

    /**
     * @param auctionListingName the auctionListingName to set
     */
    public void setAuctionListingName(String auctionListingName) {
        this.auctionListingName = auctionListingName;
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
     * @return the disabled
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * @param disabled the disabled to set
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
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
