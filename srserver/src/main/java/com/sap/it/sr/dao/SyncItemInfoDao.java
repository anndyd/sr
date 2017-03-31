package com.sap.it.sr.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sap.it.sr.entity.SyncItemInfo;

@Repository
public class SyncItemInfoDao extends BaseDao<SyncItemInfo> {
    public List<SyncItemInfo> findByPoNum(String poNum) {
        return em.createQuery("select t from SyncItemInfo t where t.quantity>0 and t.poNumber=?1", SyncItemInfo.class)
                .setParameter(1, poNum).getResultList();
    }

    public List<SyncItemInfo> findByPK(String poNum, int poItem) {
        return em.createQuery("select t from SyncItemInfo t where t.poNumber=?1 and t.poItem=?2", SyncItemInfo.class)
                .setParameter(1, poNum).setParameter(2, poItem).getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<Long> findByTime(Timestamp startTime) {
    	String sql = "select CONVERT(CONCAT(poNumber,poitem),unsigned integer) as idd " + 
			     "from SyncItemInfo"; // where createDate > ?1";
    	return em.createNativeQuery(sql)
//    			.setHint(QueryHints.REFRESH, HintValues.TRUE)
    	        //.setParameter(1, startTime, TemporalType.TIMESTAMP)
    	        .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<SyncItemInfo> findByEmpId(String empId) {
        if (empId != null) {
            empId = empId.toUpperCase();
        }
        String sql = "select * from SyncItemInfo t where t.userId=?1 " +
                     "and (t.ponumber,t.poitem) not in " +
                     "(select i.ponumber,i.poitem from iteminfo i " + 
                     "right join pickupdata p on i.PICKUP_DATA_ID = p.ID " +
                     "where p.EMPID=?1)";
//        return em.createQuery("select t from SyncItemInfo t where t.status=2 and t.userId=?1", SyncItemInfo.class)
        return em.createNativeQuery(sql, SyncItemInfo.class)
                .setParameter(1, empId).getResultList();
    }

}
