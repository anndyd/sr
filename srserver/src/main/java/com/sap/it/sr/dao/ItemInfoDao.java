package com.sap.it.sr.dao;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

import com.sap.it.sr.entity.ItemInfo;

@Repository
public class ItemInfoDao extends BaseDao<ItemInfo> {
    public ItemInfo findByPK(String poNum, int poItem) {
    	ItemInfo itm = new ItemInfo();
        List<ItemInfo> list = em.createQuery("select t from ItemInfo t where t.poNumber=?1 and t.poItem=?2", ItemInfo.class)
                .setParameter(1, poNum).setParameter(2, poItem).setMaxResults(1).getResultList();
        return list.isEmpty() ? itm : list.get(0);
    }

    @SuppressWarnings("unchecked")
	public List<Long> findByTime(Timestamp startTime) {
    	String sql = "select CONVERT(poNumber,unsigned integer) + poItem as id " + 
			     "from ItemInfo where createDate > '2016-11-20'";
    	List<Long> list = em.createNativeQuery(sql).setParameter(1, startTime, TemporalType.TIMESTAMP).getResultList();
    	return list;
    }

}
