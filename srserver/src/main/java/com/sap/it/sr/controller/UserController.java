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
	
	@RequestMapping(value="/add", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public User addUser(@RequestBody User user){
		User oU = new User();
		oU.setUserName(user.getUserName());
		oU.setPassword(user.getPassword());
		oU.setRole(user.getRole());
        return uDao.create(oU);
	}
	
	@RequestMapping(value="/update", method = RequestMethod.PUT)
	@ResponseBody
	@Transactional
	public User updateUser(@RequestBody User user){
        return uDao.merge(user);
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void deleteUser(@RequestBody Long id){
		uDao.remove(id);
	}

    @RequestMapping(value = "/active", method = RequestMethod.POST)
    @ResponseBody
    public boolean active(HttpServletRequest request, @RequestParam String userName, @RequestParam String password) {

        request.getSession().setAttribute(SessionHolder.USER_ID, userName);

        SessionHolder.setContext(userName);
        return true;
    }

}
