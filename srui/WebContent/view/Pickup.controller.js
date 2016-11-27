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
			var pModel = new JSONModel();
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
				
				ps.getPickupData(param).done(function(data){
					that.getView().getModel("input").setData(data);
//					that.getView().getModel().refresh();
					// post message to sub window
					that.postMsg(data.empId);
				});
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
				
				ps.getPickupData(param).done(function(data){
					oModel.setData(data);
					// post message to sub window
					that.postMsg(data.empId);
					if (data && data.empName === null) {
						es.getEmployee(param).done(function(data){
							oModel.getData().empName = data.empName;
							oModel.refresh();
						});
					}
				});
			} else {
				oModel.getData().empName = null;
				oModel.refresh();
			}
			
		},
		
		onAgentChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			var oModel = that.getView().getModel();
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
//			sap.ui.core.BusyIndicator.show();
			ps.upsertPickupData(this.getView().getModel().getData()).done(function(){
				MessageToast.show(this.getResourceBundle().getText("pickupS"));
			});
//			sap.ui.core.BusyIndicator.hide();
		},

		_showFormFragment : function () {
			var oPage = this.getView().byId("pickupPage");
			if (!this.oPageFragment) {
				this.oPageFragment = sap.ui.xmlfragment(this.getView().getId(), "sap.it.sr.ui.view.fragment.PickupPage");
			}
			oPage.addContent(this.oPageFragment);
			if (!this.oFormFragment) {
				this.oFormFragment = sap.ui.xmlfragment(this.getView().getId(), "sap.it.sr.ui.view.fragment.PoList");
			}
			oPage.addContent(this.oFormFragment);
		}
  });

});