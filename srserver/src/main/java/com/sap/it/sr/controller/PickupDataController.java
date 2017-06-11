package com.sap.it.sr.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.sap.it.sr.dao.SyncItemDetailDao;
import com.sap.it.sr.dao.SyncItemInfoDao;
import com.sap.it.sr.dto.PickupDataInfo;
import com.sap.it.sr.entity.Employee;
import com.sap.it.sr.entity.ItemDetail;
import com.sap.it.sr.entity.ItemInfo;
import com.sap.it.sr.entity.PickupData;
import com.sap.it.sr.entity.SyncItemDetail;
import com.sap.it.sr.entity.SyncItemInfo;
import com.sap.it.sr.service.SendMail;
import com.sap.it.sr.util.SessionHolder;

@Controller
@RequestMapping("pickup")
@Scope("request")
public class PickupDataController {
	
	@Autowired(required=true)
	private HttpServletRequest request;	

    @Autowired
    private PickupDataDao dao;

    @Autowired
    private EmployeeDao empDao;
    
    @Autowired
    private SyncItemInfoDao sidao;
    
    @Autowired
    private SyncItemDetailDao sddao;
//    
//    @Autowired
//    private ItemInfoDao idao;
//    
//    @Autowired
//    private ItemDetailDao ddao;
    
	@RequestMapping(value="/allwithparam", method = RequestMethod.GET)
	@ResponseBody
	public List<PickupDataInfo> getPickupDatas(
			@RequestParam(required = true) String empIdFrom,
			@RequestParam(required = true) String empIdTo,
			@RequestParam(required = true) String dateFrom,
			@RequestParam(required = true) String dateTo,
			@RequestParam String poNumber,
			@RequestParam String location,
			@RequestParam String equipNo,
			@RequestParam String costCenter
			){
		return dao.findAll(empIdFrom, empIdTo, costCenter, dateFrom, dateTo, poNumber, location, equipNo);
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
		return pd;
	}

	@RequestMapping(value="/find", method = RequestMethod.GET)
	@ResponseBody
	public PickupData findPickupData(@RequestParam(required = true) String empId){
		return findItemByEmpId(empId);
	}

	@RequestMapping(value="/findbypo", method = RequestMethod.GET)
	@ResponseBody
	public PickupData findPickupDataByPo(@RequestParam(required = true) String poNum){
		return findItemByPoNumber(poNum);
	}
	
	@RequestMapping(value="/upsert", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void upsertPickupData(@RequestBody PickupData pd){
		HttpSession session = request.getSession();
	    String role = session.getAttribute(SessionHolder.USER_ROLE).toString();
		
		if (pd != null && null != role && (role.equals("1") || role.equals("2"))) {
		    // upper case for employee id
		    if (pd.getAgentId() != null){
		        pd.setAgentId(pd.getAgentId().toUpperCase());
		    }
		    if (pd.getEmpId() == null || pd.getItems().isEmpty()) {
		    	// do nothing
		    } else {
		        pd.setEmpId(pd.getEmpId().toUpperCase());
		        pd.setCostCenter(empDao.getEmpInfo(pd.getEmpId()).getCostCenter());
		        PickupData opd = dao.findByEmpIdItemInfo(pd.getEmpId(), pd.getItems().get(0).getPoNumber());
		        // for avoiding idempotent problem
		        if (pd.equals(opd)) {
		        	return;
		        }
	    	    dao.merge(pd);
		    }
		    Employee emp = empDao.findByEmpId(pd.getEmpId());
		    // send mail
		    SendMail sm = new SendMail();
		    pd.setEmpName(emp.getEmpName());
		    sm.sendPickedEmail(pd, null, null);
		}
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void deletePickupData(@RequestBody Long id){
		dao.remove(id);
	}

    @RequestMapping(value="/getLocations", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getLocations(){
        return dao.getAllLocations();
    }

    @RequestMapping(value="/getPoNumbers", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getPoNumbers(){
        return dao.getAllPoNumbers();
    }

    @RequestMapping(value="/getCostCenters", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getCostCenters(){
        return dao.getAllCostCenters();
    }

	private PickupData findItemByEmpId(String empId) {
		Map<String, ItemInfo> ifs = new HashMap<>();
		List<SyncItemDetail> sids = sddao.findByEmpId(empId);
		if (sids != null) {
			sids.forEach(sid->{
				String itmKey = sid.getPoNumber() + sid.getPoItem();
				ItemInfo itm = ifs.get(itmKey);
				if (itm == null) {
					itm = new ItemInfo();
					itm.setPoNumber(sid.getPoNumber());
					itm.setPoItem(sid.getPoItem());
				}
				ItemDetail itd = new ItemDetail();
				itd.setPoNumber(sid.getPoNumber());
				itd.setPoItem(sid.getPoItem());
				itd.setPoItemDetail(sid.getPoItemDetail());
				itd.setSerailNo(sid.getSerailNo());
				itd.setEquipNo(sid.getEquipNo());
				itm.getItemDetails().add(itd);
				
				ifs.put(itmKey, itm);
			});
		}
		
		PickupData pd = new PickupData();
		List<SyncItemInfo> sis = sidao.findByEmpIdFromDetail(empId);
		if (sis != null) {
			sis.forEach(sitm->{
			    ItemInfo itm = ifs.get(sitm.getPoNumber() + sitm.getPoItem());
			    itm.setGrTime(sitm.getCreateDate());
			    itm.setPoNumber(sitm.getPoNumber());
			    itm.setPoItem(sitm.getPoItem());
			    itm.setItemDesc(sitm.getItemDesc());
			    itm.setLocation(sitm.getLocation());
			    itm.setQuantity(sitm.getQuantity());
			    itm.setPrice(sitm.getPrice());
				
				pd.getItems().add(itm);
			});
		}
		
		return pd;
	}

    /* deprecated, use find by item detail instead
	private PickupData findItemByEmpId(String empId) {
		PickupData pd = new PickupData();
		List<SyncItemInfo> sis = sidao.findByEmpId(empId);
		if (sis != null) {
			sis.forEach(sitm->{
				ItemInfo itm = new ItemInfo();
				itm.setGrTime(sitm.getCreateDate());
				itm.setPoNumber(sitm.getPoNumber());
				itm.setPoItem(sitm.getPoItem());
				itm.setItemDesc(sitm.getItemDesc());
				itm.setLocation(sitm.getLocation());
				itm.setQuantity(sitm.getQuantity());
				
				List<SyncItemDetail> sids = sddao.findByPoItem(sitm.getPoNumber(), sitm.getPoItem());
				if (sids != null) {
					sids.forEach(sid->{
						ItemDetail itd = new ItemDetail();
						itd.setPoNumber(sid.getPoNumber());
						itd.setPoItem(sid.getPoItem());
						itd.setPoItemDetail(sid.getPoItemDetail());
						itd.setSerailNo(sid.getSerailNo());
						itd.setEquipNo(sid.getEquipNo());
						itm.getItemDetails().add(itd);
					});
				}
				pd.getItems().add(itm);
			});
		}
		
		return pd;
	} */

	private PickupData findItemByPoNumber(String poNum) {
		PickupData pd = new PickupData();
		List<SyncItemInfo> sis = sidao.findByPoNum(poNum);
		if (sis != null) {
			sis.forEach(sitm->{
				ItemInfo itm = new ItemInfo();
				itm.setGrTime(sitm.getCreateDate());
				itm.setPoNumber(sitm.getPoNumber());
				itm.setPoItem(sitm.getPoItem());
				itm.setItemDesc(sitm.getItemDesc());
				itm.setLocation(sitm.getLocation());
				itm.setQuantity(sitm.getQuantity());
				itm.setPrice(sitm.getPrice());
				pd.getItems().add(itm);
			});
		}
		
		return pd;
		
	}
}
