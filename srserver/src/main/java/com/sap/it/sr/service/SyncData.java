package com.sap.it.sr.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sap.it.sr.dao.CommonSettingsDao;
import com.sap.it.sr.dao.GrPoInfoDao;
import com.sap.it.sr.dao.SyncItemDetailDao;
import com.sap.it.sr.dao.SyncItemInfoDao;
import com.sap.it.sr.entity.CommonSettings;
import com.sap.it.sr.entity.SyncItemDetail;
import com.sap.it.sr.entity.SyncItemInfo;

@Lazy(false)
@Component
//@Scope("request")
public class SyncData {
	private static final Logger LOGGER = Logger.getLogger(SyncData.class);
    
    @Autowired
    private GrPoInfoDao dao;

    @Autowired
    private CommonSettingsDao sdao;
    
    @Autowired
    private SyncItemInfoDao idao;

    @Autowired
    private SyncItemDetailDao ddao;
	
	@Scheduled(cron="0 0/5 * * * ?")
    @Transactional
	public void autoSyncGrData() {
		LOGGER.info("Start synchronize gr data, ...");
		syncGrData();
		LOGGER.info("Synchronize gr data end.");
	}
	
    @Transactional
    public long syncGrData(){
        long rlt = 0;
//      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
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
        if (itmi != null && itmi.size() > 0) {
            List<Long> ovs1 = idao.findByTime(startTime);
            for (SyncItemInfo itm : itmi) {
                if (!ovs1.contains(itm.getId())) {
                    idao.merge(itm);
                    rlt++;
                }
            }
            List<SyncItemDetail> itmd = dao.findDoneItemDetail(startTime);
            if (itmd != null && itmd.size() > 0) {
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
