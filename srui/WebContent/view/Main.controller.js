sap.ui.define([
    'jquery.sap.global',
    "sap/it/sr/ui/view/base/BaseController",
    'sap/ui/model/json/JSONModel'
  ], function(jQuery, BaseController, JSONModel) {
  "use strict";

  return BaseController.extend("sap.it.sr.ui.view.Main", {

    onInit : function (evt) {
      /*var sPath = jQuery.sap.getModulePath("sap.it.sr.ui", "/config/mainTiles.json");
      var oModel = new JSONModel(sPath);
      this.getView().setModel(oModel);

      */
      var i18n = this.getView().getModel("i18n");
      var tileData =[ 
		    {
				"icon" : "cart-3",
				"title" : this.getResourceBundle().getText("pickup"),
				"key" : "pickup"
			}, 
			{
				"icon" : "request",
				"title" : this.getResourceBundle().getText("itticket"),
				"key" : "unavailable"
//			}, 
//			{
//				"icon" : "multi-select",
//				"title" : this.getResourceBundle().getText("assetcheck"),
//				"key" : "assetcheck"
			}
      ];
      var tileData2 =[ 
//         		    {
//         				"icon" : "bar-code",
//         				"title" : this.getResourceBundle().getText("scanbadge"),
//         				"key" : "scanBadge"
//         			}, 
//         			{
//         				"icon" : "cause",
//         				"title" : this.getResourceBundle().getText("importdata"),
//         				"key" : "importdata"
//         			}, 
//         			{
//         				"icon" : "detail-view",
//         				"title" : this.getResourceBundle().getText("queryinfo"),
//         				"key" : "queryinfo"
//         			}, 
         			{
         				"icon" : "kpi-managing-my-area",
         				"title" : this.getResourceBundle().getText("updatedata"),
         				"key" : "empData"
         			}
               ];
      var tileData3 =[ 
         		    {
         				"icon" : "switch-classes",
         				"title" : this.getResourceBundle().getText("empdata"),
         				"key" : "exportEmpData"
         			}, 
         			{
         				"icon" : "history",
         				"title" : this.getResourceBundle().getText("pickupdata"),
         				"key" : "exportPickupData"
         			}
               ];
      
      this.tileFragment = sap.ui.xmlfragment(
              "sap.it.sr.ui.view.fragment.CustomTile", this);
      this.createTiles(this.tileFragment, tileData, "frqTileContainer");
      this.createTiles(this.tileFragment, tileData2, "importTileContainer");
      this.createTiles(this.tileFragment, tileData3, "exportTileContainer");
    },
    
    createTiles : function (oTile, data, tileId) {
      var oTitleGrid = this.getView().byId(tileId);
      oTitleGrid.removeAllContent();
      if (jQuery.isArray(data)) {
        for (var i = 0; i < data.length; i++) {
          var targetTile = oTile.clone();
          if (data[i].key === "unavailable") {
        	  targetTile.addStyleClass("notavaTile");
          }
          
          var oModel = new JSONModel(data[i]);
          targetTile.setModel(oModel);
          //targetTile.addStyleClass(cls);
          oTitleGrid.addContent(targetTile);
        }
      };
    },
    onTilePress : function(evt){
      var texts = evt.getSource().findElements(true);
      if (texts && texts.length > 5) {
        var key = texts[5].getText();
        this.getRouter().navTo(key);
      }
    }
  });

});
