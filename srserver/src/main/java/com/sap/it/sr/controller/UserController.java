package com.sap.it.sr.controller;


import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.it.sr.dao.UserDao;
import com.sap.it.sr.dto.SessionInfo;
import com.sap.it.sr.entity.User;
import com.sap.it.sr.util.EncryptHelper;
import com.sap.it.sr.util.SessionHolder;

@Controller
@RequestMapping("user")
@Scope("request")
public class UserController {
	@Autowired(required=true)
	private HttpServletRequest request;	
    
	@Autowired
    private UserDao uDao;

	@RequestMapping(value="/all", method = RequestMethod.GET)
	@ResponseBody
	public List<User> getUsers(){
		List<User> users = uDao.findAll();
		return users;
	}

	@RequestMapping(value="/get", method = RequestMethod.GET)
	@ResponseBody
	public User getUser(@RequestParam(required = true) String userName){
	    if (userName != null) {
	        userName = userName.toUpperCase();
	    }
	    User user = uDao.findByName(userName);
	    return user;
	}
	
	@RequestMapping(value="/upsert", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void upsertUser(@RequestBody User user){
		String password = "";
		if (user.getUserName() != null) {
		    User oU = uDao.findByName(user.getUserName().toUpperCase());
			if (oU.getId() != null) {
				user.setId(oU.getId());
				password = oU.getPassword();
			}
			if (null != user.getPassword() && !user.getPassword().equals(password) && user.getPassword().length() > 0) {
				user.setPassword(EncryptHelper.encrypt(user.getPassword()));
			}
			uDao.merge(user);
		}
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void deleteUser(@RequestBody Long id){
		uDao.remove(id);
	}

    @RequestMapping(value = "/active", method = RequestMethod.POST)
    @ResponseBody
    public SessionInfo active(HttpServletRequest req) {
    	SessionInfo rlt = new SessionInfo();
    	// Get the client SSL certificates associated with the request
		X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
		// Check that a certificate was obtained
		if (certs.length < 1) {
			rlt.setError("SSL not client authenticated");
		}
		// The base of the certificate chain contains the client's info
		X509Certificate principalCert = certs[0];

		// Get the Distinguished Name from the certificate
		// CN = I063098	O = SAP-AG	C = DE
		Principal principal = principalCert.getSubjectDN();

		// Extract the common name (CN)
		int start = principal.getName().indexOf("CN");
		String tmpName, name = "";
		if (start > -1) {
			tmpName = principal.getName().substring(start + 3);
			int end = tmpName.indexOf(",");
			if (end > 0) {
				name = tmpName.substring(0, end);
			} else {
				name = tmpName;
			}
		}
		User usr = uDao.findByName(name);
		String usrId = null;
		String usrFullName = "";
		String usrRole = "";
		if (usr != null && usr.getStatus()) {
    		usrId = name;
    		usrFullName = usr.getFullName();
    		usrRole = usr.getRole();
    		rlt.setUserFullName(usrFullName != null && usrFullName.length() > 0 ? 
    				name + " (" + usrFullName + ")" : name);
    		rlt.setCurrentUser(usrId);
    		rlt.setRole(usrRole);
    	}
        req.getSession().setAttribute(SessionHolder.USER_ID, usrId);
        req.getSession().setAttribute(SessionHolder.USER_FULLNAME, usrFullName);
        req.getSession().setAttribute(SessionHolder.USER_ROLE, usrRole);

        SessionHolder.setContext(usrId, usrFullName, usrRole);
        
        return rlt;
    }

}
