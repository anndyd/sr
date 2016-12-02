package com.sap.it.sr.dao;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

import com.sap.it.sr.entity.SyncItemInfo;

@Repository
public class SyncItemInfoDao extends BaseDao<SyncItemInfo> {
    public List<SyncItemInfo> findByPoNum(String poNum) {
        return em.createQuery("select t from SyncItemInfo t where t.status=2 and t.poNumber=?1", SyncItemInfo.class)
                .setParameter(1, poNum).getResultList();
    }

    public SyncItemInfo findByPK(String poNum, int poItem) {
    	SyncItemInfo itm = new SyncItemInfo();
        List<SyncItemInfo> list = em.createQuery("select t from SyncItemInfo t where t.poNumber=?1 and t.poItem=?2", SyncItemInfo.class)
                .setParameter(1, poNum).setParameter(2, poItem).setMaxResults(1).getResultList();
        return list.isEmpty() ? itm : list.get(0);
    }

    @SuppressWarnings("unchecked")
	public List<Long> findByTime(Timestamp startTime) {
    	String sql = "select CONVERT(poNumber,unsigned integer) + poItem as id " + 
			     "from SyncItemInfo where createDate > ?1";
    	return em.createNativeQuery(sql).setParameter(1, startTime, TemporalType.TIMESTAMP).getResultList();
    }

    public List<SyncItemInfo> findByEmpId(String empId) {
        return em.createQuery("select t from SyncItemInfo t where t.status=2 and t.userId=?1", SyncItemInfo.class)
                .setParameter(1, empId).getResultList();
    }

}
