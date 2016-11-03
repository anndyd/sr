package com.sap.it.sr.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.it.sr.dao.EmployeeDao;
import com.sap.it.sr.dao.PickupDataDao;
import com.sap.it.sr.entity.PickupData;

@Controller
@RequestMapping("pickupData")
@Scope("request")
public class PickupDataController {

    @Autowired
    private PickupDataDao dao;

    @Autowired
    private EmployeeDao empDao;

	@RequestMapping(value="/allwithparam", method = RequestMethod.GET)
	@ResponseBody
	public List<PickupData> getPickupDatas(
			@RequestParam(required = true) String empIdFrom,
			@RequestParam(required = true) String empIdTo,
			@RequestParam(required = true) String dateFrom,
			@RequestParam(required = true) String dateTo
			){
		return dao.findAll(empIdFrom, empIdTo, dateFrom, dateTo);
	}

	@RequestMapping(value="/all", method = RequestMethod.GET)
	@ResponseBody
	public List<PickupData> getPickupDatas(){
		return dao.findAll();
	}

	@RequestMapping(value="/get", method = RequestMethod.GET)
	@ResponseBody
	public PickupData getPickupData(@RequestParam(required = true) String badgeId, 
			@RequestParam(required = true) String empId){
		PickupData pd = new PickupData();
		if (!badgeId.equals("")) {
			empId = empDao.findByBadgeId(badgeId).getEmpId();
		} 
		pd = dao.findByEmpId(empId);
		pd.setBadgeId(badgeId);
		pd.setEmpId(empId);
		if (pd.getEmpId() != null && pd.getEmpId() != "") {
			pd.setEmpName(empDao.findByEmpId(empId).getEmpName());
		}
		if (pd.getAgentId() != null && pd.getAgentId() != "") {
			pd.setAgentName(empDao.findByEmpId(pd.getAgentId()).getEmpName());
		}
		if (pd.getPickupDate() == null) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			pd.setPickupDate(dateFormat.format(new Date()));
		}
		return pd;
	}
	
	@RequestMapping(value="/upsert", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void upsertPickupData(@RequestBody PickupData pd){
		PickupData pd1 = dao.findByEmpId(pd.getEmpId());
		if (pd.getEmpId() != null) {
			pd.setId(pd1.getId());
			dao.merge(pd);
		}
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void deletePickupData(@RequestBody Long id){
		dao.remove(id);
	}

}
