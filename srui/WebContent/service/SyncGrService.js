sap.ui.define([
          'jquery.sap.global',
		  "sap/it/sr/ui/service/BaseService"
          ], function(jQuery, BaseService) {
  "use strict";
  var bs = new BaseService();
  return BaseService.extend("sap.it.sr.ui.service.SyncGrService", {
	  syncGrData: function() {
      var dtd = $.Deferred();
      bs.asyncReq({
        url: "/srserver/grData/sync",
        type: "GET"
      }).done(function(data) {
        dtd.resolve(data);
      }).fail(function(err) {
        dtd.reject(err);
      });
      return dtd.promise();
    }
  
  });
 });
