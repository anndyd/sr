sap.ui.jsview("sap.it.sr.ui.view.TestDelegate", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.TestDelegate
	*/ 
	getControllerName : function() {
		return "sap.it.sr.ui.view.TestDelegate";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.TestDelegate
	*/ 
	createContent : function(oController) {
// 		return new sap.m.Page({
//			title: "Title"
//		}).addEventDelegate({
//	        onAfterHide: function(evt) {
//	            sap.m.MessageToast.show("evt.data.foo")
//	          }
//	        });
 		
 		return new sap.m.Page({ 
 	        title: "firstDetail",
 	        content: new sap.m.Button({ 
 	          text: "to page 2",
 	          press: function () {
 	            splitApp.toDetail(secondDetailPage.getId(),"show",{foo :"bar"});
 	          }
 	        })
 	      }).addEventDelegate({
 	        onAfterHide: function(evt) {
 	          sap.m.MessageToast.show("page 1 hide")
 	        }
 	      });
	}

});