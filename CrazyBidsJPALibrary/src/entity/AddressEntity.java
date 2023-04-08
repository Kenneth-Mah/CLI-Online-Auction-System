/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author kenne
 */
@Entity
public class AddressEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 1, max = 32)
    private String addressName;
    @Column(nullable = false)
    @NotNull
    private Boolean active;
    
    @OneToMany(mappedBy = "address")
    private List<AuctionListingEntity> wonAuctions;

    public AddressEntity() {
        this.active = true;
        this.wonAuctions = new ArrayList<>();
    }

    public AddressEntity(String addressName, Boolean active, List<AuctionListingEntity> wonAuctions) {
        this();
        this.addressName = addressName;
        this.active = active;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (addressId != null ? addressId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the addressId fields are not set
        if (!(object instanceof AddressEntity)) {
            return false;
        }
        AddressEntity other = (AddressEntity) object;
        if ((this.addressId == null && other.addressId != null) || (this.addressId != null && !this.addressId.equals(other.addressId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AddressEntity[ id=" + addressId + " ]";
    }

    /**
     * @return the addressName
     */
    public String getAddressName() {
        return addressName;
    }

    /**
     * @param addressName the addressName to set
     */
    public void setAddressName(String addressName) {
        this.addressName = addressName;
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
