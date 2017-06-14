package com.sap.it.sr.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.springframework.stereotype.Repository;

import com.sap.it.sr.dto.PickupDataInfo;
import com.sap.it.sr.entity.PickupData;

@Repository
public class PickupDataDao extends BaseDao<PickupData> {
    @SuppressWarnings("unchecked")
    public List<PickupData> findAllWithJpql(String empIdFrom, String empIdTo, String dateFrom, String dateTo, 
    		String poNumber, String location, String equipNo) {
		String sql = "select distinct t from PickupData t";
        
        int i = 0;
        String jn = "";
        String w = "";
        List<String> p = new ArrayList<String>();
        
        if (empIdFrom != null && !empIdFrom.equals("")) {
        	i++;
        	w = w + " t.empId=?" + i;
        	p.add(empIdFrom.toUpperCase());
        }
        if (empIdTo != null && !empIdTo.equals("")) {
        	i++;
        	if (i>1) {
        		w = w + " and t.empId<=?" + i;
        	} else {
        		w = w + " t.empId<=?" + i;
        	}
        	p.add(empIdTo.toUpperCase());
        }
        if (dateFrom != null && !dateFrom.equals("")) {
        	i++;
        	if (i>1) {
        		w = w + " and t.pickupTime>=?" + i;
        	} else {
        		w = w + " t.pickupTime>=?" + i;
        	}
        	p.add(dateFrom);
        }
        if (dateTo != null && !dateTo.equals("")) {
        	i++;
        	if (i>1) {
        		w = w + " and t.pickupTime<=?" + i;
        	} else {
        	   	w = w + " t.pickupTime<=?" + i;
        	}
        	p.add(dateTo);
        }
        if (poNumber != null && !poNumber.equals("")) {
       		jn = " join t.items i";
       		
        	i++;
        	if (i>1) {
        		w = w + " and i.poNumber=?" + i;
        	} else {
        	   	w = w + " i.poNumber=?" + i;
        	}
        	p.add(poNumber);
        }
        if (equipNo != null && !equipNo.equals("")) {
        	if (jn.equals("")) {
        		jn = " join t.items i";
        	}
       		jn = jn + " join i.itemDetails d";
       		
        	i++;
        	if (i>1) {
        		w = w + " and d.equipNo=?" + i;
        	} else {
        	   	w = w + " d.equipNo=?" + i;
        	}
        	p.add(equipNo);
        }
        if (location != null && !location.equals("")) {
        	if (jn.equals("")) {
        		jn = " join t.items i";
        	}
        	i++;
        	if (i>1) {
        		w = w + " and i.location IN (" + location + ")";
        	} else {
        	   	w = w + " i.location IN (" + location + ")";
        	}
        }
       
        if (!w.equals("")) {
        	w = " where " + w;
        }
        
        Query query = em.createQuery(sql + jn + w, PickupData.class);
        if (p.size() > 0) {
        	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        for (int j = 0; j < p.size(); j++) {
				String param = p.get(j);
	        	if (param.contains(":")) {
		        	try {
		        		query.setParameter(j+1, new Timestamp(formatter.parse(param).getTime()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
	        	} else {
	        		query.setParameter(j+1, param);
	        	}
	        }
        }
        List<PickupData> list = query.getResultList();
        return list;
    }
    @SuppressWarnings("unchecked")
    public List<PickupDataInfo> findAll(String empIdFrom, String empIdTo, String costCenter, String dateFrom, String dateTo, 
    		String poNumber, String location, String equipNo) {
		String sql = "select (@i:=@i+1) id, p.EMPID, i.GRTIME, p.PICKUPTIME, TIMESTAMPDIFF(HOUR,i.GRTIME,p.PICKUPTIME) usedTime, " +
					 "i.PONUMBER, i.POITEM, i.ITEMDESC, i.LOCATION, i.QUANTITY, i.PRICE, " +
					 "d.EQUIPNO, d.SERAILNO, p.COSTCENTER " +
					 "from pickupdata p " +
					 "CROSS JOIN (SELECT @i := 0) AS dummy " +
					 "join iteminfo i on p.ID = i.PICKUP_DATA_ID " +
					 "left join  " +
					 "(select * from itemdetail si where LENGTH(si.EQUIPNO)>0 or LENGTH(si.SERAILNO)>0) d  " +
					 "on i.ID = d.ITEM_INFO_ID";
        
        int i = 0;
        String w = "";
        List<String> p = new ArrayList<String>();
        
        if (empIdFrom != null && !empIdFrom.equals("")) {
        	i++;
        	w = w + " p.EMPID=?" + i;
        	p.add(empIdFrom.toUpperCase());
        }
        if (empIdTo != null && !empIdTo.equals("")) {
        	i++;
        	if (i>1) {
        		w = w + " and p.EMPID<=?" + i;
        	} else {
        		w = w + " p.EMPID<=?" + i;
        	}
        	p.add(empIdTo.toUpperCase());
        }
        if (costCenter != null && !costCenter.equals("")) {
        	i++;
        	if (i>1) {
        		w = w + " and p.costCenter IN (?" + i + ")";
        	} else {
        		w = w + " p.costCenter IN (?" + i + ")";
        	}
        	p.add(costCenter);
        }
        if (dateFrom != null && !dateFrom.equals("")) {
        	i++;
        	if (i>1) {
        		w = w + " and p.pickupTime>=?" + i;
        	} else {
        		w = w + " p.pickupTime>=?" + i;
        	}
        	p.add(dateFrom);
        }
        if (dateTo != null && !dateTo.equals("")) {
        	i++;
        	if (i>1) {
        		w = w + " and p.pickupTime<=?" + i;
        	} else {
        	   	w = w + " p.pickupTime<=?" + i;
        	}
        	p.add(dateTo);
        }
        if (poNumber != null && !poNumber.equals("")) {
        	i++;
        	if (i>1) {
        		w = w + " and i.poNumber=?" + i;
        	} else {
        	   	w = w + " i.poNumber=?" + i;
        	}
        	p.add(poNumber);
        }
        if (equipNo != null && !equipNo.equals("")) {
        	i++;
        	if (i>1) {
        		w = w + " and d.equipNo=?" + i;
        	} else {
        	   	w = w + " d.equipNo=?" + i;
        	}
        	p.add(equipNo);
        }
        if (location != null && !location.equals("")) {
        	i++;
        	if (i>1) {
        		w = w + " and i.location IN (" + location + ")";
        	} else {
        	   	w = w + " i.location IN (" + location + ")";
        	}
        }
       
        if (!w.equals("")) {
        	w = " where " + w;
        }
        
        Query query = em.createNativeQuery(sql + w, PickupDataInfo.class).setHint(QueryHints.REFRESH, HintValues.TRUE);
        if (p.size() > 0) {
        	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        for (int j = 0; j < p.size(); j++) {
				String param = p.get(j);
	        	if (param.contains(":")) {
		        	try {
		        		query.setParameter(j+1, new Timestamp(formatter.parse(param).getTime()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
	        	} else {
	        		query.setParameter(j+1, param);
	        	}
	        }
        }
        List<PickupDataInfo> list = query.getResultList();
        return list;
    }

    public PickupData findByEmpId(String empId) {
    	PickupData pd = new PickupData();
    	if (empId != null) {
            empId = empId.toUpperCase();
        }
        List<PickupData> list = em.createQuery("select t from PickupData t where t.empId=?1", PickupData.class)
                .setParameter(1, empId).setMaxResults(1).getResultList();
        return list.isEmpty() ? pd : list.get(0);
    }

    public PickupData findByAgentId(String agentId) {
    	PickupData pd = new PickupData();
    	if (agentId != null) {
    	    agentId = agentId.toUpperCase();
        }
        List<PickupData> list = em.createQuery("select t from PickupData t where t.agentId=?1", PickupData.class)
                .setParameter(1, agentId.toUpperCase()).setMaxResults(1).getResultList();
        return list.isEmpty() ? pd : list.get(0);
    }

    public PickupData findByEmpIdItemInfo(String empId, String PoNum) {
    	PickupData pd = new PickupData();
    	if (empId != null) {
            empId = empId.toUpperCase();
	        List<PickupData> list = em.createQuery("select t from PickupData t " +
	        		"join t.items i where t.empId=?1 " +
	        		"and i.poNumber=?2", PickupData.class)
	                .setParameter(1, empId).setParameter(2, PoNum)
	                .setMaxResults(1).getResultList();
	        pd = list.isEmpty() ? pd : list.get(0);
        }
    	return pd;
    }
    
    // the following functions are for user input suggestion
    @SuppressWarnings("unchecked")
    public List<String> getAllLocations() {
        return em.createNativeQuery("select distinct t.location from ItemInfo t order by location").getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getAllPoNumbers() {
        return em.createNativeQuery("select distinct t.ponumber from ItemInfo t order by ponumber").getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getAllCostCenters() {
        return em.createNativeQuery("select distinct t.costcenter from pickupdata t  order by costcenter").getResultList();
    }
     
}
