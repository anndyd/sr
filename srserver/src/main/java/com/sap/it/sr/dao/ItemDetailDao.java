package com.sap.it.sr.dao;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

import com.sap.it.sr.entity.ItemDetail;

@Repository
public class ItemDetailDao extends BaseDao<ItemDetail> {
    public ItemDetail findByPK(String poNum, int poItem, int poItemDetail) {
    	ItemDetail itm = new ItemDetail();
        List<ItemDetail> list = em.createQuery("select t from ItemDetail t where t.poNumber=?1 " +
        		"and t.poItem=?2 and t.poItemDetail=?3", ItemDetail.class)
                .setParameter(1, poNum).setParameter(2, poItem).setParameter(3, poItemDetail)
                .setMaxResults(1).getResultList();
        return list.isEmpty() ? itm : list.get(0);
    }

    @SuppressWarnings("unchecked")
	public List<Long> findByTime(Timestamp startTime) {
    	String sql = "select CONVERT(d.poNumber,unsigned integer) + d.poItem + d.poItemDetail as id" + 
			    "from ItemDetail d left join ItemInfo i " +
    			"on d.poNumber=i.poNumber and d.poItem=i.poItem " +
			    "where i.createDate > ?1";
	
    	List<Long> list = em.createNativeQuery(sql).setParameter(1, startTime, TemporalType.TIMESTAMP).getResultList();
       return list;
    }

}
