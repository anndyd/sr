var util = {
    isLogin: false,
    userName: "",
    role: "",
    badgeIdLength: 10,
    openSecondWindow: function (url, winName) {
		var orgwin = window.open('', winName);
		if (orgwin) {
			orgwin.close();
		}
		// open new window
		orgwin = window.open(url, winName, 'fullscreen=1, toolbar=0, menubar=0, status=0, screenX=' + 
				 window.screen.availWidth + ', left=' + window.screen.availWidth);
		return orgwin;
    }
};

