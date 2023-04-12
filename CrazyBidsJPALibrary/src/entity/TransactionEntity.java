/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author yeowh
 */
@Entity
public class TransactionEntity implements Serializable, Comparable<TransactionEntity> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionid;
    @Column(nullable = false, precision = 18, scale = 4)
    @NotNull
    @Digits(integer = 14, fraction = 4)
    // NOTE: transactionAmount can be positive AND negative!
    private BigDecimal transactionAmount;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date timeOfTransaction;
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer quantity;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CustomerEntity customer;
    @OneToOne
    // NOTE: creditPackage can be null!
    private CreditPackageEntity creditPackage;
    @OneToOne
    // NOTE: bid can be null!
    private BidEntity bid;

    public TransactionEntity() {
        this.quantity = 1;
    }

    public TransactionEntity(BigDecimal transactionAmount, Date timeOfTransaction, Integer quantity, CustomerEntity customer, CreditPackageEntity creditPackage, BidEntity bid) {
        this.transactionAmount = transactionAmount;
        this.timeOfTransaction = timeOfTransaction;
        this.quantity = quantity;
        this.customer = customer;
        this.creditPackage = creditPackage;
        this.bid = bid;
    }

    public Long getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(Long transactionid) {
        this.transactionid = transactionid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transactionid != null ? transactionid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the transactionid fields are not set
        if (!(object instanceof TransactionEntity)) {
            return false;
        }
        TransactionEntity other = (TransactionEntity) object;
        if ((this.transactionid == null && other.transactionid != null) || (this.transactionid != null && !this.transactionid.equals(other.transactionid))) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(TransactionEntity other) {
        return this.timeOfTransaction.compareTo(other.timeOfTransaction);
    }

    @Override
    public String toString() {
        return "entity.TransactionEntity[ id=" + transactionid + " ]";
    }

    /**
     * @return the transactionAmount
     */
    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }
    
    /**
     * @param transactionAmount the transactionAmount to set
     */
    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    /**
     * @return the timeOfTransaction
     */
    public Date getTimeOfTransaction() {
        return timeOfTransaction;
    }

    /**
     * @param timeOfTransaction the timeOfTransaction to set
     */
    public void setTimeOfTransaction(Date timeOfTransaction) {
        this.timeOfTransaction = timeOfTransaction;
    }
    
    /**
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
     * @return the creditPackage
     */
    public CreditPackageEntity getCreditPackage() {
        return creditPackage;
    }

    /**
     * @param creditPackage the creditPackage to set
     */
    public void setCreditPackage(CreditPackageEntity creditPackage) {
        this.creditPackage = creditPackage;
    }

    /**
     * @return the bid
     */
    public BidEntity getBid() {
        return bid;
    }

    /**
     * @param bid the bid to set
     */
    public void setBid(BidEntity bid) {
        this.bid = bid;
    }
    
}
