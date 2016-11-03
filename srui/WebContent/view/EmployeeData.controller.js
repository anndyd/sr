sap.ui.define([
    'jquery.sap.global',
    "sap/it/sr/ui/view/base/BaseController",
    "sap/it/sr/ui/service/EmployeeService",
    "sap/m/MessageToast",
    'sap/ui/model/json/JSONModel'
  ], function(jQuery, BaseController, EmployeeService, MessageToast, JSONModel) {
  "use strict";
  var es = new EmployeeService();
  return BaseController.extend("sap.it.sr.ui.view.EmployeeData", {

		onInit: function (oEvent) {
			var that = this;
			var oModel = new JSONModel();
			var param = {badgeId: "", empId: ""};
			
			es.getEmployee(param).done(function(data){
				oModel.setData(data);
			}).always(function(){
				that.getView().setModel(oModel);
				that.getView().bindElement("/");
			});
//			that._showFormFragment();
		},
		
		onEmpChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			if (v && v.length > 6) {
				var param = {badgeId: "", empId: v};
				
				es.getEmployee(param).done(function(data){
					that.getView().getModel().setData(data);
				});
			}
		},

		onExit : function () {
			if (this.oFormFragment) {
				this.oFormFragment.destroy();
			}
		},

		handleSavePress : function () {
//			sap.ui.core.BusyIndicator.show();
			es.upsertEmployee(this.getView().getModel().getData());
//			sap.ui.core.BusyIndicator.hide();
			MessageToast.show(this.getResourceBundle().getText("updateEmpS"));
		},

		_showFormFragment : function (sFragmentName) {
			var oPage = this.getView().byId("empDataPage");
			if (!this.oFormFragment) {
				this.oFormFragment = sap.ui.xmlfragment(this.getView().getId(), "sap.it.sr.ui.view.fragment.PoList");
			}
			oPage.addContent(this.oFormFragment);
		}
  });

});