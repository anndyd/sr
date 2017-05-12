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
			var aModel = new JSONModel();
			pModel.setData({
				empIdFrom : "",
				empIdTo : "",
				costCenter : "",
				dateFrom : "",
				dateTo : "",
				poNumber : "",
				location : "",
				equipNo : ""
			});
//			aModel.setData({
//				locations : [{"Name": "BJ"}, {"Name": "CTU"}, {"Name": "DL"}, {"Name": "GZ"}, {"Name": "NKG"}, {"Name": "PVG"}, {"Name": "SH"}, {"Name": "SZ"}]
//			});
			
			that.getView().setModel(oModel);
			that.getView().setModel(pModel, "input");
			that.getView().setModel(aModel, "assist");
			that.getView().bindElement("input>/");
			that.getView().bindElement("assist>/");
			
//			this.byId("empIdInput").setFilterFunction(function(sTerm, oItem) {
//				// A case-insensitive 'string contains' style filter
//				return oItem.getText().match(new RegExp(sTerm, "i"));
//			});
		},

		onAfterRendering: function (evt) {
      var that = this;
      var aModel = that.getView().getModel("assist");
      $.when(ps.getLocations(), ps.getCostCenters(), ps.getPoNumbers())
      .done(function (ldata, cdata, pdata) {
        aModel.setData({
          locations: ldata,
          costcenter: cdata,
          ponumber: pdata
        })
      });
    },
		
		handleSelectionFinish: function(oEvent) {
			var selectedItems = oEvent.getParameter("selectedItems");
			var locations = "";
			for (var i = 0; i < selectedItems.length; i++) {
				locations += "'" + selectedItems[i].getText() + "'";
				if (i != selectedItems.length-1) {
					locations += ",";
				}
			}
			this.getView().getModel("input").getData().location = locations;
			this.handleRefresh();
		},
		
		handleRefresh : function (oEvent) {
			sap.ui.core.BusyIndicator.show();
			var that = this;

			var param = that.getView().getModel("input").getData();
			var oModel = that.getView().getModel();
			ps.getPickupDatas(param).done(function (data) {
				if (data) {
					var dtxt = that.getResourceBundle().getText("days");
					var htxt = that.getResourceBundle().getText("hours");
					for (var i=0; i<data.length; i++) {
						var h = data[i].usedTime;
						var d = Math.floor(h/24);
						data[i].usedTime = d + " " + dtxt + " " + (h-d*24) + " " + htxt + " ";
					}
				}
				oModel.setData(data);
				oModel.refresh();
			});
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
			var oModel = this.getView().getModel();
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
						name : i18n.getText("grTime"),
						template : {
							content : "{grTime}"
						}
					}, {
						name : i18n.getText("pickupTime"),
						template : {
							content : "{pickupTime}"
						}
					}, {
						name : i18n.getText("usedTime"),
						template : {
							content : "{usedTime}"
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
						name : i18n.getText("price"),
						template : {
							content : "{price}"
						}
					}, {
						name : i18n.getText("equipNo"),
						template : {
							content : "{equipNo}"
						}
					}, {
						name : i18n.getText("serialNo"),
						template : {
							content : "{serialNo}"
						}
					}, {
						name : i18n.getText("costCenter"),
						template : {
							content : "{costCenter}"
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