/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 *
 * @author kenne
 */
@Entity
public class BidEntity implements Serializable, Comparable<BidEntity> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;
    @Column(nullable = false, precision = 18, scale = 4)
    @NotNull
    @DecimalMin("0.0500")
    @Digits(integer = 14, fraction = 4)
    private BigDecimal bidPrice;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CustomerEntity customer;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private AuctionListingEntity auctionListing;

    public BidEntity() {
    }

    public BidEntity(BigDecimal bidPrice, CustomerEntity customer, AuctionListingEntity auctionListing) {
        this.bidPrice = bidPrice;
        this.customer = customer;
        this.auctionListing = auctionListing;
    }

    public Long getBidId() {
        return bidId;
    }

    public void setBidId(Long bidId) {
        this.bidId = bidId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bidId != null ? bidId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the bidId fields are not set
        if (!(object instanceof BidEntity)) {
            return false;
        }
        BidEntity other = (BidEntity) object;
        if ((this.bidId == null && other.bidId != null) || (this.bidId != null && !this.bidId.equals(other.bidId))) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(BidEntity other) {
        return this.bidPrice.compareTo(other.bidPrice);
    }

    @Override
    public String toString() {
        return "entity.BidEntity[ id=" + bidId + " ]";
    }

    /**
     * @return the bidPrice
     */
    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    /**
     * @param bidPrice the bidPrice to set
     */
    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    /**
     * @return the customer
     */
    public CustomerEntity getCustomer() {
        return customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    /**
     * @return the auctionListing
     */
    public AuctionListingEntity getAuctionListing() {
        return auctionListing;
    }

    /**
     * @param auctionListing the auctionListing to set
     */
    public void setAuctionListing(AuctionListingEntity auctionListing) {
        this.auctionListing = auctionListing;
    }
    
}
