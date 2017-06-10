sap.ui.define([
    'jquery.sap.global',
    "sap/it/sr/ui/view/base/BaseController",
    "sap/it/sr/ui/service/UserService",
    'sap/ui/model/json/JSONModel'
  ], function(jQuery, BaseController, UserService, JSONModel) {
  "use strict";
  var us = new UserService();
  return BaseController.extend("sap.it.sr.ui.view.Main", {

    onInit : function (evt) {
    	var i18n = this.getResourceBundle();
		var tileData = [ 
		    {
				"icon" : "cart-3",
				"title" : i18n.getText("pickup"),
				"tileId" : "pickup",
				"info" : i18n.getText("pickupTitle")
			}, 
//			{
//				"icon" : "request",
//				"title" : i18n.getText("itticket"),
//				"tileId" : "unavailable"
//			}, 
//			{
//				"icon" : "multi-select",
//				"title" : i18n.getText("assetcheck"),
//				"tileId" : "assetcheck"
//			}
// 		    {
// 				"icon" : "bar-code",
// 				"title" : i18n.getText("scanbadge"),
// 				"tileId" : "scanBadge"
// 			}, 
// 			{
// 				"icon" : "cause",
// 				"title" : i18n.getText("importdata"),
// 				"tileId" : "importdata"
// 			}, 
// 			{
// 				"icon" : "detail-view",
// 				"title" : i18n.getText("queryinfo"),
// 				"tileId" : "queryinfo"
// 			}, 
 			{
 				"icon" : "kpi-managing-my-area",
 				"title" : i18n.getText("updatedata"),
 				"tileId" : "empData",
				"info" : i18n.getText("empDataTitle")
 			},
 		    {
 				"icon" : "switch-classes",
 				"title" : i18n.getText("empdata"),
 				"tileId" : "exportEmpData",
				"info" : i18n.getText("exportEmployeeData")
 			}, 
 			{
 				"icon" : "history",
 				"title" : i18n.getText("pickupdata"),
 				"tileId" : "exportPickupData",
				"info" : i18n.getText("exportPickupData")
 			}
        ];
		var oModel = new JSONModel();
		oModel.setData(tileData);
		this.getView().setModel(oModel);
    },

    onBeforeRendering : function (evt) {
      var that = this;
      $.when(us.getSession()).done(function (udata) {
    	if (udata.role === "1" || udata.role === "2") {
    		// do nothing
    	} else {
    	  that.getRouter().navTo("exportPickupData");
    	}
      });
    },
    
    onTilePress : function(evt){
    	var oData = this.getView().getModel().getData();
    	var tileIdx = evt.getSource().sId.slice(-1);
		if (oData && tileIdx && oData.length > 0 && tileIdx > -1) {
			this.getRouter().navTo(oData[tileIdx].tileId);
		}
    }
  });

});
