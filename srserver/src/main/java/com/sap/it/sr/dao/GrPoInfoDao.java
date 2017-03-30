package com.sap.it.sr.dao;


import java.sql.Timestamp;
import java.util.List;

import javax.persistence.TemporalType;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.springframework.stereotype.Repository;

import com.sap.it.sr.entity.SyncItemDetail;
import com.sap.it.sr.entity.SyncItemInfo;

@Repository
public class GrPoInfoDao extends BaseDao<SyncItemInfo> {
    
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
	public List<SyncItemInfo> findDoneItem(Timestamp startTime) {
    	String sql = "select CONVERT(BIGINT,(i.PO_NUMBER + CONVERT(VARCHAR,i.PO_ITEM))) as id, " +
                     "i.PO_NUMBER as poNumber, i.PO_ITEM as poItem,  " +
                     "i.DESCRIPTION as itemDesc, i.LOCATION as location, " +
                     "i.USERID as userId, i.STATUS as status, " +
                     "i.QUANTITY as quantity, i.PLANT as plant, l.CREATE_TIME as createDate " +
                     "from DBA.ITEM_INFO i " +
                     "LEFT JOIN DBA.PO_ITEM_LOG l on i.PO_NUMBER = l.PO_NUMBER and i.PO_ITEM = l.PO_ITEM " +
                     "where i.STATUS = 2 and l.STEP=2 and l.STATUS=0 and " +
			         "l.CREATE_TIME > ?1";
    	
        List<SyncItemInfo> list = grem.createNativeQuery(sql, SyncItemInfo.class)
        		.setHint(QueryHints.REFRESH, HintValues.TRUE)
                .setParameter(1, startTime, TemporalType.TIMESTAMP).getResultList();
        return list;
    }

	@SuppressWarnings("unchecked")
	public List<SyncItemDetail> findDoneItemDetail(Timestamp startTime) {
    	String sql = "select CONVERT(BIGINT,(PO_NUMBER + Convert(varchar,PO_ITEM) + Convert(varchar,PO_ITEM_ID))) as id, " +
                "PO_NUMBER as poNumber, PO_ITEM as poItem, " +
                "PO_ITEM_ID as poItemDetail,   " +
                "SERIALNO as serialno, EQUIPNO as equipno " +
                "from DBA.ITEM_DETAIL_INFO " +
                "where string(PO_NUMBER,'-',PO_ITEM) in " +
                "(select string(PO_NUMBER,'-',PO_ITEM) from DBA.ITEM_INFO  " +
                "where STATUS = 2 and PO_NUMBER in  " +
                "(select PO_NUMBER from DBA.PO_ITEM_LOG where STEP=2 and STATUS=0 and CREATE_TIME > ?1))";
    	
        List<SyncItemDetail> list = grem.createNativeQuery(sql, SyncItemDetail.class)
        		.setHint(QueryHints.REFRESH, HintValues.TRUE)
                .setParameter(1, startTime, TemporalType.TIMESTAMP).getResultList();
        return list;
    }

}
