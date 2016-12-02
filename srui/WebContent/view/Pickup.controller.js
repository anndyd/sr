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
  var subwin, __empId;
 return BaseController.extend("sap.it.sr.ui.view.Pickup", {

		onInit: function (oEvent) {
			var that = this;
			var oModel = new JSONModel();
			var pModel = new JSONModel({
				badgeId: "",
				empId: "",
				empName: "",
				agentId: "",
				agentName: ""
			});
			that.getView().setModel(oModel);
			that.getView().setModel(pModel, "input");
			that.getView().bindElement("input>/");
			that._showFormFragment();
			// when matched route
			that.getRouter().getRoute("pickup").attachPatternMatched(that.onRouteMatched, that);
			// message listener
			window.addEventListener("message", that.onMessage.bind(that));
		},
		
		onRouteMatched: function (evt) {
			// open new window
//			subwin = util.openSecondWindow("/srui/index.html#/pickup2", 'SecondWindow');
		},

	    onMessage : function (evt) {
	    	var that = this;
	    	__empId = evt.data;
			var param = {badgeId: "", empId: evt.data};
			
			ps.getPickupData(param).done(function(data){
				that.getView().getModel("input").setData(data);
//				that.getView().getModel().refresh();
			});
	    },
		
		onBadgeChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			if (v && v.length === 8) {
				var param = {badgeId: v, empId: ""};
				that._getEmployeeAndPickupData(param);
			}
		},

		postMsg : function (param) {
			if (__empId !== param) {
				__empId = param;
				subwin.postMessage(param, "*");
			}
		},
		
		onEmpChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			var oModel = that.getView().getModel();
			if (v && v.length > 6) {
				var param = {badgeId: "", empId: v};
				that._getEmployeeAndPickupData(param);
			} else {
				oModel.getData().empName = null;
				oModel.refresh();
			}
			
		},
		
		onAgentChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			var oModel = that.getView().getModel("input");
			if (v && v.length > 6) {
				var param = {badgeId: "", empId: v};
				
				if (oModel.getData().agentName === null) {
					es.getEmployee(param).done(function(data){
						oModel.getData().agentName = data.empName;
						oModel.refresh();
					});
				}
			} else {
				oModel.getData().agentName = null;
				oModel.refresh();
			}
		},

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
//			sap.ui.core.BusyIndicator.show();
			var pData = that.getView().getModel("input").getData();
			var param = that.getView().getModel().getData();
			// for updating pickup data
			param.empId = pData.empId;
			param.badgeId = pData.badgeId;
			param.agentId = pData.agentId;
			param.pickupTime = new Date();
			
			ps.upsertPickupData(param).done(function(){
				MessageToast.show(that.getResourceBundle().getText("pickupS"));
			});
//			sap.ui.core.BusyIndicator.hide();
		},

		_getEmployeeAndPickupData : function (param) {
			var that = this;
			// get employee data
			var pModel = that.getView().getModel("input");
			es.getEmployee(param).done(function(empData) {
				pModel.getData().empName = empData.empName;
			});
			// get pickup data
			var pData = pModel.getData();
			var paramP = {
				empId : param.empId
			};
			ps.findPickupData(paramP).done(function(pickData) {
				that.getView().getModel().setData(pickData);
				// post message to sub window
				// that.postMsg(data.empId);
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
			oPage.addContent(this.oFormFragment);
		}
  });

});