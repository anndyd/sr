sap.ui.define([ 'jquery.sap.global', "sap/it/sr/ui/view/base/BaseController",
	"sap/it/sr/ui/service/UserService", "sap/m/MessageToast",
	'sap/ui/model/json/JSONModel' ], function(jQuery, BaseController,
	UserService, MessageToast, JSONModel) {
    "use strict";
    var us = new UserService();
    var w;
    return BaseController.extend("sap.it.sr.ui.view.User", {

	onInit : function(oEvent) {
	    var that = this;
	    var oModel = new JSONModel();
	    var pModel = new JSONModel();
	    that.getView().setModel(oModel);
	    that.getView().setModel(pModel, "input");
	    that.getView().bindElement("input>/");
	    that.refreshTable();
	},

	onTablePress : function(evt) {
	    var that = this;
	    var pModel = that.getView().getModel("input");
	    var itmCxt = evt.getParameters().listItem.getBindingContext();
	    pModel.setData({
		userName : itmCxt.getProperty("userName"),
		fullName : itmCxt.getProperty("fullName"),
		password : itmCxt.getProperty("password")
	    });
	    pModel.refresh();

	},

	refreshTable: function() {
	    sap.ui.core.BusyIndicator.show();
	    var oModel = this.getView().getModel();
	    us.getUsers().done(function(data) {
		oModel.setData(data);
		oModel.refresh();
	    });
	    sap.ui.core.BusyIndicator.hide();
	},
	
	onExit : function() {
	    if (this.oFormFragment) {
		this.oFormFragment.destroy();
	    }
	},

	handleAddPress : function() {
	    var that = this;
	    var pModel = that.getView().getModel("input");
	    pModel.setData({
		userName : "",
		fullName : "",
		password : ""
	    });
	    pModel.refresh();
	},

	handleSavePress : function() {
	    var that = this;
	    us.upsertUser(that.getView().getModel("input").getData()).done(function(){
		that.refreshTable();
		MessageToast.show(that.getResourceBundle().getText("updateUserS"));
	    });
	}
    });

});