sap.ui.define([
    'jquery.sap.global',
    "sap/it/sr/ui/view/base/BaseController",
    "sap/it/sr/ui/service/BaseService",
    "sap/it/sr/ui/service/PickupService",
    "sap/it/sr/ui/service/EmployeeService",
    "sap/m/MessageToast",
    'sap/ui/model/json/JSONModel'
  ], function(jQuery, BaseController, BaseService, PickupService, EmployeeService, MessageToast, JSONModel) {
  "use strict";
  var ps = new PickupService();
  var es = new EmployeeService();
  var subwin, __empId = "";
  var noempTimeout = null;

 return BaseController.extend("sap.it.sr.ui.view.Pickup", {

		onInit: function (oEvent) {
			// only enable on secondary screen
			//this.getOwnerComponent().byId("app").byId("idAppControl").setMode(sap.m.SplitAppMode.HideMode);
			var that = this;
			var oModel = new JSONModel();
			var pModel = new JSONModel({
			  cpart: false
			});
			that.getView().setModel(oModel);
			that.getView().setModel(pModel, "input");
			that.getView().bindElement("input>/");
			that._showFormFragment();
			// when matched route, only enable on primary screen
			that.getRouter().getRoute("pickup").attachPatternMatched(that.onRouteMatched, that);
			// message listener
			window.addEventListener("message", that.onMessage.bind(that));
		},
	    onAfterRendering: function() {
//	      var navigationList = this.getView().byId('navigationList');
//	      navigationList.itemSelect(oControlEvent); 
//			this.getView().byId("pickupPage-cont").addStyleClass("srpagesection");
	      
	    },
		// only enable on primary screen
		onRouteMatched: function (evt) {
			// open new window
			subwin = util.openSecondWindow("/srui/index.html#/pickup2", 'SecondWindow');
		},

		postMsg : function (param) {
			if (__empId !== param.empId) {
				__empId = param.empId;
				// only enable on primary screen
				subwin.postMessage({id: "pick", data: param}, "*");
				// only enable on secondary screen
				//window.opener.postMessage(param, "*");
			}
		},

	    onMessage : function (evt) {
	    	var that = this;
	    	if (evt.data && evt.data.id === "pick2") {
	    		var data = evt.data.data;
		    	__empId = data.empId;
		    	// refresh page input value
				that.getView().getModel("input").setData(data);
				that.getView().getModel("input").refresh();
		    	// find pickup data
				var param = {badgeId: "", empId: __empId};
				that._getEmployeeAndPickupData(param);
	    	}
	    },
		
		onBadgeChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			if (v && v.length >= util.badgeIdLength-1) {
				var param = {badgeId: v, empId: ""};
				that._getEmployeeAndPickupData(param);
			}
		},
		
		onEmpChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			var oModel = that.getView().getModel("input");
			if (oModel.getData()) {
				oModel.getData().empName = "";
				oModel.refresh();
			}
			if (v && v.length > 6) {
				var param = {badgeId: "", empId: v};
				that._getEmployeeAndPickupData(param);
			}
		},
		
		onAgentChange: function (evt) {
			var that = this;
			var v = evt.getParameters().value;
			var oModel = that.getView().getModel("input");
			oModel.getData().agentName = "";
			oModel.refresh();
			if (v && v.length > 6) {
				var param = {badgeId: "", empId: v};
				es.getEmployee(param).done(function(data){
					oModel.getData().agentName = data.empName;
					oModel.refresh();
				});
			}
		},
		// for cpart pickup, not use now
		/*
		onPoNumChange: function (evt) {
	      var that = this;
	      var param = {
	          poNum : evt.getParameters().value
	      };
	      ps.findPickupDataByPo(param).done(function(pickData) {
	        that.getView().getModel().setData(pickData);
	        // post message to sub window
	        // that.postMsg(data.empId);
	      });
		},*/

		onCollapseAll : function () {
			var oTreeTable = this.getView().byId("pkTreeTable");
			oTreeTable.collapseAll();
		},

		onExpandFirstLevel : function () {
			var oTreeTable = this.getView().byId("pkTreeTable");
			oTreeTable.expandToLevel(1);
		},
		onSelect: function (evt){
			var that = this;
		},
		
		onExit : function () {
			if (this.oFormFragment) {
				this.oFormFragment.destroy();
			}
			if (this.oPageFragment) {
				this.oPageFragment.destroy();
			}
			window.removeEventListener("message", onMessage.bind(this));
			subwin.close();
		},
		
		handlePickupPress : function () {
			var that = this;
			sap.ui.core.BusyIndicator.show();
			var pData = that.getView().getModel("input").getData();
			var tdata = that.getView().getModel().getData();
			// get table selected data
			var selectedIdx = that.getView().byId("pkTreeTable").getSelectedIndices();
			if (!selectedIdx || selectedIdx.length === 0) {
				sap.ui.core.BusyIndicator.hide();
				MessageToast.show(that.getResourceBundle().getText("noSelection"));
				return;
			}
			// for updating pickup data
			var param = {
				empId: pData.empId,                
				badgeId: pData.badgeId,            
				agentId: pData.agentId,            
				pickupTime: (new Date()).getTime(),	
				items: []
			};
//			selectedIdx.forEach(function(idx) {
//				param.items.push(tdata.items[idx]);
//			});
			that._parseSelectedData(param, tdata, selectedIdx);
			console.log(param);
			
			
			ps.upsertPickupData(param).done(function(){
				MessageToast.show(that.getResourceBundle().getText("pickupS"));
				that.getView().getModel("input").setData({});
				that.getView().getModel().setData({});
				var pm = {
					empId : null
				}
				// post message to another window
		        that.postMsg(pm);
			});
		},
		_parseSelectedData: function (param, tdata, indices) {
			var that = this;
			var ttb = that.getView().byId("pkTreeTable");
			var itmsBk = {};
			$.extend(true, itmsBk, tdata.items);
			var itmIdx = -1;
			var newItmIdx = -1;
			var needClean = true;
			for (var i=0; i<indices.length; i++) {
				var idxs = ttb.getContextByIndex(indices[i]).sPath.match(/\d+/g);
				
				if (idxs[0] !== itmIdx) {
					param.items.push($.extend(true, {}, itmsBk[idxs[0]]));
					newItmIdx++;
					needClean = true;
					if (idxs.length === 2) {
						needClean = false;
						param.items[newItmIdx].itemDetails.length = 0;
						param.items[newItmIdx].itemDetails.push($.extend(true, {}, itmsBk[idxs[0]].itemDetails[idxs[1]]));
					}
				} else {
					if (needClean) {
						param.items[newItmIdx].itemDetails.length = 0;
						needClean = false;
					}
					param.items[newItmIdx].itemDetails.push($.extend(true, {}, itmsBk[idxs[0]].itemDetails[idxs[1]]));
				}
				itmIdx = idxs[0];
			}
		},
		
		_getEmployeeAndPickupData : function (param) {
 			var that = this;
	      // get employee data
	      var pModel = that.getView().getModel("input");
	      es.getEmployee(param).done(function(empData) {
	        pModel.getData().badgeId = param.badgeId && param.badgeId.length > 0 ? param.badgeId : empData.badgeId;
	        pModel.getData().empName = empData.empName;
	        pModel.getData().empId = param.empId && param.empId.length > 0 ? param.empId : empData.empId;
	        pModel.refresh();
	        // post message to another window
	        that.postMsg(pModel.getData());
	        if (empData && empData.empId !== null) {
	          clearTimeout(noempTimeout);
	          noempTimeout = null;
	          var paramP = {
	            empId : empData.empId
	          };
	          ps.findPickupData(paramP).done(function(pickData) {
	            that.getView().getModel().setData(pickData);
	            // for cpart pickup, not use now
	            // that.getView().byId("chkCpart").setVisible(pickData.items.length === 0);
	            if (!pickData || pickData.items.length === 0) {
	              MessageToast.show(that.getResourceBundle().getText("pickupDataNotFound"));
	              setTimeout(function() {
	                that.getView().getModel("input").setData({});
	                that.getView().getModel().setData({});
	              }, util.nodataTimeout);
	            } else {
	            	setTimeout(function() {
		            	var oTreeTable = that.getView().byId("pkTreeTable");
		            	oTreeTable.selectAll();
	            	}, 50);
	            }
	          });
	        } else {
	          that.getView().getModel().setData({});
	          MessageToast.show(that.getResourceBundle().getText("employeeNotFound"));
	          noempTimeout = setTimeout(function() {
	            that.getView().getModel("input").setData({});
	            that.getView().getModel().setData({});
	          }, util.nodataTimeout);
	        }
	      });
	    },
		
		_showFormFragment : function () {
			var oPage = this.getView().byId("pickupPage");
			if (!this.oPageFragment) {
				this.oPageFragment = sap.ui.xmlfragment(this.getView().getId(), "sap.it.sr.ui.view.fragment.PickupPage", this);
			}
			oPage.addContent(this.oPageFragment);
			if (!this.oFormFragment) {
				this.oFormFragment = sap.ui.xmlfragment(this.getView().getId(), "sap.it.sr.ui.view.fragment.PoList", this);
			}
			oPage.addContent(this.oFormFragment.addStyleClass("srpagesection"));
		}
  });

});