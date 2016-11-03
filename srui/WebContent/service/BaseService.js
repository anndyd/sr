sap.ui.define([
          'jquery.sap.global', 
          "sap/ui/base/Object",
          "sap/m/MessageBox"
          ], function(jQuery, Object, MessageBox) {
  "use strict";
  var processStatus = function(err) {
      switch (err.status) {
        case 400:
        case 500:
        	var resTxt = err.responseText;
        	MessageBox.alert(resTxt, {styleClass: "srMessageBoxStyle srMessageBoxError"});
          break;
        case 401:
        case 403:
          window.location.href = "login.html";
          break;
        case 404:
          MessageBox.alert("404 Not Found", {styleClass: "srMessageBoxStyle srMessageBoxError"});
          break;
        default:
          break;
      }
    };

  return Object.extend("sap.it.sr.ui.service.BaseService", {
    asyncReq: function(options) {
      var dtd = $.Deferred();
      options.async = true;
      options.selfHandleFail = options.selfHandleFail || false;
      options.tryCount = 0;
      options.retryLimit = 3;
      
      if (!!window.ActiveXObject || "ActiveXObject" in window) {
        $.ajaxSetup({ cache: false });
      }
      
      $.ajax(options).done(function(data) {
        dtd.resolve(data);
      }).done(function(data) {
        dtd.resolve(data);
      }).fail(function(err) {
        processStatus(err);
        dtd.reject(err);
      });
      return dtd.promise();
    }
  });
});
