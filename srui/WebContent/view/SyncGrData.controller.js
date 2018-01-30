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
			var that = this;
		  that.getView().byId("sync").setEnabled(false);
			sap.ui.core.BusyIndicator.show();
			var i18n = this.getResourceBundle();
			var date1 = new Date();
			var param = that.getView().getModel().getData();
			
			ss.syncGrData(param).done(function(data){
				var date2 = new Date();
				var diff = date2 - date1;
				
				var rlt = [{zkey: i18n.getText("takeTime"), zvalue: diff/1000 + i18n.getText("seconds")},
				           {zkey: i18n.getText("effectLines"), zvalue: data}];
				that.getView().getModel().setData(rlt);
				
			}).always(function(){
			  that.getView().byId("sync").setEnabled(true);
			});
		},

		onExit : function () {
		}	
  });

});