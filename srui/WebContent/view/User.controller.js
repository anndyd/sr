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
	     pModel.setData({
	        userName: "sss",
	        password: "123"
	      });

			
			that.getView().setModel(oModel);
			that.getView().byId("userForm").setModel(pModel, "input");
//			that.getView().byId("userForm").bindElement("/");
//			that.getView().bindElement("/");
			
			us.getUsers().done(function(data){
				oModel.setData(data);
				oModel.refresh();
			});
		},

		onTablePress: function (evt) {
		  var that = this;
		  var pModel = that.getView().byId("userForm").getModel("input");
			var src = evt.getSource;
			var itm = evt.getParameters().listItem;
			pModel.setData({
			  userName: itm.getBindingContext().getProperty("userName"),
			  password: itm.getBindingContext().getProperty("password")
			});
			pModel.refresh();
			
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