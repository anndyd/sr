var util = {
    isLogin: false,
    userName: "",
    role: "",
    openLoginWindow: function () {
    	var oControl = sap.ui.getCore().byId("login_view");
        oControl && oControl.destroy();
        var oLoginView = sap.ui.view({ id: "login_view", viewName: "sap.it.sr.ui.view.Login", type: sap.ui.core.mvc.ViewType.XML });
//        oLoginView.oController.open();

//        sap.ui.getCore().byId("main_shell").removeAllWorksetItems();
        sap.ui.getCore().byId("my_shell").setContent(sap.ui.getCore().byId("login_view"));
    },
    logoff: function () {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: sbo.basistools.getRootPath() + "/WS/WebCommonWS.asmx/LogOff",
            dataType: 'json',
            success: function (result) {
                SAPAnywhereCD.isLogin = false;
                var button = sap.ui.getCore().byId('button_login');
                button.setIcon(sap.ui.core.IconPool.getIconURI("person-placeholder"));
                button.setTooltip('Login');
                window.location.reload();
            },
            error: function (msg) {
                window.location.reload();
            }
        });
    },
};
// Find Left Boundry of the Screen/Monitor
function FindLeftScreenBoundry () {
	// Check if the window is off the primary monitor in a positive axis
	// X,Y                  X,Y                    S = Screen, W = Window
	// 0,0  ----------   1280,0  ----------
	//     |          |         |  ---     |
	//     |          |         | | W |    |
	//     |        S |         |  ---   S |
	//      ----------           ----------
	if (window.leftWindowBoundry() > window.screen.width)
	{
		return window.leftWindowBoundry() - (window.leftWindowBoundry() - window.screen.width);
	}

	// Check if the window is off the primary monitor in a negative axis
	// X,Y                  X,Y                    S = Screen, W = Window
	// 0,0  ----------  -1280,0  ----------
	//     |          |         |  ---     |
	//     |          |         | | W |    |
	//     |        S |         |  ---   S |
	//      ----------           ----------
	// This only works in Firefox at the moment due to a bug in Internet Explorer opening new windows into a negative axis
	// However, you can move opened windows into a negative axis as a workaround
	if (window.leftWindowBoundry() < 0 && window.leftWindowBoundry() > (window.screen.width * -1))
	{
		return (window.screen.width * -1);
	}

	// If neither of the above, the monitor is on the primary monitor whose's screen X should be 0
	return 0;
}

window.leftScreenBoundry = FindLeftScreenBoundry;
//window.open(thePage, 'windowName', 'resizable=1, scrollbars=1, fullscreen=0, height=200, width=650, screenX=' + window.leftScreenBoundry() + ' , left=' + window.leftScreenBoundry() + ', toolbar=0, menubar=0, status=1');
//E:\tmp\ushell-lib-1.42.1-testresources\META-INF\test-resources\sap\ushell\demoapps\PostMessageTestApp\PostMessageTest.view.js

