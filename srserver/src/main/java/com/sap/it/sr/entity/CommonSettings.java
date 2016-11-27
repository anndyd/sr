package com.sap.it.sr.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CommonSettings implements Serializable {
	private static final long serialVersionUID = -6856682953939004354L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Date poCreateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getPoCreateTime() {
		return poCreateTime;
	}

	public void setPoCreateTime(Date poCreateTime) {
		this.poCreateTime = poCreateTime;
	}
}
