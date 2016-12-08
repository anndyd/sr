package com.sap.it.sr.controller;

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
import com.sap.it.sr.dao.SyncItemDetailDao;
import com.sap.it.sr.dao.SyncItemInfoDao;
import com.sap.it.sr.entity.ItemDetail;
import com.sap.it.sr.entity.ItemInfo;
import com.sap.it.sr.entity.PickupData;
import com.sap.it.sr.entity.SyncItemDetail;
import com.sap.it.sr.entity.SyncItemInfo;

@Controller
@RequestMapping("pickup")
@Scope("request")
public class PickupDataController {

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
	public List<PickupData> getPickupDatas(
			@RequestParam(required = true) String empIdFrom,
			@RequestParam(required = true) String empIdTo,
			@RequestParam(required = true) String dateFrom,
			@RequestParam(required = true) String dateTo,
			@RequestParam String poNumber,
			@RequestParam String location,
			@RequestParam String equipNo
			){
		return dao.findAll(empIdFrom, empIdTo, dateFrom, dateTo, poNumber, location, equipNo);
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
		if (pd.getPickupTime() == null) {
//			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//			pd.setPickupDate(dateFormat.format(new Date()));
//			pd.setPickupTime();
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
		if (pd != null) {
		    // upper case for employee id
		    if (pd.getAgentId() != null){
		        pd.setAgentId(pd.getAgentId().toUpperCase());
		    }
		    if (pd.getEmpId() == null || pd.getItems().isEmpty()) {
		    	// do nothing
		    } else {
		        pd.setEmpId(pd.getEmpId().toUpperCase());
		        PickupData opd = dao.findByEmpIdItemInfo(pd.getEmpId(), pd.getItems().get(0).getPoNumber());
		        if (opd.getId() != null) {
		        	// for avoiding idempotent problem
		        	pd.setId(opd.getId());
		        }
		        
		        pd.getItems().forEach(itm->{
			        itm.setPickupData(pd);
			        itm.getItemDetails().forEach(itd->{
			            itd.setItemInfo(itm);
			        });
			    });
	    	    dao.merge(pd);
	//    	    dao.create(pd);
	    	    pd.getItems().forEach(itm->{
	    	        SyncItemInfo si = sidao.findByPK(itm.getPoNumber(), itm.getPoItem());
	    	        si.setStatus(4); // 4 - picked
	    	        sidao.merge(si);
	    	    });
		    }
		}
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void deletePickupData(@RequestBody Long id){
		dao.remove(id);
	}

	private PickupData findItemByEmpId(String empId) {
		PickupData pd = new PickupData();
		List<SyncItemInfo> sis = sidao.findByEmpId(empId);
		if (sis != null) {
			sis.forEach(sitm->{
				ItemInfo itm = new ItemInfo();
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
	}

	private PickupData findItemByPoNumber(String poNum) {
		PickupData pd = new PickupData();
		List<SyncItemInfo> sis = sidao.findByPoNum(poNum);
		if (sis != null) {
			sis.forEach(sitm->{
				ItemInfo itm = new ItemInfo();
				itm.setPoNumber(sitm.getPoNumber());
				itm.setPoItem(sitm.getPoItem());
				itm.setItemDesc(sitm.getItemDesc());
				itm.setLocation(sitm.getLocation());
				itm.setQuantity(sitm.getQuantity());
				pd.getItems().add(itm);
			});
		}
		
		return pd;
		
	}
}
