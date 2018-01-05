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
import com.sap.it.sr.dao.EmployeeDao;
import com.sap.it.sr.dao.GrPoInfoDao;
import com.sap.it.sr.dao.SyncItemDetailDao;
import com.sap.it.sr.dao.SyncItemInfoDao;
import com.sap.it.sr.entity.CommonSettings;
import com.sap.it.sr.entity.Employee;
import com.sap.it.sr.entity.SyncItemDetail;
import com.sap.it.sr.entity.SyncItemInfo;
import com.sap.it.sr.util.EmpInfo;

@Lazy(false)
@Component
// @Scope("request")
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

    @Autowired
    private EmployeeDao edao;

    @Scheduled(cron = "0 0/5 * * * ?")
    @Transactional
    public void autoSyncGrData() {
        LOGGER.info("Start synchronize gr data, ...");
        syncGrData();
        LOGGER.info("Synchronize gr data end.");
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void autoSyncEmpData() {
        LOGGER.info("Start synchronize employee data, ...");
        syncEmployeeDataFromLDAP();
        LOGGER.info("Synchronize employee data end.");
    }

    @Transactional
    public long syncGrData() {
        long rlt = 0;
        try {
            // SimpleDateFormat formatter = new
            // SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            // String dateInString = "2016-11-20 15:23:01";

            Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
            // try {
            // currentTime = new
            // Timestamp(formatter.parse(dateInString).getTime());
            // } catch (ParseException e) {}
            List<CommonSettings> ss = sdao.findAll();
            CommonSettings cs = new CommonSettings();
            cs.setPoCreateTime(currentTime);
            if (ss != null && ss.size() > 0) {
                cs = ss.get(0);
            }
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(cs.getPoCreateTime().getTime());
            // back 1 second for corvering millisecond cut error
            cal.add(Calendar.SECOND, -1);
            Timestamp startTime = new Timestamp(cal.getTime().getTime());

            List<SyncItemInfo> itmi = dao.findDoneItem(startTime);
            if (itmi != null && itmi.size() > 0) {
                for (SyncItemInfo itm : itmi) {
                    idao.merge(itm);
                    rlt++;
                }
                List<SyncItemDetail> itmd = dao.findDoneItemDetail(startTime);
                if (itmd != null && itmd.size() > 0) {
                    for (SyncItemDetail itm : itmd) {
                        ddao.merge(itm);
                    }
                }
            }
            cs.setPoCreateTime(currentTime);
            sdao.merge(cs);
        } catch (Exception e) {
            LOGGER.error("======== Synchronize employee data failed. ========");
            e.printStackTrace();
        }

        return rlt;
    }

    @Transactional
    private void syncEmployeeDataFromLDAP() {
        List<Employee> emps = edao.findAll();
        for (Employee emp : emps) {
            EmpInfo ei = edao.getEmpInfo(emp.getEmpId());
            if (null != ei) {
                if ((null != ei.getName() && !ei.getName().equals(emp.getEmpName()))
                        || (null != ei.getCostCenter() && !ei.getCostCenter().equals(emp.getCostCenter()))) {
                    emp.setEmpName(ei.getName());
                    emp.setCostCenter(ei.getCostCenter());
                    edao.merge(emp);
                    LOGGER.info("Update employee: " + emp.getEmpId());
                }
            } else {
                edao.remove(emp);
                LOGGER.info("Remove employee: " + emp.getEmpId());
            }
        }
    }

}
