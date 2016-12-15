package com.sap.it.sr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.it.sr.service.SyncData;

@Controller
@RequestMapping("grData")
@Scope("request")
public class GrDataController {
//	private static final Logger LOGGER = Logger.getLogger(GrDataController.class);
	
    @Autowired
    private SyncData sync;

	@RequestMapping(value="/sync", method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public long syncGrData(){
		return sync.syncGrData();
	}
}
