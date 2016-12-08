sap.ui.define([
		'jquery.sap.global',
		"sap/it/sr/ui/view/base/BaseController",
		"sap/it/sr/ui/service/PickupService",
		"sap/m/MessageToast",
		'sap/ui/core/util/Export',
		'sap/ui/core/util/ExportTypeCSV',
		'sap/ui/model/json/JSONModel'
	], function (jQuery, BaseController, PickupService, MessageToast, Export, ExportTypeCSV, JSONModel) {
	"use strict";
	var ps = new PickupService();
	return BaseController.extend("sap.it.sr.ui.view.ExportPickupData", {

		onInit : function (oEvent) {
			var that = this;
			var oModel = new JSONModel();
			var pModel = new JSONModel();
			pModel.setData({
				empIdFrom : "",
				empIdTo : "",
				dateFrom : "",
				dateTo : "",
				poNumber : "",
				location : "",
				equipNo : ""
			});

			that.getView().setModel(oModel);
			that.getView().setModel(pModel, "input");
			that.getView().bindElement("input>/");
		},

		handleRefresh : function (oEvent) {
			sap.ui.core.BusyIndicator.show();
			var that = this;

			var param = that.getView().getModel("input").getData();
			var oModel = that.getView().getModel();
			ps.getPickupDatas(param).done(function (data) {
				oModel.setData(data);
				oModel.refresh();
			});
		},

		onCollapseAll : function () {
			var oTreeTable = this.getView().byId("pkTreeTable");
			oTreeTable.collapseAll();
		},

		onExpandFirstLevel : function () {
			var oTreeTable = this.getView().byId("pkTreeTable");
			oTreeTable.expandToLevel(1);
		},

		onExit : function () {},

		handleChange : function (oEvent) {
			var oDP = oEvent.oSource;
			// var sValue = oEvent.getParameter("value");
			var bValid = oEvent.getParameter("valid");
			this._iEvent++;

			if (bValid) {
				oDP.setValueState(sap.ui.core.ValueState.None);
			} else {
				oDP.setValueState(sap.ui.core.ValueState.Error);
			}
		},

		handlePress : sap.m.Table.prototype.exportData || function () {
			sap.ui.core.BusyIndicator.show();
			var oModel = new JSONModel();
			var tdata = [], pd = {};
			var data = this.getView().getModel().getData();
			if (data) {
				for (var i = 0, len = data.length; i < len; i++) {
					pd = {};
					pd.empId = data[i].empId;
					pd.agentId = data[i].agentId;
					pd.pickupTime = data[i].pickupTime;
					tdata.push(pd);
					var itms = data[i].items
					for (var j = 0, lenj = itms.length; j < lenj; j++) {
						pd = {};
						pd.poNumber = itms[j].poNumber;
						pd.poItem = itms[j].poItem;
						pd.itemDesc = itms[j].itemDesc;
						pd.location = itms[j].location;
						pd.quantity = itms[j].quantity;
						tdata.push(pd);
						var itds = itms[j].itemDetails
						for (var k = 0, lenk = itds.length; k < lenk; k++) {
							pd = {};
							pd.serialNo = itds[k].serialNo;
							pd.equipNo = itds[k].equipNo;
							tdata.push(pd);
						}						
					}
				}
			}
			oModel.setData(tdata);
			
			var i18n = this.getResourceBundle();
			var oExport = new Export({

				// Type that will be used to generate the
				// content. Own ExportType's can be created to
				// support other formats
				exportType : new ExportTypeCSV({
					separatorChar : ","
				}),

				// Pass in the model created above
				models : oModel,

				// binding information for the rows aggregation
				rows : {
					path:'/',
					isTreeBinding:true
				},

				// column definitions with column name and
				// binding info for the content
				columns : [{
						name : i18n.getText("empId"),
						template : {
							content : "{empId}"
						}
					}, {
						name : i18n.getText("agentId"),
						template : {
							content : "{agentId}"
						}
					}, {
						name : i18n.getText("pickupTime"),
						template : {
							content : "{pickupTime}"
						}
					}, {
						name : i18n.getText("poNumber"),
						template : {
							content : "{poNumber}"
						}
					}, {
						name : i18n.getText("poItem"),
						template : {
							content : "{poItem}"
						}
					}, {
						name : i18n.getText("desc"),
						template : {
							content : "{itemDesc}"
						}
					}, {
						name : i18n.getText("location"),
						template : {
							content : "{location}"
						}
					}, {
						name : i18n.getText("quantity"),
						template : {
							content : "{quantity}"
						}
					}, {
						name : i18n.getText("serialNo"),
						template : {
							content : "{serialNo}"
						}
					}, {
						name : i18n.getText("equipNo"),
						template : {
							content : "{equipNo}"
						}
					}
				]
			});

			// download exported file
			oExport.saveFile("PickupHistory").catch (function (oError) {
				MessageBox.error(i18n.getText("exportError") + oError);
			})
				.then(function () {
					oExport.destroy();
				});

			sap.ui.core.BusyIndicator.hide();
			MessageToast.show(i18n.getText("exportS"));
		}
	});

});