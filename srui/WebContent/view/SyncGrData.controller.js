sap.ui.define([
    'jquery.sap.global',
    "sap/it/sr/ui/view/base/BaseController",
    "sap/it/sr/ui/service/SyncGrService",
    'sap/ui/model/json/JSONModel'
  ], function(jQuery, BaseController, SyncGrService, JSONModel) {
  "use strict";
  var ss = new SyncGrService();
 return BaseController.extend("sap.it.sr.ui.view.SyncGrData", {

		onInit: function (oEvent) {
			var that = this;
			var oModel = new JSONModel();
			
			that.getView().setModel(oModel);
			that.getView().bindElement("/");
//			that.refreshData();
		},

		handleRefresh: function (oEvent) {
			this.refreshData();
		},
		
		refreshData: function () {
			sap.ui.core.BusyIndicator.show();
			var i18n = this.getResourceBundle();
			var that = this;
			var date1 = new Date();
			
			ss.syncGrData().done(function(data){
				var date2 = new Date();
				var diff = date2 - date1;
				
				var rlt = [{zkey: i18n.getText("takeTime"), zvalue: diff/1000 + i18n.getText("seconds")},
				           {zkey: i18n.getText("effectLines"), zvalue: data}];
				that.getView().getModel().setData(rlt);
				
			});

			sap.ui.core.BusyIndicator.hide();
		},

		onExit : function () {
		}	
  });

});