var util = {
    isLogin: false,
    sessionInfo: {},
    badgeIdLength: 10,
    nodataTimeout: 5000,
    openSecondWindow: function (url, winName) {
  		var orgwin = window.open('', winName);
  		if (orgwin) {
  			orgwin.close();
  		}
  		// open new window
  		orgwin = window.open(url, winName, 'fullscreen=1, toolbar=0, menubar=0, status=0, screenX=' + 
  				 window.screen.availWidth + ', left=' + window.screen.availWidth);
  		setTimeout(function() {
    	  if (orgwin.screenLeft !== window.screen.availWidth) {
    	    orgwin.close();
    	    return null;
    	  }
  		},100);
  		return orgwin;
    }
};

