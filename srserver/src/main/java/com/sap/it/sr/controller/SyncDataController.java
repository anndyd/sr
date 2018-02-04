package com.sap.it.sr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.it.sr.service.SyncData;

@Controller
@RequestMapping("syncData")
@Scope("request")
public class SyncDataController {
//	private static final Logger LOGGER = Logger.getLogger(GrDataController.class);
	
    @Autowired
    private SyncData sync;

	@RequestMapping(value="/syncGr", method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public long syncGrData(@RequestParam String startTime){
		return sync.syncGrData(startTime);
	}

	@RequestMapping(value="/syncEmp", method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public void syncEmpData(){
		sync.autoSyncEmpData();
	}
}
