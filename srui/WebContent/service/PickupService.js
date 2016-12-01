sap.ui.define([
          'jquery.sap.global',
		  "sap/it/sr/ui/service/BaseService"
          ], function(jQuery, BaseService) {
  "use strict";
  var bs = new BaseService();
  return BaseService.extend("sap.it.sr.ui.service.PickupService", {
    getPickupDatas: function(oData) {
      var dtd = $.Deferred();
      bs.asyncReq({
        url: "/srserver/pickup/allwithparam",
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
    getPickupData: function(oData) {
      var dtd = $.Deferred();
      bs.asyncReq({
        url: "/srserver/pickup/get",
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
    findPickupData: function(oData) {
      var dtd = $.Deferred();
      bs.asyncReq({
        url: "/srserver/pickup/find",
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
    upsertPickupData: function(oData) {
        var dtd = $.Deferred();
        bs.asyncReq({
          url: "/srserver/pickup/upsert",
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
