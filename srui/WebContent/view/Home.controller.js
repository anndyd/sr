sap.ui.define([
          "sap/it/sr/ui/view/base/BaseController",
          'jquery.sap.global', 
          'sap/ui/core/mvc/Controller', 
          'sap/m/Popover', 
          'sap/m/Button'
          ], function(BaseController, jQuery, Popover, Button) {
  "use strict";

  return BaseController.extend("sap.it.sr.ui.view.Home", {

    onInit : function() {
    },
    onAfterRendering: function() {
//      var navigationList = this.getView().byId('navigationList');
//      navigationList.itemSelect(oControlEvent); 
      
    },

    onCollapseExapandPress : function(event) {
      var navigationList = this.getView().byId('navigationList');
      var expanded = !navigationList.getExpanded();

      navigationList.setExpanded(expanded);
    },
    onSelectItem : function(event) {
        var txt = event.getParameters().item.getKey();
        if (txt) {
        	this.getRouter().navTo(txt);
        }
    }
  });

/**
 * Called when a controller is instantiated and its View controls (if available) are already created. Can be used to
 * modify the View before it is displayed, to bind event handlers and do other one-time initialization.
 * 
 * @memberOf view.Home
 */
//	onInit: function() {
//
//	},

/**
* Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
* (NOT before the first rendering! onInit() is used for that one!).
* @memberOf view.Home
*/
//	onBeforeRendering: function() {
//
//	},

/**
* Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
* This hook is the same one that SAPUI5 controls get after being rendered.
* @memberOf view.Home
*/
//	onAfterRendering: function() {
//
//	},

/**
* Called when the Controller is destroyed. Use this one to free resources and finalize activities.
* @memberOf view.Home
*/
//	onExit: function() {
//
//	}

});