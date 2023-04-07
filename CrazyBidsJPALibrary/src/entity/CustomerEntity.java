/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author yeowh
 */
@Entity
public class CustomerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    @Column(nullable = false, precision = 18, scale = 4)
    @NotNull
    @DecimalMin("0.0000")
    @Digits(integer = 14, fraction = 4)
    private BigDecimal availableBalance;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 1, max = 32)
    private String firstName;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 1, max = 32)
    private String lastName;
    @Column(nullable = false, unique = true, length = 32)
    @NotNull
    @Size(min = 6, max = 32)
    private String username;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 8, max = 32)
    private String password;
    
    @OneToMany(mappedBy = "customer")
    // NOTE: transactions can be null!
    private List<TransactionEntity> transactions;
    @OneToMany(mappedBy = "customer")
    // NOTE: bids can be null!
    private List<BidEntity> bids;
    @OneToMany
    // NOTE: addresses can be null!
    private List<AddressEntity> addresses;
    @OneToMany
    // NOTE: wonAuctions can be null!
    private List<AuctionListingEntity> wonAuctions;

    public CustomerEntity() {
        transactions = new ArrayList<>();
        bids = new ArrayList<>();
        addresses = new ArrayList<>();
        wonAuctions = new ArrayList<>();
    }

    public CustomerEntity(String firstName, String lastName, String username, String password) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }
    
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (customerId != null ? customerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the customerId fields are not set
        if (!(object instanceof CustomerEntity)) {
            return false;
        }
        CustomerEntity other = (CustomerEntity) object;
        if ((this.customerId == null && other.customerId != null) || (this.customerId != null && !this.customerId.equals(other.customerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CustomerEntity[ id=" + customerId + " ]";
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the availableBalance
     */
    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    /**
     * @param availableBalance the availableBalance to set
     */
    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    /**
     * @return the transactions
     */
    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

    /**
     * @param transactions the transactions to set
     */
    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
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

    /**
     * @return the addresses
     */
    public List<AddressEntity> getAddresses() {
        return addresses;
    }

    /**
     * @param addresses the addresses to set
     */
    public void setAddresses(List<AddressEntity> addresses) {
        this.addresses = addresses;
    }

    /**
     * @return the wonAuctions
     */
    public List<AuctionListingEntity> getWonAuctions() {
        return wonAuctions;
    }

    /**
     * @param wonAuctions the wonAuctions to set
     */
    public void setWonAuctions(List<AuctionListingEntity> wonAuctions) {
        this.wonAuctions = wonAuctions;
    }

}
