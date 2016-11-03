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
 return BaseController.extend("sap.it.sr.ui.view.Login", {

		onInit: function (oEvent) {
		},
		
		onExit : function () {
		},

		handleLoginPress : function () {
//			sap.ui.core.BusyIndicator.show();
//			ps.upsertPickupData(this.getView().getModel().getData());
//			sap.ui.core.BusyIndicator.hide();
			MessageToast.show(this.getResourceBundle().getText("pickupS"));
		}
		
  });

});