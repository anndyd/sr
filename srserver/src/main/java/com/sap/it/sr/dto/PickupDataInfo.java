package com.sap.it.sr.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PickupDataInfo implements Serializable {
	private static final long serialVersionUID = 8262031072400046195L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	private String empId;
	private Timestamp grTime;
	private Timestamp pickupTime;
	private long usedTime;
    private String poNumber;
    private int poItem;
    private String itemDesc;
    private String location;
    private int quantity;
    private BigDecimal price;
    private String serailNo;
	private String equipNo;
	private String costCenter;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public Timestamp getGrTime() {
		return grTime;
	}
	public void setGrTime(Timestamp grTime) {
		this.grTime = grTime;
	}
	public Timestamp getPickupTime() {
		return pickupTime;
	}
	public void setPickupTime(Timestamp pickupTime) {
		this.pickupTime = pickupTime;
	}
	public long getUsedTime() {
		return usedTime;
	}
	public void setUsedTime(long usedTime) {
		this.usedTime = usedTime;
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
	public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
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
	public String getCostCenter() {
		return costCenter;
	}
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}
}
