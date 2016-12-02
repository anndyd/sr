package com.sap.it.sr.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.sap.it.sr.entity.PickupData;

@Repository
public class PickupDataDao extends BaseDao<PickupData> {
    @SuppressWarnings("unchecked")
    public List<PickupData> findAll(String empIdFrom, String empIdTo, String dateFrom, String dateTo) {
        String sql = "select t from PickupData t";
        
        int i = 0;
        String w = "";
        List<String> p = new ArrayList<String>();
        
        if (empIdFrom != null && !empIdFrom.equals("")) {
        	i++;
        	w = w + " t.empId>=?" + i;
        	p.add(empIdFrom.toUpperCase());
        }
        if (empIdTo != null && !empIdTo.equals("")) {
        	i++;
        	if (i>0) {
        		w = w + " and t.empId<=?" + i;
        	} else {
        		w = w + " t.empId<=?" + i;
        	}
        	p.add(empIdTo.toUpperCase());
        }
        if (dateFrom != null && !dateFrom.equals("")) {
        	i++;
        	if (i>0) {
        		w = w + " and t.pickupDate>=?" + i;
        	} else {
        		w = w + " t.pickupDate>=?" + i;
        	}
        	p.add(dateFrom);
        }
        if (dateTo != null && !dateTo.equals("")) {
        	i++;
        	if (i>0) {
        		w = w + " and t.pickupDate<=?" + i;
        	} else {
        	   	w = w + " t.pickupDate<=?" + i;
        	}
        	p.add(dateTo);
        }
        if (!w.equals("")) {
        	w = " where " + w;
        }
        
        Query query = em.createQuery(sql);
        if (p.size() > 0) {
	        for (int j = 0; j < p.size(); j++) {
	        	query.setParameter(j+1, p.get(j));
	        }
        }
        return query.getResultList();
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
}
