sap.ui.define([
       		"sap/ui/core/mvc/Controller"
    	], function(Controller) {
	"use strict";

	var Formatter = {

		userStatus :  function (fValue) {
			try {
//				var i18n = this.getResourceBundle();
				if (fValue) {
					return "Active";//i18n.getText("active");
				} else {
					return "Inactive";//i18n.getText("inactive");
				}
			} catch (err) {
				return "None";
			}
		}
	};


	return Formatter;

}, /* bExport= */ true);
