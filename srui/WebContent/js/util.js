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
//window.open(thePage, 'windowName', 'resizable=1, scrollbars=1, fullscreen=0, height=200, width=650, screenX=' + window.leftScreenBoundry() + ' , left=' + window.leftScreenBoundry() + ', toolbar=0, menubar=0, status=1');
//E:\tmp\ushell-lib-1.42.1-testresources\META-INF\test-resources\sap\ushell\demoapps\PostMessageTestApp\PostMessageTest.view.js

