sap.ui.define([
    'jquery.sap.global',
    "sap/it/sr/ui/view/base/BaseController",
    "sap/it/sr/ui/service/UserService",
    "sap/m/MessageToast",
    'sap/ui/model/json/JSONModel'
  ], function(jQuery, BaseController, UserService, MessageToast, JSONModel) {
  "use strict";
  var us = new UserService();
  var w;
  return BaseController.extend("sap.it.sr.ui.view.User", {

		onInit: function (oEvent) {
			var that = this;
			var oModel = new JSONModel();
			var pModel = new JSONModel();
			that.getView().setModel(oModel);
			that.getView().setModel(pModel, "input");
			that.getView().bindElement("/");
			
			us.getUsers().done(function(data){
				oModel.setData(data);
				oMedel.refresh();
			});
		},

		onTablePress: function (evt) {
			var src = evt.getSource;
			var itm = evt.getParameters.listItem;
		},
		
		onExit : function () {
			if (this.oFormFragment) {
				this.oFormFragment.destroy();
			}
		},

		handleSavePress : function () {
//			sap.ui.core.BusyIndicator.show();
			es.upsertUser(this.getView().getModel().getData());
//			sap.ui.core.BusyIndicator.hide();
			MessageToast.show(this.getResourceBundle().getText("updateUserS"));
		}
  });

});