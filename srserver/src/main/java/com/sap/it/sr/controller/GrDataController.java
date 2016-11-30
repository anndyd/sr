package com.sap.it.sr.controller;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.it.sr.dao.CommonSettingsDao;
import com.sap.it.sr.dao.GrPoInfoDao;
import com.sap.it.sr.dao.ItemDetailDao;
import com.sap.it.sr.dao.ItemInfoDao;
import com.sap.it.sr.entity.CommonSettings;
import com.sap.it.sr.entity.ItemDetail;
import com.sap.it.sr.entity.ItemInfo;

@Controller
@RequestMapping("grData")
@Scope("request")
public class GrDataController {

    @Autowired
    private GrPoInfoDao dao;

    @Autowired
    private CommonSettingsDao sdao;
    
    @Autowired
    private ItemInfoDao idao;

    @Autowired
    private ItemDetailDao ddao;

	@RequestMapping(value="/sync", method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public long syncGrData(){
		long rlt = 0;
		Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
		List<CommonSettings> ss = sdao.findAll();
		CommonSettings cs = new CommonSettings();
		cs.setPoCreateTime(currentTime);
		if (ss != null && ss.size() > 0) {
			cs = ss.get(0);
		}
		Timestamp startTime = cs.getPoCreateTime();
		
		List<ItemInfo> itmi = dao.findDoneItem(startTime);
		if (itmi != null) {
			List<Long> ovs1 = idao.findByTime(startTime);
			for (ItemInfo itm : itmi) {
				if (!ovs1.contains(itm.getId())) {
					idao.merge(itm);
					rlt++;
				}
			}
			List<ItemDetail> itmd = dao.findDoneItemDetail(startTime);
			if (itmd != null) {
				List<Long> ovs2 = idao.findByTime(startTime);
				for (ItemDetail itm : itmd) {
					if (!ovs2.contains(itm.getId())) {
						ddao.merge(itm);
					}
				}
			}
		}
		cs.setPoCreateTime(currentTime);
		sdao.merge(cs);
		
		return rlt;
	}
}
