package com.sap.it.sr.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sap.it.sr.entity.SyncItemDetail;

@Repository
public class SyncItemDetailDao extends BaseDao<SyncItemDetail> {
    public List<SyncItemDetail> findByPoItem(String poNum, int poItem) {
        return em.createQuery("select t from SyncItemDetail t where t.poNumber=?1 " +
        		"and t.poItem=?2", SyncItemDetail.class)
                .setParameter(1, poNum).setParameter(2, poItem).getResultList();
    }

    public SyncItemDetail findByPK(String poNum, int poItem, int poItemDetail) {
    	SyncItemDetail itm = new SyncItemDetail();
        List<SyncItemDetail> list = em.createQuery("select t from SyncItemDetail t where t.poNumber=?1 " +
        		"and t.poItem=?2 and t.poItemDetail=?3", SyncItemDetail.class)
                .setParameter(1, poNum).setParameter(2, poItem).setParameter(3, poItemDetail)
                .setMaxResults(1).getResultList();
        return list.isEmpty() ? itm : list.get(0);
    }

    @SuppressWarnings("unchecked")
	public List<Long> findByTime(Timestamp startTime) {
    	String sql = "select CONVERT(concat(d.poNumber,d.poItem,poItemDetail),unsigned integer) as idd " + 
			    "from SyncItemDetail d left join SyncItemInfo i " +
    			"on d.poNumber=i.poNumber and d.poItem=i.poItem ";// +
			    //"where i.createDate > ?1";
	
    	return em.createNativeQuery(sql)
    	        //.setParameter(1, startTime, TemporalType.TIMESTAMP)
    	        .getResultList();
    }

}
