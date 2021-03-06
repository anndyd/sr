sap.ui.define([
          'jquery.sap.global',
		  "sap/it/sr/ui/service/BaseService"
          ], function(jQuery, BaseService) {
  "use strict";
  var bs = new BaseService();
  return BaseService.extend("sap.it.sr.ui.service.EmployeeService", {
    getAllEmployees: function() {
      var dtd = $.Deferred();
      bs.asyncReq({
        url: "/srserver/employee/all",
        type: "GET"
      }).done(function(data) {
        dtd.resolve(data);
      }).fail(function(err) {
        dtd.reject(err);
      });
      return dtd.promise();
    },
    getEmployee: function(oData) {
      var dtd = $.Deferred();
      bs.asyncReq({
        url: "/srserver/employee/get",
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
    upsertEmployee: function(oData) {
        var dtd = $.Deferred();
        bs.asyncReq({
          url: "/srserver/employee/upsert",
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
