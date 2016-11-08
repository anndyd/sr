package com.sap.it.sr.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.sap.it.sr.entity.User;
import com.sap.it.sr.util.SessionHolder;

@Controller
@RequestMapping("user")
@Scope("request")
public class UserController {

    @Autowired
    private UserDao uDao;

	@RequestMapping(value="/all", method = RequestMethod.GET)
	@ResponseBody
	public List<User> getUsers(){
		return uDao.findAll();
	}

	@RequestMapping(value="/get", method = RequestMethod.GET)
	@ResponseBody
	public User getUser(@RequestParam(required = true) String userName){
		return uDao.findByName(userName);
	}
	
	@RequestMapping(value="/upsert", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void upsertUser(@RequestBody User user){
		User oU = uDao.findByName(user.getUserName());
		if (oU.getUserName() != null) {
			user.setId(oU.getId());
		}
        uDao.merge(user);
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void deleteUser(@RequestBody Long id){
		uDao.remove(id);
	}

    @RequestMapping(value = "/active", method = RequestMethod.POST)
    @ResponseBody
    public String active(HttpServletRequest request, @RequestParam String userName, @RequestParam String password) {
    	User usr = uDao.findByName(userName);
    	String usrId = null;
    	String rlt = "login.html";
//    	if (usr != null && usr.getPassword() == password) {
    		usrId = userName;
    		rlt = "index.html";
//    	}
        request.getSession().setAttribute(SessionHolder.USER_ID, usrId);

        SessionHolder.setContext(usrId);
        return rlt;

    }

}
