sap.ui.define([
    'jquery.sap.global',
    "sap/it/sr/ui/view/base/BaseController",
    "sap/it/sr/ui/service/EmployeeService",
    "sap/m/MessageToast",
	'sap/ui/core/util/Export',
	'sap/ui/core/util/ExportTypeCSV',
    'sap/ui/model/json/JSONModel'
  ], function(jQuery, BaseController, EmployeeService, MessageToast, Export, ExportTypeCSV, JSONModel) {
  "use strict";
  var es = new EmployeeService();
 return BaseController.extend("sap.it.sr.ui.view.ExportEmployeeData", {

		onInit: function (oEvent) {
			var that = this;
			var oModel = new JSONModel();
			
			that.getView().setModel(oModel);
			that.getView().bindElement("/");
			that.refreshData();
		},

		handleRefresh: function (oEvent) {
			this.refreshData();
		},
		
		refreshData: function () {
			sap.ui.core.BusyIndicator.show();
			var that = this;
			
			es.getAllEmployees().done(function(data){
				that.getView().getModel().setData(data);
				
			});

			sap.ui.core.BusyIndicator.hide();
		},

		onExit : function () {
		},

		handlePress : sap.m.Table.prototype.exportData || function () {
			sap.ui.core.BusyIndicator.show();
			var i18n = this.getResourceBundle();
			var oExport = new Export({

				// Type that will be used to generate the content. Own ExportType's can be created to support other formats
				exportType : new ExportTypeCSV({
					separatorChar : ","
				}),

				// Pass in the model created above
				models : this.getView().getModel(),

				// binding information for the rows aggregation
				rows : {
					path : "/"
				},

				// column definitions with column name and binding info for the content
				columns : [{
					name : i18n.getText("empId"),
					template : {
						content : "{empId}"
					}
				}, {
					name : i18n.getText("badgeId"),
					template : {
						content : "{badgeId}"
					}
				}, {
					name : i18n.getText("empName"),
					template : {
						content : "{empName}"
					}
				}]
			});

			// download exported file
			oExport.saveFile("EmployeeBadgeInfo").catch(function(oError) {
				MessageBox.error(i18n.getText("exportError") + oError);
			}).then(function() {
				oExport.destroy();
			});
			
			sap.ui.core.BusyIndicator.hide();
			MessageToast.show(i18n.getText("exportS"));
		}		
  });

});