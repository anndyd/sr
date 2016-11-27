package com.sap.it.sr.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
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
	public long syncGrData(){
		long rlt = 0;
		Date startDate = new Date((new java.util.Date()).getTime());
		List<CommonSettings> ss = sdao.findAll();
		if (ss != null && ss.size() > 0) {
			startDate = ss.get(0).getPoCreateTime();
		}
		List<ItemInfo> itmi = dao.findDoneItem(startDate);
		if (itmi != null) {
			for (ItemInfo itm : itmi) {
				idao.merge(itm);
			}
			List<ItemDetail> itmd = dao.findDoneItemDetail(startDate);
			if (itmd != null) {
				for (ItemDetail itm : itmd) {
					ddao.merge(itm);
				}
			}
			rlt = itmi.size();
		}
		
		return rlt;
	}
}
