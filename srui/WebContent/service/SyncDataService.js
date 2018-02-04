sap.ui.define([
          'jquery.sap.global',
		  "sap/it/sr/ui/service/BaseService"
          ], function(jQuery, BaseService) {
  "use strict";
  var bs = new BaseService();
  return BaseService.extend("sap.it.sr.ui.service.SyncDataService", {
	  syncGrData: function(oData) {
	      var dtd = $.Deferred();
	      bs.asyncReq({
	        url: "/srserver/syncData/syncGr",
	        type: "GET",
	    		contentType: "application/json",
	    		data: oData
	      }).done(function(data) {
	        dtd.resolve(data);
	      }).fail(function(err) {
	        dtd.reject(err);
	      });
	      return dtd.promise();
	  },
	  syncEmpData: function() {
	      var dtd = $.Deferred();
	      bs.asyncReq({
	        url: "/srserver/syncData/syncEmp",
	        type: "GET"
	      }).done(function() {
	        dtd.resolve();
	      }).fail(function(err) {
	        dtd.reject(err);
	      });
	      return dtd.promise();
	    },
 
  });
 });
