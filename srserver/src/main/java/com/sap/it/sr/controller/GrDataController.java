package com.sap.it.sr.controller;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.it.sr.dao.CommonSettingsDao;
import com.sap.it.sr.dao.GrPoInfoDao;
import com.sap.it.sr.dao.SyncItemDetailDao;
import com.sap.it.sr.dao.SyncItemInfoDao;
import com.sap.it.sr.entity.CommonSettings;
import com.sap.it.sr.entity.SyncItemDetail;
import com.sap.it.sr.entity.SyncItemInfo;

@Controller
@RequestMapping("grData")
@Scope("request")
public class GrDataController {

    @Autowired
    private GrPoInfoDao dao;

    @Autowired
    private CommonSettingsDao sdao;
    
    @Autowired
    private SyncItemInfoDao idao;

    @Autowired
    private SyncItemDetailDao ddao;

	@RequestMapping(value="/sync", method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	@Scheduled(cron="0 0/5 * * * ?")
	public long syncGrData(){
		long rlt = 0;
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
//        String dateInString = "2016-11-20 15:23:01";

		Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
//        try {
//            currentTime = new Timestamp(formatter.parse(dateInString).getTime());
//        } catch (ParseException e) {}
		List<CommonSettings> ss = sdao.findAll();
		CommonSettings cs = new CommonSettings();
		cs.setPoCreateTime(currentTime);
		if (ss != null && ss.size() > 0) {
			cs = ss.get(0);
		}
		Timestamp startTime = cs.getPoCreateTime();
		
		List<SyncItemInfo> itmi = dao.findDoneItem(startTime);
		if (itmi != null) {
			List<Long> ovs1 = idao.findByTime(startTime);
			for (SyncItemInfo itm : itmi) {
				if (!ovs1.contains(itm.getId())) {
					idao.merge(itm);
					rlt++;
				}
			}
			List<SyncItemDetail> itmd = dao.findDoneItemDetail(startTime);
			if (itmd != null) {
				List<Long> ovs2 = ddao.findByTime(startTime);
				for (SyncItemDetail itm : itmd) {
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
