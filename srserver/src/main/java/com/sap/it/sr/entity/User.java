package com.sap.it.sr.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import org.eclipse.persistence.annotations.JoinFetch;

@Entity
public class User implements Serializable {
    private static final long serialVersionUID = 185706989928100701L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    private String fullName;
    
    private boolean status;
    
    private String password;
    
    private String role;
    
    private String pickLocation;
    
    private String remark;
    
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(
          name="costcenter",
          joinColumns=@JoinColumn(name="EMPID", referencedColumnName="USERNAME")
    )
    @Column(name="CHARGECC")
    @JoinFetch
    private List<String> chargeCC;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
	    if (userName != null) {
	        userName = userName.toUpperCase();
        }
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPickLocation() {
		return pickLocation;
	}

	public void setPickLocation(String pickLocation) {
		this.pickLocation = pickLocation;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<String> getChargeCC() {
		return chargeCC;
	}

	public void setChargeCC(List<String> chargeCC) {
		this.chargeCC = chargeCC;
	}

}
