sap.ui.define([
          'jquery.sap.global',
		  "sap/it/sr/ui/service/BaseService"
          ], function(jQuery, BaseService) {
  "use strict";
  var bs = new BaseService();
  return BaseService.extend("sap.it.sr.ui.service.UserService", {
    getSize: function() {
      var dtd = $.Deferred();
      bs.asyncReq({
        url: "/srserver/user/getSize",
        type: "GET"
      }).done(function(data) {
        dtd.resolve(data);
      }).fail(function(err) {
        dtd.reject(err);
      });
      return dtd.promise();
    },
    getUsers: function(oData) {
      var dtd = $.Deferred();
      bs.asyncReq({
        url: "/srserver/user/all",
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
    getUser: function(oData) {
      var dtd = $.Deferred();
      bs.asyncReq({
        url: "/srserver/user/get",
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
    upsertUser: function(oData) {
        var dtd = $.Deferred();
        bs.asyncReq({
          url: "/srserver/user/upsert",
          type: "POST",
          contentType: "application/json",
          data: JSON.stringify(oData)
        }).done(function(data) {
          dtd.resolve(data);
        });
        return dtd.promise();
     }
  
  });
 });
