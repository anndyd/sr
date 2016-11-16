package com.sap.it.sr.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

import com.sap.it.sr.entity.GrPoInfo;

@Repository
public class GrPoInfoDao extends BaseDao<GrPoInfo> {
    
//  CREATE TABLE PO_INFO --
//      PO_NUMBER       VARCHAR(10)     NOT NULL,
//      PO_ITEM_COUNT   INT             NOT NULL,
//      STATUS          INT             NOT NULL,
//      CREATE_TIME     TIMESTAMP,
//      UPDATE_TIME     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
//  TABLE ITEM_INFO  --
//  PO_NUMBER       VARCHAR(10)     NOT NULL,
//  PO_ITEM         INT             NOT NULL,
//  DESCRIPTION     VARCHAR(200),
//  LOCATION        VARCHAR(8),
//  BRAND           VARCHAR(30),
//  QUANTITY        INT,
//  USERID          VARCHAR(20),
//  STATUS          INT,
//  EQUIPNO_NEEDED  BIT,
//	  PLANT           VARCHAR(30),

// -- GrPoInfo class --
//    private String poNumber;
//    private int poItem;
//    private String itemDesc;
//    private String location;
//    private String userId;
//    private int status;
//    private int quantity;
//    private Date createDate; 
	
	@SuppressWarnings("unchecked")
	public List<GrPoInfo> findByEmpId(String empId, Date startDate) {
    	String sql = "select p.PO_NUMBER as poNumber, i.PO_ITEM as poItem, " + 
    				 "i.DESCRIPTION as itemDesc, i.LOCATION as location, " +
    			     "i.USERID as userId, i.STATUS as status, " +
    			     "i.QUANTITY as quantity, p.CREATE_TIME as createDate " +
    			     "from PO_INFO p left join ITEM_INFO i on p.PO_NUMBER = i.PO_NUMBER " +
    			     "where i.USERID = ?1 and p.CREATE_TIME > ?2";
    	
        List<GrPoInfo> list = grem.createNativeQuery(sql, GrPoInfo.class)
                .setParameter(1, empId).setParameter(2, startDate, TemporalType.DATE).getResultList();
        return list;
    }

}
