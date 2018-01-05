package com.sap.it.sr.controller;


import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
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
	private static final Logger LOGGER = Logger.getLogger(UserController.class);
	
	@Autowired(required=true)
	private HttpServletRequest request;	
    
	@Autowired
    private UserDao uDao;

	@RequestMapping(value="/getSize", method = RequestMethod.GET)
	@ResponseBody
	public long getSize(){
	    Object rlt = uDao.createNativeQuery("select count(id) from user").getSingleResult();
		return (long) rlt;
	}

	@RequestMapping(value="/all", method = RequestMethod.GET)
	@ResponseBody
	public List<User> getUsers(@RequestParam(required = true) int start, @RequestParam(required = true) int max){
		List<User> users = uDao.findAll("role,userName", start, max);
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
	public void upsertUser(@RequestBody User user){
		String password = "";
		String fullName = "";
		String location = "";
		if (user.getUserName() != null) {
			HttpSession session = request.getSession();
		    String role = session.getAttribute(SessionHolder.USER_ROLE).toString();
		    String currentUser = session.getAttribute(SessionHolder.USER_ID).toString();
		    
		    if (null != role && (role.equals("1") || role.equals("2"))) {
			    User oU = uDao.findByName(user.getUserName().toUpperCase());
				if (oU.getId() != null) {
					user.setId(oU.getId());
					password = oU.getPassword();
					
					fullName = user.getFullName();
					location = user.getPickLocation();
					// role 2 only can modify full name and location
					if (role.equals("2") && currentUser.equals(user.getUserName())) {
						try {
							BeanUtils.copyProperties(user, oU);
						} catch (Exception e) {
							LOGGER.info(e);
						}
						user.setFullName(fullName);
						user.setPickLocation(location);
					} else if (role.equals("2")) {
						return;
					}
				} else if (role.equals("2")) {
					// role 2 can't add user.
					return;
				}
		    	if (currentUser.equals(user.getUserName())) {
					if (null != user.getPassword() && !user.getPassword().equals(password) && user.getPassword().length() > 0) {
						user.setPassword(EncryptHelper.encrypt(user.getPassword()));
					}
		    	} else {
		    		// if it is not self, use old value.
		    		user.setPassword(password);
		    	}
				uDao.merge(user);
		    }
		}
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	@ResponseBody
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
    		rlt.setChargeCC(usr.getChargeCC());
    	}
        req.getSession().setAttribute(SessionHolder.USER_ID, usrId);
        req.getSession().setAttribute(SessionHolder.USER_FULLNAME, usrFullName);
        req.getSession().setAttribute(SessionHolder.USER_ROLE, usrRole);

        SessionHolder.setContext(usrId, usrFullName, usrRole);
        
        return rlt;
    }

}
