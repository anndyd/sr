package com.sap.it.sr.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sap.it.sr.controller.GrDataController;

@Lazy(false)
@Component
public class SyncData {
	private static final Logger LOGGER = Logger.getLogger(SyncData.class);
	
	@Autowired
	private GrDataController grDataCtl;
	
	@Scheduled(cron="0 0/5 * * * ?")
	public void autoSyncGrData() {
		LOGGER.info("Start synchronize gr data, ...");
		grDataCtl.syncGrData();
		LOGGER.info("Synchronize gr data end.");
	}

}
