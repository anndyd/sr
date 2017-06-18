sap.ui.define([ 'jquery.sap.global', "sap/it/sr/ui/js/Formatter",
		"sap/it/sr/ui/view/base/BaseController",
		"sap/it/sr/ui/service/UserService", 
	    "sap/it/sr/ui/service/EmployeeService",
		"sap/m/MessageToast",
		'sap/ui/model/json/JSONModel' ], function(jQuery, Formatter,
		BaseController, UserService, EmployeeService, MessageToast, JSONModel) {
	"use strict";
	var us = new UserService();
	var es = new EmployeeService();
	var w, start = 0, count = 0, page = 0, PAGESIZE = 8， addMode = false;
	return BaseController.extend("sap.it.sr.ui.view.User", {

		onInit : function(oEvent) {
			var that = this;
			var oModel = new JSONModel();
			var pModel = new JSONModel();
			pModel.setData({
				roleCtl : false
			});
			that.getView().setModel(oModel);
			that.getView().setModel(pModel, "input");
			that.getView().bindElement("input>/");
			us.getSize().done(function(data) {
				count = data;
			});
			that.refreshTable(0);
		},

		onTablePress : function(evt) {
			var that = this;
			var pModel = that.getView().getModel("input");
			var itmCxt = evt.getParameters().listItem.getBindingContext();
			pModel.setData({
				userName : itmCxt.getProperty("userName"),
				fullName : itmCxt.getProperty("fullName"),
				status : itmCxt.getProperty("status"),
				pickLocation : itmCxt.getProperty("pickLocation"),
				password : itmCxt.getProperty("password"),
				role : itmCxt.getProperty("role"),
				chargeCC : itmCxt.getProperty("chargeCC"),
				
				roleCtl : util.sessionInfo.role === "1",
				editCtl : util.sessionInfo.role === "1" ||
				util.sessionInfo.currentUser === itmCxt.getProperty("userName"),
				pwdCtl : util.sessionInfo.currentUser === itmCxt.getProperty("userName")
			});
			pModel.refresh();

		},

		refreshTable : function(start) {
			sap.ui.core.BusyIndicator.show();
			var oModel = this.getView().getModel();
			var param = {
				start: start,
				max: PAGESIZE
			}
			us.getUsers(param).done(function(data) {
				oModel.setData(data);
				oModel.refresh();
			});
		},

		onExit : function() {
			if (this.oFormFragment) {
				this.oFormFragment.destroy();
			}
		},
		
		onEmpChange : function(evt) {
		    var that = this;
			var pModel = that.getView().getModel("input");
		    var v = evt.getParameters().value;
		    if (v && v.length > 6) {
				var param = {
				    badgeId : "",
				    empId : v
				};
				us.getUser({userName: v}).done(function(udata){
					if (udata) {
						var pModel = that.getView().getModel("input");
						pModel.setData({
							userName : udata.userName,
							fullName : udata.fullName,
							status : udata.status,
							pickLocation : udata.pickLocation,
							password : udata.password,
							role : udata.role,
							chargeCC : udata.chargeCC,
							
							roleCtl : util.sessionInfo.role === "1",
							editCtl : util.sessionInfo.role === "1" ||
							util.sessionInfo.currentUser === udata.userName,
							pwdCtl : util.sessionInfo.currentUser === udata.userName
						});
						pModel.refresh();
					} else {
						es.getEmployee(param).done(function(data) {
							data.badgeId = param.badgeId;
							data.empId = param.empId;
							pModel.setData({
								userName : data.empId,
								fullName : data.empName,
								pwdCtl : false
							});
							pModel.refresh();
						});
						
					}
				});
		    }
		},

		handleAddPress : function() {
			var that = this;
			var pModel = that.getView().getModel("input");
			pModel.setData({
				userName : "",
				fullName : "",
				status : 0,
				pickLocation : "",
				password : "",
				role : null,
				pwdCtl : false
			});
			pModel.refresh();
			addMode = true;
		},

		handleSavePress : function() {
			var that = this;
			us.upsertUser(that.getView().getModel("input").getData()).done(
				function() {
					if (addMode) {
						count++;
					}
					that.refreshTable();
					MessageToast.show(that.getResourceBundle().getText(
							"updateUserS"));
				}
			);
			addMode = false;
		},
		
		userStatus :  function (fValue) {
			try {
				var i18n = this.getResourceBundle();
				if (fValue) {
					return i18n.getText("active");
				} else {
					return i18n.getText("inactive");
				}
			} catch (err) {
				return "None";
			}
		},

		handleNext : function() {
			if (page < 0) {
				page = 1;
			} else {
				page += 1;
			}
			start = page * PAGESIZE;
			if (start === count) {
				var Btn = this.getView().byId("btnNext");
				Btn.setEnabled(false);
			}
			if (start >= PAGESIZE) {
				var Btn = this.getView().byId("btnPrevious");
				Btn.setEnabled(true);
			}
			this.refreshTable(start);
		},

		handlePrevious : function() {
			page -= 1;
			if (page <= 0) {
				start = 0;
			} else {
				start = page * PAGESIZE;
			}
			if (start < count) {
				var Btn = this.getView().byId("btnNext");
				Btn.setEnabled(true);
			}
			if (start === 0) {
				var Btn = this.getView().byId("btnPrevious");
				Btn.setEnabled(false);
			}
			this.refreshTable(start);
		}
	});

});