/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author yeowh
 */
@Entity
public class TransactionEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionid;
    private BigDecimal trasactionAmount;
    
    private CustomerEntity customer;
    
    private CreditPackageEntity creditPackage;
    
    private BidEntity bid;

    public TransactionEntity() {
    }

    public TransactionEntity(BigDecimal trasactionAmount, CustomerEntity customer, CreditPackageEntity creditPackage, BidEntity bid) {
        this.trasactionAmount = trasactionAmount;
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
    public String toString() {
        return "entity.TransactionEntity[ id=" + transactionid + " ]";
    }

    /**
     * @return the trasactionAmount
     */
    public BigDecimal getTrasactionAmount() {
        return trasactionAmount;
    }

    /**
     * @param trasactionAmount the trasactionAmount to set
     */
    public void setTrasactionAmount(BigDecimal trasactionAmount) {
        this.trasactionAmount = trasactionAmount;
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
