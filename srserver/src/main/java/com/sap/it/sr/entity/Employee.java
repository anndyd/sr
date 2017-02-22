package com.sap.it.sr.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Employee implements Serializable {
	private static final long serialVersionUID = 8125620808110899438L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	private String badgeId;
    private String empId;
    private String empName;
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

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
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
