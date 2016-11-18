sap.ui.define([
    'jquery.sap.global',
    "sap/it/sr/ui/view/base/BaseController",
    "sap/it/sr/ui/service/EmployeeService",
    "sap/m/MessageToast",
    'sap/ui/model/json/JSONModel'
  ], function(jQuery, BaseController, EmployeeService, MessageToast, JSONModel) {
	  "use strict";
	  var es = new EmployeeService();
	  var __empId;
  return BaseController.extend("sap.it.sr.ui.view.EmployeeData2", {

		onInit : function(oEvent) {
			this.getOwnerComponent().byId("app").byId("idAppControl").setMode(sap.m.SplitAppMode.HideMode);
			var that = this;
			var oModel = new JSONModel();
			var param = {
				badgeId : "",
				empId : ""
			};
	
			es.getEmployee(param).done(function(data) {
				oModel.setData(data);
			}).always(function() {
				that.getView().setModel(oModel);
				that.getView().bindElement("/");
			});
			that._showFormFragment();
			// message listener
			window.addEventListener("message", that.onMessage.bind(that));
		},
	
	    onMessage : function (evt) {
	    	var that = this;
	    	__empId = evt.data;
			var param = {badgeId: "", empId: evt.data};
			
			es.getEmployee(param).done(function(data) {
				that.getView().getModel().setData(data);
	//			that.getView().getModel().refresh();
			});
	    },
	
		postMsg : function (param) {
			if (__empId !== param) {
				__empId = param;
				window.openerwindow.opener.postMessage(param, "*");
			}
		},
	
		onEmpChange : function(evt) {
		    var that = this;
		    var v = evt.getParameters().value;
		    if (v && v.length > 6) {
				var param = {
				    badgeId : "",
				    empId : v
				};
		
				es.getEmployee(param).done(function(data) {
				    that.getView().getModel().setData(data);
				    // post message to sub window
					that.postMsg(data.empId);
				});
		    }
		},
	
		onExit : function() {
		    if (this.oPageFragment) {
		    	this.oPageFragment.destroy();
		    }
			window.removeEventListener("message", onMessage.bind(this));
			subwin.close();
		},
	
		handleSavePress : function() {
		    // sap.ui.core.BusyIndicator.show();
		    es.upsertEmployee(this.getView().getModel().getData()).done(function(){
		    	MessageToast.show(this.getResourceBundle().getText("updateEmpS"));
		    });
		    // sap.ui.core.BusyIndicator.hide();
		},

		_showFormFragment : function () {
			var oPage = this.getView().byId("empDataPage");
			if (!this.oPageFragment) {
				this.oPageFragment = sap.ui.xmlfragment(this.getView().getId(), "sap.it.sr.ui.view.fragment.EmployeePage");
			}
			oPage.addContent(this.oPageFragment);
		}
    });

});