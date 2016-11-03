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
 return BaseController.extend("sap.it.sr.ui.view.Pickup", {

		onInit: function (oEvent) {
			var that = this;
			var oModel = new JSONModel();
			var param = {badgeId: "", empId: ""};
			
			ps.getPickupData(param).done(function(data){
				oModel.setData(data);
			}).always(function(){
				that.getView().setModel(oModel);
				that.getView().bindElement("/");
			});
			that._showFormFragment();
		},
		
		onBadgeChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			if (v && v.length === 8) {
				var param = {badgeId: v, empId: ""};
				
				ps.getPickupData(param).done(function(data){
					that.getView().getModel().setData(data);
//					that.getView().getModel().refresh();
				});
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
		},

		handlePickupPress : function () {
//			sap.ui.core.BusyIndicator.show();
			ps.upsertPickupData(this.getView().getModel().getData());
//			sap.ui.core.BusyIndicator.hide();
			MessageToast.show(this.getResourceBundle().getText("pickupS"));
		},

		_showFormFragment : function (sFragmentName) {
			var oPage = this.getView().byId("pickupPage");
			if (!this.oFormFragment) {
				this.oFormFragment = sap.ui.xmlfragment(this.getView().getId(), "sap.it.sr.ui.view.fragment.PoList");
			}
			oPage.addContent(this.oFormFragment);
		}
  });

});