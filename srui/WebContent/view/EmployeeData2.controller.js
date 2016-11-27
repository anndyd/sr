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
			var tModel = new JSONModel();
			that.getView().setModel(tModel);
			that.getView().setModel(oModel, "input");
			that.getView().bindElement("input>/");
	
			that._showFormFragment();
			// message listener
			window.addEventListener("message", that.onMessage.bind(that));
		},
	
	    onMessage : function (evt) {
	    	var that = this;
	    	__empId = evt.data;
			var param = {badgeId: "", empId: evt.data};
			
			es.getEmployee(param).done(function(data) {
				that.getView().getModel().setData([data]);
				that.getView().getModel("input").setData(data);
			});
	    },
	
		postMsg : function (param) {
			if (__empId !== param) {
				__empId = param;
				window.opener.postMessage(param, "*");
			}
		},
	
		onEmpChange : function(evt) {
		    var that = this;
		    var v = evt.getParameters().value;
		    if (v && v.length > 0) {
				var param = {
				    badgeId : "",
				    empId : v
				};
		
				es.getEmployee(param).done(function(data) {
				    var tdata = [data];
				    data.empId = v;
				    that.getView().getModel("input").setData(data);
				    that.getView().getModel().setData(tdata);
				    // post message to sub window
					that.postMsg(data.empId);
				});
		    }
		},
	
		onExit : function() {
		    if (this.oTableFragment) {
		    	this.oTableFragment.destroy();
		    }
			window.removeEventListener("message", onMessage.bind(this));
			subwin.close();
		},
	
		handleSavePress : function() {
			var that = this;
		    // sap.ui.core.BusyIndicator.show();
		    es.upsertEmployee(this.getView().getModel("input").getData()).done(function(){
		    	MessageToast.show(that.getResourceBundle().getText("updateEmpS"));
		    });
		    // sap.ui.core.BusyIndicator.hide();
		},

		_showFormFragment : function () {
			var oPage = this.getView().byId("empDataPage");
			if (!this.oTableFragment) {
				this.oTableFragment = sap.ui.xmlfragment(this.getView().getId(), "sap.it.sr.ui.view.fragment.EmployeeTable");
			}
			oPage.addContent(this.oTableFragment);
		}
    });

});