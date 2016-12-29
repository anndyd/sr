sap.ui.define([
    'jquery.sap.global',
    "sap/it/sr/ui/view/base/BaseController",
    "sap/it/sr/ui/service/PickupService",
    "sap/it/sr/ui/service/EmployeeService",
    "sap/m/MessageToast",
    'sap/ui/model/json/JSONModel'
  ], function(jQuery, BaseController, PickupService, EmployeeService, MessageToast, JSONModel) {
  "use strict";
  var ps = new PickupService();
  var es = new EmployeeService();
  var __empId = "";
 return BaseController.extend("sap.it.sr.ui.view.Pickup2", {

		onInit: function (oEvent) {
			// only enable on secondary screen
			this.getOwnerComponent().byId("app").byId("idAppControl").setMode(sap.m.SplitAppMode.HideMode);
			var that = this;
			var oModel = new JSONModel();
			var pModel = new JSONModel({
			  cpart: false
			});
			that.getView().setModel(oModel);
			that.getView().setModel(pModel, "input");
			that.getView().bindElement("input>/");
			that._showFormFragment();
			// when matched route, only enable on primary screen
			//that.getRouter().getRoute("pickup").attachPatternMatched(that.onRouteMatched, that);
			// message listener
			window.addEventListener("message", that.onMessage.bind(that));
		},
		// only enable on primary screen
		onRouteMatched: function (evt) {
			// open new window
			//subwin = util.openSecondWindow("/srui/index.html#/pickup2", 'SecondWindow');
		},

		postMsg : function (param) {
			if (__empId !== param.empId) {
				__empId = param.empId;
				// only enable on primary screen
				//subwin.postMessage(param, "*");
				// only enable on secondary screen
				window.opener.postMessage(param, "*");
			}
		},

	    onMessage : function (evt) {
	    	var that = this;
	    	__empId = evt.data.empId;
	    	// refresh page input value
			that.getView().getModel("input").setData(evt.data);
			that.getView().getModel("input").refresh();
	    	// find pickup data
			var param = {badgeId: "", empId: __empId};
			that._getEmployeeAndPickupData(param);
	    },
		
		onBadgeChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			if (v && v.length >= util.badgeIdLength-1) {
				var param = {badgeId: v, empId: ""};
				that._getEmployeeAndPickupData(param);
			}
		},
		
		onEmpChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			var oModel = that.getView().getModel("input");
			if (oModel.getData()) {
				oModel.getData().empName = "";
				oModel.refresh();
			}
			if (v && v.length > 6) {
				var param = {badgeId: "", empId: v};
				that._getEmployeeAndPickupData(param);
			}
		},
		
		onAgentChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			var oModel = that.getView().getModel("input");
			oModel.getData().agentName = "";
			oModel.refresh();
			if (v && v.length > 6) {
				var param = {badgeId: "", empId: v};
				es.getEmployee(param).done(function(data){
					oModel.getData().agentName = data.empName;
					oModel.refresh();
				});
			}
		},
		// for cpart pickup, not use now
		/*
		onPoNumChange: function (evt) {
	      var that = this;
	      var param = {
	          poNum : evt.getParameters().value
	      };
	      ps.findPickupDataByPo(param).done(function(pickData) {
	        that.getView().getModel().setData(pickData);
	        // post message to sub window
	        // that.postMsg(data.empId);
	      });
		},*/

		onCollapseAll : function () {
			var oTreeTable = this.getView().byId("pkTreeTable");
			oTreeTable.collapseAll();
		},

		onExpandFirstLevel : function () {
			var oTreeTable = this.getView().byId("pkTreeTable");
			oTreeTable.expandToLevel(1);
		},
		
		onExit : function () {
			if (this.oFormFragment) {
				this.oFormFragment.destroy();
			}
			if (this.oPageFragment) {
				this.oPageFragment.destroy();
			}
			window.removeEventListener("message", onMessage.bind(this));
			subwin.close();
		},
		
		handlePickupPress : function () {
			var that = this;
			sap.ui.core.BusyIndicator.show();
			var pData = that.getView().getModel("input").getData();
			var param = that.getView().getModel().getData();
			// for updating pickup data
			param.empId = pData.empId;
			param.badgeId = pData.badgeId;
			param.agentId = pData.agentId;
			param.pickupTime = (new Date()).getTime();
			
			ps.upsertPickupData(param).done(function(){
				MessageToast.show(that.getResourceBundle().getText("pickupS"));
				that.getView().getModel("input").setData({});
				that.getView().getModel().setData({});
				var pm = {
					empId : ""
				}
				// post message to another window
		        that.postMsg(pm);
			});
		},

		_getEmployeeAndPickupData : function (param) {
			var that = this;
			// get employee data
			var pModel = that.getView().getModel("input");
			es.getEmployee(param).done(function(empData) {
			  pModel.getData().badgeId = param.badgeId.length > 0 ? param.badgeId: empData.badgeId;
			  pModel.getData().empName = empData.empName;
			  pModel.getData().empId = param.empId.length > 0 ? param.empId: empData.empId;
			  pModel.refresh();
	          // post message to another window
	          that.postMsg(pModel.getData());
			  if (empData && empData.empId !== null) {
			    var paramP = {
		          empId : empData.empId
		        };
		        ps.findPickupData(paramP).done(function(pickData) {
		          that.getView().getModel().setData(pickData);
		          // for cpart pickup, not use now
		          // that.getView().byId("chkCpart").setVisible(pickData.items.length === 0);
		          if (!pickData || pickData.items.length === 0) {
		        	MessageToast.show(that.getResourceBundle().getText("pickupDataNotFound"));
					setTimeout(function () {
						that.getView().getModel("input").setData({});
						that.getView().getModel().setData({});
					}, 3000);
		          }
		        });
			  } else {
				that.getView().getModel().setData({});
			    MessageToast.show(that.getResourceBundle().getText("employeeNotFound"));
				setTimeout(function () {
					that.getView().getModel("input").setData({});
					that.getView().getModel().setData({});
				}, 3000);
			  }
			});
		},
		
		_showFormFragment : function () {
			var oPage = this.getView().byId("pickupPage");
			if (!this.oPageFragment) {
				this.oPageFragment = sap.ui.xmlfragment(this.getView().getId(), "sap.it.sr.ui.view.fragment.PickupPage", this);
			}
			oPage.addContent(this.oPageFragment);
			if (!this.oFormFragment) {
				this.oFormFragment = sap.ui.xmlfragment(this.getView().getId(), "sap.it.sr.ui.view.fragment.PoList", this);
			}
			oPage.addContent(this.oFormFragment.addStyleClass("srpagesection"));
		}
 	});

});