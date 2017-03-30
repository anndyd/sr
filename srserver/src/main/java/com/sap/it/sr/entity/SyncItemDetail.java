package com.sap.it.sr.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SyncItemDetail implements Serializable {
	private static final long serialVersionUID = -3517360767968072506L;

	@Id
	private Long id;

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
