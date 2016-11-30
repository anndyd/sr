package com.sap.it.sr.dao;


import java.sql.Timestamp;
import java.util.List;

import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

import com.sap.it.sr.entity.ItemDetail;
import com.sap.it.sr.entity.ItemInfo;

@Repository
public class GrPoInfoDao extends BaseDao<ItemInfo> {
    
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

	@SuppressWarnings("unchecked")
	public List<ItemInfo> findDoneItem(Timestamp startTime) {
    	String sql = "select CONVERT(BIGINT,i.PO_NUMBER) + i.PO_ITEM as id, p.PO_NUMBER as poNumber, i.PO_ITEM as poItem, " + 
    				 "i.DESCRIPTION as itemDesc, i.LOCATION as location, " +
    			     "i.USERID as userId, i.STATUS as status, " +
    			     "i.QUANTITY as quantity, p.CREATE_TIME as createDate " +
    			     "from DBA.PO_INFO p left join DBA.ITEM_INFO i on p.PO_NUMBER = i.PO_NUMBER " +
    			     "where i.STATUS = 2 and p.CREATE_TIME > ?1";
    	
        List<ItemInfo> list = grem.createNativeQuery(sql, ItemInfo.class)
                .setParameter(1, startTime, TemporalType.TIMESTAMP).getResultList();
        return list;
    }

	@SuppressWarnings("unchecked")
	public List<ItemDetail> findDoneItemDetail(Timestamp startTime) {
    	String sql = "select CONVERT(BIGINT,PO_NUMBER) + PO_ITEM + PO_ITEM_ID as id, " +
                "PO_NUMBER as poNumber, PO_ITEM as poItem, " +
                "PO_ITEM_ID as poItemDetail,   " +
                "SERIALNO as serialno, EQUIPNO as equipno " +
                "from DBA.ITEM_DETAIL_INFO " +
                "where string(PO_NUMBER,'-',PO_ITEM) in " +
                "(select string(PO_NUMBER,'-',PO_ITEM) from DBA.ITEM_INFO  " +
                "where STATUS = 2 and PO_NUMBER in  " +
                "(select PO_NUMBER from DBA.PO_INFO where CREATE_TIME > ?1))";
    	
        List<ItemDetail> list = grem.createNativeQuery(sql, ItemDetail.class)
                .setParameter(1, startTime, TemporalType.TIMESTAMP).getResultList();
        return list;
    }

}
