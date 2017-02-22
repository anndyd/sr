package com.sap.it.sr.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class PickupData implements Serializable {
	private static final long serialVersionUID = -7403478285620985471L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String badgeId;
    private String empId;
    private String agentId;
    @Transient
    private String empName;
    @Transient
    private String agentName;
    
    @OneToMany(mappedBy = "pickupData", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemInfo> items = new ArrayList<ItemInfo>();
    
    private Timestamp pickupTime;
    private String costCenter;
    
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getBadgeId() {
		return badgeId;
	}

	public void setBadgeId(String badgeId) {
		this.badgeId = badgeId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public List<ItemInfo> getItems() {
		return items;
	}

	public void setItems(List<ItemInfo> items) {
		this.items = items;
	}

	public Timestamp getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(Timestamp pickupDate) {
		this.pickupTime = pickupDate;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
