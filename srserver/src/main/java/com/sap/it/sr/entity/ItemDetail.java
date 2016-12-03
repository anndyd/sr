package com.sap.it.sr.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class ItemDetail implements Serializable {
	private static final long serialVersionUID = -3273329222309149185L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_INFO_ID")
	@JsonBackReference
    private ItemInfo itemInfo;

    private String poNumber;
    private int poItem;
    private int poItemDetail;
    private String serailNo;
	private String equipNo;
    
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public ItemInfo getItemInfo() {
        return itemInfo;
    }

    public void setItemInfo(ItemInfo itemInfo) {
        this.itemInfo = itemInfo;
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

	public int getPoItemDetail() {
		return poItemDetail;
	}

	public void setPoItemDetail(int poItemDetail) {
		this.poItemDetail = poItemDetail;
	}

	public String getSerailNo() {
		return serailNo;
	}

	public void setSerailNo(String serailNo) {
		this.serailNo = serailNo;
	}

	public String getEquipNo() {
		return equipNo;
	}

	public void setEquipNo(String equipNo) {
		this.equipNo = equipNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
