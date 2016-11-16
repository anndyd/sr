package com.sap.it.sr.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class PoInfo implements Serializable {
	private static final long serialVersionUID = -7458269368197976414L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PICKUP_DATA_ID", nullable = false)
    private PickupData pickupData;

    private String poNumber;
    private int poItem;
    private String itemDesc;
    private int quantity;
    
    private String remark;
//    PO_NUMBER       VARCHAR(10)     NOT NULL,
//    PO_ITEM         INT             NOT NULL,
//    DESCRIPTION     VARCHAR(200),
//    LOCATION        VARCHAR(8),
//    BRAND           VARCHAR(30),
//    QUANTITY        INT,
//    USERID          VARCHAR(20),
//    STATUS          INT,
//    EQUIPNO_NEEDED  BIT,
//	  PLANT           VARCHAR(30),

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
