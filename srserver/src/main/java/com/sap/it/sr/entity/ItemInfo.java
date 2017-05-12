package com.sap.it.sr.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class ItemInfo implements Serializable {
	private static final long serialVersionUID = -7458269368197976414L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PICKUP_DATA_ID")
	@JsonBackReference
    private PickupData pickupData;

    @OneToMany(mappedBy = "itemInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemDetail> itemDetails = new ArrayList<ItemDetail>();

    
    private Timestamp grTime;
    private String poNumber;
    private int poItem;
    private String itemDesc;
    private String location;
    private int quantity;
    private BigDecimal price;
    
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PickupData getPickupData() {
        return pickupData;
    }

    public void setPickupData(PickupData pickupData) {
        this.pickupData = pickupData;
    }

    public List<ItemDetail> getItemDetails() {
		return itemDetails;
	}

	public void setItemDetails(List<ItemDetail> itemDetails) {
		this.itemDetails = itemDetails;
	}

	public Timestamp getGrTime() {
		return grTime;
	}

	public void setGrTime(Timestamp grTime) {
		this.grTime = grTime;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public int getPoItem() {
		return poItem;
	}

	public void setPoItem(int poItem) {
		this.poItem = poItem;
	}

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
