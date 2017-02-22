package com.sap.it.sr.util;

public class EmpInfo {
	private String id;
	private String mail;
	private String name;
	private String country;
	private String location;
	private String department;
	private String manager;
	private String costCenter;
	
	public void printInfo() {
		System.out.println("EmpInfo: ");
		System.out.println("id = " + id);
		System.out.println("mail = " + mail);
		System.out.println("name = " + name);
		System.out.println("country = " + country);
		System.out.println("location = " + location);
		System.out.println("department = " + department);
		System.out.println("manager = " + manager);
		System.out.println("costCenter = " + costCenter);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}
	
}
