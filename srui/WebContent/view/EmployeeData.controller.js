sap.ui.define([
    'jquery.sap.global',
    "sap/it/sr/ui/view/base/BaseController",
    "sap/it/sr/ui/service/EmployeeService",
    "sap/m/MessageToast",
    'sap/ui/model/json/JSONModel'
  ], function(jQuery, BaseController, EmployeeService, MessageToast, JSONModel) {
	  "use strict";
	  var es = new EmployeeService();
	  var subwin, __badgeId = "";
  return BaseController.extend("sap.it.sr.ui.view.EmployeeData", {

		onInit : function(oEvent) {
			// only enable on secondary screen
			//this.getOwnerComponent().byId("app").byId("idAppControl").setMode(sap.m.SplitAppMode.HideMode);
			var that = this;
			that._showFormFragment();
			
			var oModel = new JSONModel();
			var tModel = new JSONModel();
			that.getView().setModel(tModel);
			that.getView().setModel(oModel, "input");
			that.getView().bindElement("input>/");

			// when matched route, only enable on primary screen
			that.getRouter().getRoute("empData").attachPatternMatched(that.onRouteMatched, that);
			// message listener
			window.addEventListener("message", that.onMessage.bind(that));
		},
		
		onRouteMatched: function (evt) {
			// open new window, only enable on primary screen
			subwin = util.openSecondWindow("/srui/index.html#/empData2", 'SecondWindow');
		},
	
		postMsg : function (param) {
			__badgeId = param.badgeId;
			// only enable on primary screen
			subwin.postMessage({id: "empData", data: param}, "*");
			// only enable on secondary screen
			//window.opener.postMessage(param, "*");
		},
	
	    onMessage : function (evt) {
	    	var that = this;
	    	if (evt.data && evt.data.id === "empData2") {
	    		var data = evt.data.data;
		    	__badgeId = data.badgeId;
		    	that.getView().getModel("input").setData(data);
		    	that.getView().getModel("input").refresh();
		    	
		    	if (data.save && data.save === "save") {
					var param = {badgeId: "", empId: data.empId};
					that._getEmpData(param);
		    	}
	    	}
		},
	
		onEmpChange : function(evt) {
		    var that = this;
		    var v = evt.getParameters().value;
		    if (v && v.length > 6) {
			    // post message to another window
		    	var data = that.getView().getModel("input").getData();
		    	data.save = "";
		    	data.empId = v;
				that.postMsg(data);
				var param = {
				    badgeId : "",
				    empId : v
				};
				that._getEmpData(param);
		    }
		},
		
		onEmpNameChange : function(evt) {
		    var that = this;
		    var v = evt.getParameters().value;
		    // post message to another window
	    	var data = that.getView().getModel("input").getData();
	    	data.save = "";
	    	data.empName = v;
			that.postMsg(data);
		},
		
		onBadgeChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			if (v && v.length >= util.badgeIdLength-1) {
			    // post message to another window
		    	var data = that.getView().getModel("input").getData();
		    	data.save = "";
		    	data.badgeId = v;
		    	if (__badgeId !== v) {
		    		that.postMsg(data);
		    	}
				var param = {
				    badgeId : v,
				    empId : ""
				};
				that._getEmpData(param);
			}
		},
	
		onExit : function() {
  		  if (this.oPageFragment) {
  		    this.oPageFragment.destroy();
  		  }
		    if (this.oTableFragment) {
		    	this.oTableFragment.destroy();
		    }
			window.removeEventListener("message", onMessage.bind(this));
			subwin.close();
		},
	
		handleResetPress : function() {
			var that = this;
            that.getView().getModel("input").setData({});
            that.getView().getModel().setData({});
		},
	
		handleSavePress : function() {
			var that = this;
		    sap.ui.core.BusyIndicator.show();
		    es.upsertEmployee(this.getView().getModel("input").getData()).done(function(){
		    	MessageToast.show(that.getResourceBundle().getText("updateEmpS"));
		    	var data = that.getView().getModel("input").getData();
		    	that.getView().getModel().setData([data]);
			    // post message to another window
		    	data.save = "save";
				that.postMsg(data);
		    });
		},
		
		_getEmpData: function(param) {
			var that = this;
			param.needADInfo = true;
			es.getEmployee(param).done(function(data) {
        if (null === data.badgeId && null === data.empId && 
            null === data.costCenter && null === data.empName) {
        } else {
  				if (null === data.badgeId) {
  					data.badgeId = param.badgeId;
  				}
  				if (null === data.empId) {
  					data.empId = param.empId;
  				}
			    var tdata = [data];
		    	that.getView().getModel("input").setData(data);
			    that.getView().getModel().setData(tdata);
			  }
			});
		},

		_showFormFragment : function () {
			var oPage = this.getView().byId("empDataPage");
			if (!this.oPageFragment) {
			  this.oPageFragment = sap.ui.xmlfragment(this.getView().getId(), "sap.it.sr.ui.view.fragment.EmployeePage", this);
			}
			oPage.addContent(this.oPageFragment);
			if (!this.oTableFragment) {
				this.oTableFragment = sap.ui.xmlfragment(this.getView().getId(), "sap.it.sr.ui.view.fragment.EmployeeTable", this);
			}
			oPage.addContent(this.oTableFragment);
		}
    });

});